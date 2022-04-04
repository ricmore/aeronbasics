package com.github.ricmore.aeronbasics.plain.starters;

import java.util.concurrent.TimeUnit;

import com.github.ricmore.aeronbasics.plain.publication.Publisher;
import com.github.ricmore.aeronbasics.plain.subscription.Subscriber;
import com.github.ricmore.aeronbasics.plain.util.ConnectionCheck;

import org.agrona.CloseHelper;
import org.agrona.concurrent.SigInt;

import io.aeron.driver.MediaDriver;

public class AeronStarterSingleEmbeddedMediaDriver {

  public static void main(String[] args) throws InterruptedException {
    final MediaDriver driver = MediaDriver.launchEmbedded();

    final Subscriber subscriber = new Subscriber(driver.aeronDirectoryName());
    final Publisher publisher = Publisher.startExternalMediaDriverUsingDir(driver.aeronDirectoryName());

    subscriber.setup();

    System.out.println("Connected after setup? Subscriber: " + subscriber.isConnected() + ", Publisher: " + publisher.isConnected());
    new ConnectionCheck().waitTillConnected(publisher::isConnected, "publisher");
    new ConnectionCheck().waitTillConnected(subscriber::isConnected, "subscriber");
    System.out.println("Connected now? Subscriber: " + subscriber.isConnected() + ", Publisher: " + publisher.isConnected());

    Runnable closeResources = () -> {
      System.out.println("Closing all resources");
      CloseHelper.closeAll(subscriber, publisher, driver);
    };
    Thread subscriberThread = new Thread(subscriber::listen, "subscriber-thread");
    Thread publisherThread = new Thread(() -> {
      publisher.sendMessages();
      closeResources.run();
    }, "publisher-thread");

    subscriberThread.start();
    TimeUnit.SECONDS.sleep(1);
    publisherThread.start();

    SigInt.register(closeResources);
  }

}
