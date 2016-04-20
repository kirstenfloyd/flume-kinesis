package com.amazonaws.services.kinesis.flume;

/**
 * Contains configuration constants used by Kinesis and Firehose sources/sinks
 */
final class ConfigurationConstants {
  static final int DEFAULT_BATCH_SIZE = 100;

  static final int DEFAULT_MAX_ATTEMPTS = 100;

  static final boolean DEFAULT_ROLLBACK_AFTER_MAX_ATTEMPTS = false;

  static final long BACKOFF_TIME_IN_MILLIS = 100L;

  static final boolean DEFAULT_PARTITION_KEY_FROM_EVENT = false;

  static final String DEFAULT_KINESIS_ENDPOINT = "https://kinesis.us-east-1.amazonaws.com";

  static final String DEFAULT_FIREHOSE_ENDPOINT = "https://firehose.us-east-1.amazonaws.com";

  private ConfigurationConstants() {
    // Disable object creation
  }
}
