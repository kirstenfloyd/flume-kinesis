package com.amazonaws.services.kinesis.flume;

/*
 * Copyright 2012-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * Modifications: Copyright 2015 Sharethrough, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

import java.nio.ByteBuffer;
import java.util.List;
import java.util.ArrayList;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.kinesis.MyAwsCredentials;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.apache.flume.instrumentation.SinkCounter;

import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehoseClient;
import com.amazonaws.services.kinesisfirehose.model.PutRecordBatchRequest;
import com.amazonaws.services.kinesisfirehose.model.Record;
import com.amazonaws.services.kinesisfirehose.model.PutRecordBatchResult;
import com.amazonaws.services.kinesisfirehose.model.PutRecordBatchResponseEntry;
import org.rightnow.logging.transformers.EventTransformer;

public class FirehoseSink extends AbstractSink implements Configurable {
  private static final Log LOG = LogFactory.getLog(FirehoseSink.class);

  private SinkCounter sinkCounter;

  private static AmazonKinesisFirehoseClient kinesisClient;
  private String streamName;
  private String endpoint;
  private int batchSize;
  private int maxAttempts;
  private boolean rollbackAfterMaxAttempts;
  private AWSCredentialsProvider credentialsProvider;

  @Override
  public void configure(Context context) {
    this.endpoint = context.getString("endpoint", ConfigurationConstants.DEFAULT_FIREHOSE_ENDPOINT);

    String accessKeyId = context.getString("accessKeyId");

    String secretAccessKey = context.getString("secretAccessKey");

    if (accessKeyId != null && secretAccessKey != null) {
      credentialsProvider = new MyAwsCredentials(accessKeyId, secretAccessKey);
    } else {
      credentialsProvider = new DefaultAWSCredentialsProviderChain();
    }

    this.streamName = Preconditions.checkNotNull(
      context.getString("streamName"), "streamName is required");

    this.batchSize = context.getInteger("batchSize", ConfigurationConstants.DEFAULT_BATCH_SIZE);
    Preconditions.checkArgument(batchSize > 0 && batchSize <= 500,
      "batchSize must be between 1 and 500");

    this.maxAttempts = context.getInteger("maxAttempts", ConfigurationConstants.DEFAULT_MAX_ATTEMPTS);
    Preconditions.checkArgument(maxAttempts > 0, "maxAttempts must be greater than 0");

    this.rollbackAfterMaxAttempts = context.getBoolean("rollbackAfterMaxAttempts", ConfigurationConstants.DEFAULT_ROLLBACK_AFTER_MAX_ATTEMPTS);

    if (sinkCounter == null) {
      sinkCounter = new SinkCounter(getName());
    }
  }

  @Override
  public void start() {
    kinesisClient = new AmazonKinesisFirehoseClient(credentialsProvider);
    kinesisClient.setEndpoint(this.endpoint);
    sinkCounter.start();
  }

  @Override
  public Status process() throws EventDeliveryException {
    Status status = null;
    //Get the channel associated with this Sink
    Channel ch = getChannel();
    Transaction txn = ch.getTransaction();
    List<Record> recordList = Lists.newArrayList();
    //Start the transaction
    txn.begin();
    try {
      int txnEventCount;
      int attemptCount = 1;
      int failedTxnEventCount = 0;

      for (txnEventCount = 0; txnEventCount < batchSize; txnEventCount++) {
        //Take an event from the channel
        Event event = ch.take();
        if (event == null) {
          break;
        }
        Record entry = new Record();

        EventTransformer transformer = new EventTransformer();
        String json = transformer.getJson(event.getBody());
        entry.setData(ByteBuffer.wrap(json.getBytes()));

        recordList.add(entry);
      }

      if (txnEventCount > 0) {
        if (txnEventCount == batchSize) {
          sinkCounter.incrementBatchCompleteCount();

        } else {
          sinkCounter.incrementBatchUnderflowCount();
        }

        PutRecordBatchRequest putRecordsRequest = new PutRecordBatchRequest();
        putRecordsRequest.setDeliveryStreamName(this.streamName);
        putRecordsRequest.setRecords(recordList);

        sinkCounter.addToEventDrainAttemptCount(putRecordsRequest.getRecords().size());
        PutRecordBatchResult putRecordsResult = kinesisClient.putRecordBatch(putRecordsRequest);

        while (putRecordsResult.getFailedPutCount() > 0 && attemptCount < maxAttempts) {
          LOG.warn("Failed to sink " + putRecordsResult.getFailedPutCount() + " records on attempt " + attemptCount + " of " + maxAttempts);

          try {
            Thread.sleep(ConfigurationConstants.BACKOFF_TIME_IN_MILLIS);
          } catch (InterruptedException e) {
            LOG.debug("Interrupted sleep", e);
          }

          List<Record> failedRecordsList = getFailedRecordsFromResult(putRecordsResult, recordList);
          putRecordsRequest.setRecords(failedRecordsList);
          putRecordsResult = kinesisClient.putRecordBatch(putRecordsRequest);
          attemptCount++;
        }

        if (putRecordsResult.getFailedPutCount() > 0) {
          LOG.error("Failed to sink " + putRecordsResult.getFailedPutCount() + " records after " + attemptCount + " out of " + maxAttempts + " attempt(s)");
          sinkCounter.incrementConnectionFailedCount();
        }

        failedTxnEventCount = putRecordsResult.getFailedPutCount();
        int successfulTxnEventCount = txnEventCount - failedTxnEventCount;
        sinkCounter.addToEventDrainSuccessCount(successfulTxnEventCount);
      } else {
        sinkCounter.incrementBatchEmptyCount();
      }

      if (failedTxnEventCount > 0 && this.rollbackAfterMaxAttempts) {
        txn.rollback();
        LOG.error("Failed to commit transaction after max attempts reached. Transaction rolled back.");
        status = Status.BACKOFF;
      } else {
        txn.commit();
        status = txnEventCount == 0 ? Status.BACKOFF : Status.READY;
      }
    } catch (Throwable t) {
      txn.rollback();
      LOG.error("Failed to commit transaction. Transaction rolled back.", t);
      status = Status.BACKOFF;
      if (t instanceof Error) {
        throw (Error)t;
      } else {
        throw new EventDeliveryException(t);
      }
    } finally {
      txn.close();
    }
    return status;
  }

  private List<Record> getFailedRecordsFromResult(PutRecordBatchResult putRecordsResult, List<Record> recordList) {
    List<Record> failedRecordsList = new ArrayList<>();
    List<PutRecordBatchResponseEntry> putRecordsResultEntryList = putRecordsResult.getRequestResponses();

    for (int i = 0; i < putRecordsResultEntryList.size(); i++) {
      Record putRecordRequestEntry = recordList.get(i);
      PutRecordBatchResponseEntry putRecordsResultEntry = putRecordsResultEntryList.get(i);
      if (putRecordsResultEntry.getErrorCode() != null) {
        failedRecordsList.add(putRecordRequestEntry);
      }
    }

    return failedRecordsList;
  }
}
