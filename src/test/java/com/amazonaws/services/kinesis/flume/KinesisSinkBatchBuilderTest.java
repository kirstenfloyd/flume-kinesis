package com.amazonaws.services.kinesis.flume;

import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import org.apache.flume.Channel;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class KinesisSinkBatchBuilderTest {
    private Event mockEvent = EventBuilder.withBody(
      "dummy event", Charset.forName("UTF-8"));

    @Test
    public void testWithNullChannel() {
      KinesisSinkBatchBuilder batchBuilder = new KinesisSinkBatchBuilder(0, false);
      List<PutRecordsRequestEntry> batch = batchBuilder.buildBatch(null);

      assertEquals("return no items when channel was null", 0, batch.size());
    }

    @Test
    public void testZeroBatchSize() {
      Channel mockChannel = mock(Channel.class);

      KinesisSinkBatchBuilder batchBuilder = new KinesisSinkBatchBuilder(0, false);
      List<PutRecordsRequestEntry> batch = batchBuilder.buildBatch(mockChannel);

      assertEquals("return no items with batch size of 0", 0, batch.size());
      verify(mockChannel, times(0)).take();
    }

    @Test
    public void testGetsBatchSizeItems() {
      Channel mockChannel = mock(Channel.class);
      when(mockChannel.take()).thenReturn(mockEvent);

      KinesisSinkBatchBuilder batchBuilder = new KinesisSinkBatchBuilder(2, false);
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

      KinesisSinkBatchBuilder batchBuilder = new KinesisSinkBatchBuilder(2, false);
      List<PutRecordsRequestEntry> batch = batchBuilder.buildBatch(mockChannel);

      assertEquals("returns availble items when batch size is higher",
        1, batch.size());
      verify(mockChannel, times(2)).take();
    }
}
