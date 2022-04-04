package com.github.ricmore.aeronbasics.plain.publication;

import org.agrona.CloseHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.aeron.driver.MediaDriver;

public class PublisherWithMediaDriver extends Publisher implements AutoCloseable {

  private final static Logger logger = LogManager.getLogger(PublisherWithMediaDriver.class);

  private MediaDriver innerMediaDriver;

  private PublisherWithMediaDriver() {
    super();
  }

  public static PublisherWithMediaDriver startWithDefaultAeronDir() {
    final PublisherWithMediaDriver publisher = new PublisherWithMediaDriver();
    publisher.startInnerMediaDriver();
    publisher.setup();
    return publisher;
  }

  public void startInnerMediaDriver() {
    MediaDriver.Context context = new MediaDriver.Context();
    context.dirDeleteOnStart(true).aeronDirectoryName("data/aeron");
    innerMediaDriver = MediaDriver.launchEmbedded(context);
    logger.info("Publisher - Launched Media Driver in {}", innerMediaDriver.aeronDirectoryName());
    super.setAeronDirectory(innerMediaDriver.aeronDirectoryName());
  }

  @Override
  public void close() {
    super.close();
    CloseHelper.closeAll(this.innerMediaDriver);
  }

}
