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
  //kinesis limits
  static int DEFAULT_MAX_BATCH_SIZE = 500;
  static int DEFAULT_MAX_BATCH_BYTE_SIZE = 5000000;
  static int DEFAULT_MAX_EVENT_SIZE = 1000000;

  private int batchSize;
  private int maxBatchByteSize;
  private int maxEventSize;
  private boolean usePartitionKeyFromEvent;
  private Event carryOver;

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

  List<PutRecordsRequestEntry> buildBatch(Channel ch) {
    if (ch == null) {
      LOG.debug("Channel was null");
      return Collections.emptyList();
    }

    List<PutRecordsRequestEntry> putRecordsRequestEntryList = Lists.newArrayList();
    int currBatchByteSize = 0;

    while (putRecordsRequestEntryList.size() < batchSize) {
      Event event = getNextEvent(ch);

      if (event == null) {
        break;
      }

      int currEventSize = event.getBody().length;

      if (isOverEventSizeLimit(currEventSize)) {
        //drop message
        continue;
      }

      if (isWithinSizeLimit(currEventSize, currBatchByteSize)) {
        PutRecordsRequestEntry entry = buildRequestEntry(event);
        putRecordsRequestEntryList.add(entry);
        currBatchByteSize += currEventSize;
      } else {
        carryOver = event;
        break;
      }
    }

    return putRecordsRequestEntryList;
  }

  private Event getNextEvent(Channel ch) {
    Event event;
    if (carryOver != null) {
      event = carryOver;
      carryOver = null;
    } else {
      //Take an event from the channel
      event = ch.take();
    }
    return event;
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
