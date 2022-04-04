package com.github.ricmore.aeronbasics.plain.starters;

import java.util.concurrent.TimeUnit;

import com.github.ricmore.aeronbasics.plain.publication.Publisher;
import com.github.ricmore.aeronbasics.plain.subscription.Subscriber;

import org.agrona.CloseHelper;
import org.agrona.concurrent.SigInt;

import io.aeron.driver.MediaDriver;

public class AeronStarter2 {

  public static void main(String[] args) throws InterruptedException {
    final MediaDriver driver = MediaDriver.launchEmbedded();

    final Subscriber subscriber = new Subscriber(driver.aeronDirectoryName());
    final Publisher publisher = Publisher.startExternalMediaDriverUsingDir(driver.aeronDirectoryName());

    Runnable closeResources = () -> {
      System.out.println("Closing all resources");
      CloseHelper.closeAll(subscriber, publisher, driver);
    };
    Thread subscriberThread = new Thread(() -> {
      subscriber.setup();
      subscriber.listen();
    }, "subscriber-thread");
    Thread publisherThread = new Thread(() -> {
      publisher.setup();
      publisher.sendMessages();
      closeResources.run();
    }, "publisher-thread");

    subscriberThread.start();
    TimeUnit.SECONDS.sleep(10);
    publisherThread.start();

    SigInt.register(closeResources);
  }

}
