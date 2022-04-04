package com.github.ricmore.aeronbasics.plain.subscription;

import org.agrona.CloseHelper;

import io.aeron.driver.MediaDriver;

public class SubscriberWithMediaDriver extends Subscriber implements AutoCloseable {

  private MediaDriver innerMediaDriver;

  private SubscriberWithMediaDriver() {
    super(null);
  }

  public static SubscriberWithMediaDriver startWithDefaultAeronDir() {
    final SubscriberWithMediaDriver subscriber = new SubscriberWithMediaDriver();
    subscriber.startInnerMediaDriver();
    subscriber.setup();
    return subscriber;
  }

  public void startInnerMediaDriver() {
    MediaDriver.Context context = new MediaDriver.Context();
    context.dirDeleteOnStart(true).aeronDirectoryName("data/aeron");
    innerMediaDriver = MediaDriver.launchEmbedded(context);
    super.setAeronDirectory(innerMediaDriver.aeronDirectoryName());
    logger.info("Subscriber: Started Media Driver in {}", innerMediaDriver.aeronDirectoryName());
  }

  @Override
  public void close() {
    logger.info("Closing subscriber's media driver");
    super.close();
    CloseHelper.closeAll(this.innerMediaDriver);
  }

}
