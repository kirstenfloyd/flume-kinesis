package com.amazonaws.services.kinesis.flume;

import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.flume.Channel;
import org.apache.flume.Event;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;

public class KinesisSinkBatchBuilder {
  private static final Log LOG = LogFactory.getLog(KinesisSinkBatchBuilder.class);

  private int batchSize;
  private int maxBatchByteSize;
  private int maxEventSize;
  private boolean usePartitionKeyFromEvent;

  public KinesisSinkBatchBuilder(
    int batchSize,
    int maxBatchByteSize,
    int maxEventSize,
    boolean usePartitionKeyFromEvent
  ) {
    this.batchSize = batchSize;
    this.usePartitionKeyFromEvent = usePartitionKeyFromEvent;
    this.maxBatchByteSize = maxBatchByteSize;
    this.maxEventSize = maxEventSize;
  }

  KinesisRecordsBatch buildBatch(Channel ch) {
    if (ch == null) {
      LOG.error("Channel was null");
      return KinesisRecordsBatch.EMPTY_RECORDS;
    }

    List<PutRecordsRequestEntry> putRecordsRequestEntryList = Lists.newArrayList();
    PutRecordsRequestEntry spillOverRecord = null;
    int currBatchByteSize = 0;


    while (putRecordsRequestEntryList.size() < batchSize) {
      //Take an event from the channel
      Event event = ch.take();

      if (event == null) {
        break;
      }

      int currEventSize = event.getBody().length;

      if (isOverEventSizeLimit(currEventSize)) {
        LOG.warn(String.format(
          "Dropping an event of size %s, max event size is %s bytes", currEventSize, maxEventSize));
        continue;
      }

      PutRecordsRequestEntry entry = buildRequestEntry(event);
      if (isWithinSizeLimit(currEventSize, currBatchByteSize)) {
        putRecordsRequestEntryList.add(entry);
        currBatchByteSize += currEventSize;
      } else {
        //The event is taken out of the channel but does not fit within this batch
        spillOverRecord = entry;
        break;
      }
    }

    return new KinesisRecordsBatch(putRecordsRequestEntryList, spillOverRecord);
  }

  private boolean isWithinSizeLimit(int currEventSize, int currBatchByteSize) {
    return currBatchByteSize + currEventSize <= maxBatchByteSize;
  }

  private boolean isOverEventSizeLimit(int eventSize) {
    return eventSize > maxEventSize;
  }

  private PutRecordsRequestEntry buildRequestEntry(Event event) {
    String partitionKey;
    if (usePartitionKeyFromEvent && event.getHeaders().containsKey("key")) {
      partitionKey = event.getHeaders().get("key");
    } else {
      partitionKey = "pk_" + new Random().nextInt(Integer.MAX_VALUE);
    }
    LOG.debug("partitionKey: " + partitionKey);
    PutRecordsRequestEntry entry = new PutRecordsRequestEntry();
    entry.setData(ByteBuffer.wrap(event.getBody()));
    entry.setPartitionKey(partitionKey);
    return entry;
  }
}
