package com.github.ricmore.aeronbasics.plain;

public class PlainAeronConfig {

  public static final String HOST_PROP = "aeron.sample.host";
  public static final String STREAM_ID_PROP = "aeron.sample.streamId";

  public static final String FRAME_COUNT_LIMIT_PROP = "aeron.sample.frameCountLimit";

  public static final String HOST;
  public static final String CHANNEL;
  public static final int STREAM_ID;
  public static final int FRAGMENT_COUNT_LIMIT;


  static {
    HOST = System.getProperty(HOST_PROP, "localhost");
    CHANNEL = "aeron:udp?endpoint=" + HOST + ":20121";
    STREAM_ID = Integer.getInteger(STREAM_ID_PROP, 1001);

    FRAGMENT_COUNT_LIMIT = Integer.getInteger(FRAME_COUNT_LIMIT_PROP, 10);
  }
}
