# flume-kinesis

Amazon Kinesis Source and Sink for Apache Flume


Forked from https://github.com/sharethrough/flume-kinesis (which was originally forked from: https://github.com/pdeyhim/flume-kinesis). Changes include:
* Updated for latest versions of flume.
* Uses newer AWS libraries
* __Sink:__ Added option to get partition key from event headers
* __Sink:__ Default partition logic will partition randomly across shards (updated the logic for choosing a partition key to more evenly distribute data across shards)

## Building and installation

```
mvn compile assembly:single
cp target/*.jar FLUME_HOME_DIR/lib
```

## Configuration

Check the examples under `conf/` for specific examples.  All values without defaults are required.

### Kinesis Source Options

|Name|Default|Description|
-------|-----------|-------------|
|endpoint|https://kinesis.us-east-1.amazonaws.com|endpoint to access kinesis|
|accessKeyId|null|AWS Access Key ID|
|secretAccessKey|null|AWS Secret Access Key|
|streamName|null|name of Kinesis stream|
|applicationName|null|name of Kinesis application|
|initialPosition|TRIM_HORIZON|strategy to set the initial iterator position|

### Kinesis Sink Options

|Name|Default|Description|
-------|-----------|-------------|
|endpoint|https://kinesis.us-east-1.amazonaws.com|endpoint to access kinesis|
|accessKeyId|null|AWS Access Key ID|
|secretAccessKey|null|AWS Secret Access Key|
|streamName|null|name of Kinesis stream|
|numberOfPartitions|1|number of Kinesis partitions.  Set this much higher than actual number of shards to get better uniforimity when sinking across shards.|
|batchSize|100|max number of events to send per API call to Kinesis.  Must be between 1 and 500.|
|maxAttempts|100|max number of times to attempt to send events.  After this the batch will be considered failed.  Must be >= 1.|
|rollbackAfterMaxAttempts|false|whether to roll back the flume transaction if events cannot be sent after max attempts|
|partitionKeyFromEvent|false|When set to true, instead of randomly generating a partition key for each event, will instead use the "key" that is set in the event headers.|
