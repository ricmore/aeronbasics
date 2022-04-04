package com.github.ricmore.aeronbasics.plain.util;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectionCheck {

  private final static Logger logger = LogManager.getLogger(ConnectionCheck.class);

  public void waitTillConnected(final BooleanSupplier connectedCheck, final String name) {
    long now = System.currentTimeMillis();
    while (!connectedCheck.getAsBoolean() && System.currentTimeMillis() - now < 10_000) {
      try {
        TimeUnit.MILLISECONDS.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    if (!connectedCheck.getAsBoolean()) {
      logger.error("Can not connect {}!! Something really wrong must be happening.", name);
    }
  }

}
