package com.amazonaws.services.kinesis.flume;

import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import org.apache.flume.Channel;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.List;

import static com.amazonaws.services.kinesis.flume.KinesisSinkBatchBuilder.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class KinesisSinkBatchBuilderTest {
    private String eventPayload = "dummy event payload";
    private Event mockEvent = EventBuilder.withBody(
      eventPayload, Charset.forName("UTF-8"));

    @Test
    public void testWithNullChannel() {
      KinesisSinkBatchBuilder batchBuilder = new KinesisSinkBatchBuilder(
        0, DEFAULT_MAX_BATCH_BYTE_SIZE, DEFAULT_MAX_EVENT_SIZE, false);
      List<PutRecordsRequestEntry> batch = batchBuilder.buildBatch(null);

      assertEquals("return no items when channel was null", 0, batch.size());
    }

    @Test
    public void testZeroBatchSize() {
      Channel mockChannel = mock(Channel.class);

      KinesisSinkBatchBuilder batchBuilder = new KinesisSinkBatchBuilder(
        0, DEFAULT_MAX_BATCH_BYTE_SIZE, DEFAULT_MAX_EVENT_SIZE, false);
      List<PutRecordsRequestEntry> batch = batchBuilder.buildBatch(mockChannel);

      assertEquals("return no items with batch size of 0", 0, batch.size());
      verify(mockChannel, times(0)).take();
    }

    @Test
    public void testGetsBatchSizeItems() {
      Channel mockChannel = mock(Channel.class);
      when(mockChannel.take()).thenReturn(mockEvent);

      KinesisSinkBatchBuilder batchBuilder = new KinesisSinkBatchBuilder(
      2, DEFAULT_MAX_BATCH_BYTE_SIZE, DEFAULT_MAX_EVENT_SIZE, false);
      List<PutRecordsRequestEntry> batch = batchBuilder.buildBatch(mockChannel);

      assertEquals("returns batch size number of items when available",
        2, batch.size());
      verify(mockChannel, times(2)).take();
    }

    @Test
    public void testGetsLessThanBatchSize() {
      Channel mockChannel = mock(Channel.class);
      when(mockChannel.take())
          .thenReturn(mockEvent)
          .thenReturn(null);

      KinesisSinkBatchBuilder batchBuilder = new KinesisSinkBatchBuilder(
        2, DEFAULT_MAX_BATCH_BYTE_SIZE, DEFAULT_MAX_EVENT_SIZE, false);
      List<PutRecordsRequestEntry> batch = batchBuilder.buildBatch(mockChannel);

      assertEquals("returns availble items when batch size is higher",
        1, batch.size());
      verify(mockChannel, times(2)).take();
    }

  @Test
  public void testZeroBatchByteSize() {
    Channel mockChannel = mock(Channel.class);

    KinesisSinkBatchBuilder batchBuilder = new KinesisSinkBatchBuilder(
            KinesisSinkBatchBuilder.DEFAULT_MAX_BATCH_SIZE, 0, DEFAULT_MAX_EVENT_SIZE, false);
    List<PutRecordsRequestEntry> batch = batchBuilder.buildBatch(mockChannel);

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
            KinesisSinkBatchBuilder.DEFAULT_MAX_BATCH_SIZE, maxBatchByteSize, DEFAULT_MAX_EVENT_SIZE, false);
    List<PutRecordsRequestEntry> batch = batchBuilder.buildBatch(mockChannel);

    assertEquals("returns items with batch size = maxBatchByteSize",
            2, batch.size());
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
            DEFAULT_MAX_BATCH_SIZE, maxBatchByteSize, DEFAULT_MAX_EVENT_SIZE, false);
    List<PutRecordsRequestEntry> batch = batchBuilder.buildBatch(mockChannel);

    assertEquals("returns items with batch size = maxBatchByteSize",
            1, batch.size());
    verify(mockChannel, times(2)).take();
  }

  @Test
  public void testGetsCarryOverEventInNextCall() {
    Channel mockChannel = mock(Channel.class);
    when(mockChannel.take())
            .thenReturn(mockEvent)
            .thenReturn(mockEvent)
            .thenReturn(mockEvent)
            .thenReturn(mockEvent)
            .thenReturn(null);

    int maxBatchByteSize = eventPayload.length() * 2;
    KinesisSinkBatchBuilder batchBuilder = new KinesisSinkBatchBuilder(
            DEFAULT_MAX_BATCH_SIZE, maxBatchByteSize, DEFAULT_MAX_EVENT_SIZE, false);

    List<PutRecordsRequestEntry> batch = batchBuilder.buildBatch(mockChannel);
    assertEquals("returns items with batch size = maxBatchByteSize",
            2, batch.size());
    verify(mockChannel, times(3)).take();

    batch = batchBuilder.buildBatch(mockChannel);
    assertEquals("returns items with batch size = maxBatchByteSize",
            2, batch.size());
    verify(mockChannel, times(5)).take();
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
            2, DEFAULT_MAX_BATCH_BYTE_SIZE, maxEventSize, false);
    List<PutRecordsRequestEntry> batch = batchBuilder.buildBatch(mockChannel);

    assertEquals("returns availble items when batch size is higher",
            1, batch.size());
    verify(mockChannel, times(3)).take();
  }

}
