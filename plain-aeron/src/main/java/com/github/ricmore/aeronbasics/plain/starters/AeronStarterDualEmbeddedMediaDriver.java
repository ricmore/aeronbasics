package com.github.ricmore.aeronbasics.plain.starters;

public class AeronStarterDualEmbeddedMediaDriver {

  public static void main(String[] args) throws InterruptedException {
    SubscriptionStarter.main(args);
    PublisherStarter.main(args);
  }

}
