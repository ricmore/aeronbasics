package com.github.ricmore.aeronbasics.plain.starters;

import java.util.ArrayList;
import java.util.List;

import com.github.ricmore.aeronbasics.plain.subscription.SubscriberWithMediaDriver;

import org.agrona.CloseHelper;
import org.agrona.concurrent.SigInt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SubscriptionStarter {

  private final static Logger logger = LogManager.getLogger(SubscriptionStarter.class);

  public static void main(String[] args) {
    final List<AutoCloseable> toBeClosed = new ArrayList<>(1);
    final Thread subscriptionThread = new Thread(
      () -> {
        logger.info("Starting publisher");
        final SubscriberWithMediaDriver subscriber = SubscriberWithMediaDriver.startWithDefaultAeronDir();

        toBeClosed.add(subscriber);

        // This will block the thread
        subscriber.listen();
      }, "subscription-thread"
    );

    SigInt.register(() -> {
      logger.info("Closing subscriber in main Thread");
      CloseHelper.closeAll(toBeClosed);
    });

    subscriptionThread.start();
  }
}
