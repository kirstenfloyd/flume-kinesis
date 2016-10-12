package com.amazonaws.services.kinesis.flume;

import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.flume.Channel;
import org.apache.flume.Event;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class KinesisSinkBatchBuilder {
  private static final Log LOG = LogFactory.getLog(KinesisSinkBatchBuilder.class);

  private int batchSize;
  private boolean usePartitionKeyFromEvent;

  public KinesisSinkBatchBuilder(int batchSize, boolean usePartitionKeyFromEvent) {
    this.batchSize = batchSize;
    this.usePartitionKeyFromEvent = usePartitionKeyFromEvent;
  }

  List<PutRecordsRequestEntry> buildBatch(Channel ch) {
    if (ch == null) {
      LOG.debug("Channel was null");
      Collections.emptyList();
    }

    List<PutRecordsRequestEntry> putRecordsRequestEntryList = Lists.newArrayList();
    while (putRecordsRequestEntryList.size() < batchSize) {
      //Take an event from the channel
      Event event = ch.take();
      if (event == null) {
        break;
      }
      PutRecordsRequestEntry entry = buildRequestEntry(event);
      putRecordsRequestEntryList.add(entry);
    }

    return putRecordsRequestEntryList;
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
