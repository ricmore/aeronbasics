package com.github.ricmore.aeronbasics.media;

import org.agrona.CloseHelper;
import org.agrona.concurrent.SigInt;

import io.aeron.driver.MediaDriver;

public class MediaDriverStarter {

  public static void main(String[] args) {
    MediaDriver driver = MediaDriver.launch();

    SigInt.register(() -> CloseHelper.close(driver));
  }
}
