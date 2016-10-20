package com.amazonaws.services.kinesis.flume;

import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import org.apache.flume.Channel;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class KinesisSinkBatchBuilderTest {
    private String eventPayload = "dummy event payload";
    private Event mockEvent = EventBuilder.withBody(
      eventPayload, Charset.forName("UTF-8"));

    @Test
    public void testWithNullChannel() {
      KinesisSinkBatchBuilder batchBuilder = new KinesisSinkBatchBuilder(
        0, ConfigurationConstants.MAX_BATCH_BYTE_SIZE, ConfigurationConstants.MAX_EVENT_SIZE, false);
      KinesisRecordsBatch recordsBatch = batchBuilder.buildBatch(null);
      List<PutRecordsRequestEntry> batch = recordsBatch.getBatch();

      assertEquals("return no items when channel was null", 0, batch.size());
    }

    @Test
    public void testZeroBatchSize() {
      Channel mockChannel = mock(Channel.class);

      KinesisSinkBatchBuilder batchBuilder = new KinesisSinkBatchBuilder(
        0, ConfigurationConstants.MAX_BATCH_BYTE_SIZE, ConfigurationConstants.MAX_EVENT_SIZE, false);
      KinesisRecordsBatch recordsBatch = batchBuilder.buildBatch(mockChannel);
      List<PutRecordsRequestEntry> batch = recordsBatch.getBatch();

      assertEquals("return no items with batch size of 0", 0, batch.size());
      verify(mockChannel, times(0)).take();
    }

    @Test
    public void testGetsBatchSizeItems() {
      Channel mockChannel = mock(Channel.class);
      when(mockChannel.take()).thenReturn(mockEvent);

      KinesisSinkBatchBuilder batchBuilder = new KinesisSinkBatchBuilder(
      2, ConfigurationConstants.MAX_BATCH_BYTE_SIZE, ConfigurationConstants.MAX_EVENT_SIZE, false);
      KinesisRecordsBatch recordsBatch = batchBuilder.buildBatch(mockChannel);
      List<PutRecordsRequestEntry> batch = recordsBatch.getBatch();

      assertEquals("returns batch size number of items when available",
        2, batch.size());
      assertFalse(recordsBatch.hasSpillOverEvent());
      verify(mockChannel, times(2)).take();
    }

    @Test
    public void testGetsLessThanBatchSize() {
      Channel mockChannel = mock(Channel.class);
      when(mockChannel.take())
          .thenReturn(mockEvent)
          .thenReturn(null);

      KinesisSinkBatchBuilder batchBuilder = new KinesisSinkBatchBuilder(
        2, ConfigurationConstants.MAX_BATCH_BYTE_SIZE, ConfigurationConstants.MAX_EVENT_SIZE, false);
      KinesisRecordsBatch recordsBatch = batchBuilder.buildBatch(mockChannel);
      List<PutRecordsRequestEntry> batch = recordsBatch.getBatch();

      assertEquals("returns available items when batch size is higher",
        1, batch.size());
      assertFalse(recordsBatch.hasSpillOverEvent());
      verify(mockChannel, times(2)).take();
    }

  @Test
  public void testZeroBatchByteSize() {
    Channel mockChannel = mock(Channel.class);

    KinesisSinkBatchBuilder batchBuilder = new KinesisSinkBatchBuilder(
            ConfigurationConstants.DEFAULT_BATCH_SIZE, 0, ConfigurationConstants.MAX_EVENT_SIZE, false);
    KinesisRecordsBatch recordsBatch = batchBuilder.buildBatch(mockChannel);
    List<PutRecordsRequestEntry> batch = recordsBatch.getBatch();

    assertEquals("return no items with batch byte size of 0", 0, batch.size());
    verify(mockChannel, times(1)).take();
  }

  @Test
  public void testGetsBatchByteSizeItems() {
    Channel mockChannel = mock(Channel.class);
    when(mockChannel.take())
      .thenReturn(mockEvent)
      .thenReturn(mockEvent)
      .thenReturn(mockEvent)
      .thenReturn(mockEvent);

    int maxBatchByteSize = eventPayload.length() * 2;
    KinesisSinkBatchBuilder batchBuilder = new KinesisSinkBatchBuilder(
            ConfigurationConstants.DEFAULT_BATCH_SIZE, maxBatchByteSize, ConfigurationConstants.MAX_EVENT_SIZE, false);
    KinesisRecordsBatch recordsBatch = batchBuilder.buildBatch(mockChannel);
    List<PutRecordsRequestEntry> batch = recordsBatch.getBatch();

    assertEquals("returns items with batch size = maxBatchByteSize",
            2, batch.size());
    assertTrue(recordsBatch.hasSpillOverEvent());
    verify(mockChannel, times(3)).take();
  }

  @Test
  public void testGetsLessThanBatchByteSizeItems() {
    Channel mockChannel = mock(Channel.class);
    when(mockChannel.take())
            .thenReturn(mockEvent)
            .thenReturn(mockEvent)
            .thenReturn(mockEvent);

    int maxBatchByteSize = eventPayload.length() * 2 - 1;
    KinesisSinkBatchBuilder batchBuilder = new KinesisSinkBatchBuilder(
            ConfigurationConstants.DEFAULT_BATCH_SIZE, maxBatchByteSize, ConfigurationConstants.MAX_EVENT_SIZE, false);
    KinesisRecordsBatch recordsBatch = batchBuilder.buildBatch(mockChannel);
    List<PutRecordsRequestEntry> batch = recordsBatch.getBatch();

    assertEquals("returns items with batch size = maxBatchByteSize",
            1, batch.size());
    assertTrue(recordsBatch.hasSpillOverEvent());
    verify(mockChannel, times(2)).take();
  }

  @Test
  public void testDropsOversizedEvents() {
    Channel mockChannel = mock(Channel.class);

    Event mockOversizedEvent = EventBuilder.withBody(
            eventPayload + eventPayload, Charset.forName("UTF-8"));

    when(mockChannel.take())
            .thenReturn(mockEvent)
            .thenReturn(mockOversizedEvent)
            .thenReturn(null);

    int maxEventSize = eventPayload.length();
    KinesisSinkBatchBuilder batchBuilder = new KinesisSinkBatchBuilder(
            2, ConfigurationConstants.MAX_BATCH_BYTE_SIZE, maxEventSize, false);
    KinesisRecordsBatch recordsBatch = batchBuilder.buildBatch(mockChannel);
    List<PutRecordsRequestEntry> batch = recordsBatch.getBatch();

    assertEquals("returns availble items when batch size is higher",
            1, batch.size());
    assertFalse(recordsBatch.hasSpillOverEvent());
    verify(mockChannel, times(3)).take();
  }

}
