package com.github.ricmore.aeronbasics.media;

import org.agrona.CloseHelper;
import org.agrona.concurrent.SigInt;

import io.aeron.archive.ArchivingMediaDriver;

public class ArchiverMediaDriverStarter {

  public static void main(String[] args) {
    ArchivingMediaDriver driver = ArchivingMediaDriver.launch();

    SigInt.register(() -> CloseHelper.close(driver));
  }
}
