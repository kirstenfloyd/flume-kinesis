package com.amazonaws.services.kinesis.flume;

import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;

import java.util.Collections;
import java.util.List;

public class KinesisRecordsBatch {
  private List<PutRecordsRequestEntry> batch;
  private PutRecordsRequestEntry spillOverRecord;
  static KinesisRecordsBatch EMPTY_RECORDS = new KinesisRecordsBatch(
    Collections.<PutRecordsRequestEntry>emptyList(), null);

  public KinesisRecordsBatch(List<PutRecordsRequestEntry> batch, PutRecordsRequestEntry spillOverRecord) {
    this.spillOverRecord = spillOverRecord;
    this.batch = batch;
  }

  public List<PutRecordsRequestEntry> getBatch() {
    return batch;
  }

  public Boolean hasSpillOverEvent() {
    return spillOverRecord != null;
  }

  public PutRecordsRequestEntry getSpillOverRecord() {
    return spillOverRecord;
  }

  public void unSetSpillOverEvent() {
    spillOverRecord = null;
  }
}
