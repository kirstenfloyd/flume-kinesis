# flume-kinesis

Amazon Kinesis Source and Sink for Apache Flume


Forked from: https://github.com/pdeyhim/flume-kinesis.

## Building and installation

```
mvn clean package
cp target/flume-kinesis-{version}.jar FLUME_HOME_DIR/plugins.d/flume-kinesis/lib
```

## Configuration

Check the examples under `conf/` for specific examples.

### AWS Credentials

The source and sink will attempt to use a [DefaultAWSCredentialsProviderChain](http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/DefaultAWSCredentialsProviderChain.html#DefaultAWSCredentialsProviderChain)
if possible to configure AWS credentials.  These can be specified as one of:

* Environment Variables - AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY
* Java System Properties - aws.accessKeyId and aws.secretKey
* Credential profiles file at the default location (~/.aws/credentials) shared by all AWS SDKs and the AWS CLI
* Instance profile credentials delivered through the Amazon EC2 metadata service

For backward compatibility you can also specify credentials in the source/sink configuration by setting `accessKeyId` and `secretAccessKey`.

### Kinesis Source Options

|Name|Default|Description|
-------|-----------|-------------|
|endpoint|https://kinesis.us-east-1.amazonaws.com|endpoint to access kinesis|
|accessKeyId|null|AWS Access Key ID, deprecated - see AWS Credentials above|
|secretAccessKey|null|AWS Secret Access Key, deprecated - see AWS Credentials above|
|streamName|null|name of Kinesis stream|
|applicationName|null|name of Kinesis application|
|initialPosition|TRIM_HORIZON|strategy to set the initial iterator position|

### Kinesis Sink Options

Sinks for both Kinesis Streams as well as Kinesis Firehose are supported.  To specify the type of stream set the appropriate type in your configuration.

For Kinesis Streams:
```
agent.sinks.kinesis.type = com.amazonaws.services.kinesis.flume.KinesisSink
```

For Kinesis Firehose:
```
agent.sinks.kinesis.type = com.amazonaws.services.kinesis.flume.FirehoseSink
```

Options for both are as follows:

|Name|Default|Description|
-------|-----------|-------------|
|endpoint|https://kinesis.us-east-1.amazonaws.com|endpoint to access kinesis|
|accessKeyId|null|AWS Access Key ID, deprecated - see AWS Credentials above|
|secretAccessKey|null|AWS Secret Access Key, deprecated - see AWS Credentials above|
|streamName|null|name of Kinesis/Firehose stream|
|batchSize|100|max number of events to send per API call to Kinesis.  Must be between 1 and 500.|
|maxAttempts|100|max number of times to attempt to send events.  After this the batch will be considered failed.  Must be >= 1.|
|rollbackAfterMaxAttempts|false|whether to roll back the flume transaction if events cannot be sent after max attempts|
|partitionKeyFromEvent|false|When set to true, instead of randomly generating a partition key for each event, will instead use the "key" that is set in the event headers.|
