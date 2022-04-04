package com.github.ricmore.aeronbasics.plain.starters;

import com.github.ricmore.aeronbasics.plain.publication.PublisherWithMediaDriver;
import com.github.ricmore.aeronbasics.plain.util.ConnectionCheck;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PublisherStarter {

  private final static Logger logger = LogManager.getLogger(PublisherStarter.class);

  public static void main(String[] args) {
    final Thread publisherThread = new Thread(() -> {
      try (final PublisherWithMediaDriver publisher = PublisherWithMediaDriver.startWithDefaultAeronDir()) {

        new ConnectionCheck().waitTillConnected(publisher::isConnected, "publisher");

        logger.info("Publisher: Sending messages");
        publisher.sendMessages();
      }
    }, "publisher-thread");

    publisherThread.start();
  }

}
