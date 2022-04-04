package com.github.ricmore.aeronbasics.plain.publication;

import static com.github.ricmore.aeronbasics.plain.PlainAeronConfig.CHANNEL;
import static com.github.ricmore.aeronbasics.plain.PlainAeronConfig.STREAM_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.agrona.BufferUtil;
import org.agrona.CloseHelper;
import org.agrona.concurrent.UnsafeBuffer;

import io.aeron.Aeron;
import io.aeron.Publication;

public class Publisher implements AutoCloseable {

  private final Aeron.Context context;
  private final List<AutoCloseable> autoCloseables = new ArrayList<>();

  private Publication publication;

  public static Publisher startWithExternalMediaDriverUsingDefaultDir() {
    final Publisher publisher = new Publisher();
    publisher.setup();
    return publisher;
  }

  public static Publisher startExternalMediaDriverUsingDir(final String aeronDirectoryName) {
    final Publisher publisher = new Publisher();
    publisher.setup();
    return publisher;
  }

  protected Publisher() {
    this(null);
  }

  private Publisher(final String aeronDirectoryName) {
    this.context = new Aeron.Context();
    if (aeronDirectoryName != null) {
      System.out.println("Publisher - Aeron directory: " + aeronDirectoryName);
      this.setAeronDirectory(aeronDirectoryName);
    }
  }

  public void setup() {
    Aeron aeron = Aeron.connect(context);
    autoCloseables.add(aeron);
    this.publication = aeron.addPublication(CHANNEL, STREAM_ID);
    autoCloseables.add(publication);
    System.out.println("Publisher: Connected to " + CHANNEL + " stream " + STREAM_ID);
  }

  public boolean isConnected() {
    return this.publication.isConnected();
  }

  public void sendMessages() {
    final UnsafeBuffer buffer = new UnsafeBuffer(BufferUtil.allocateDirectAligned(256, 64));
    for (int i = 0; i < 5; i++) {
      System.out.println("Publisher: Sending message " + i + ". Connected? " + this.publication.isConnected());
      final int length = buffer.putStringWithoutLengthAscii(0, "Hello World!");
      final long result = publication.offer(buffer, 0, length);

      this.handleResult(result);

      try {
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void close() {
    CloseHelper.closeAll(this.autoCloseables);
  }

  private void handleResult(long result) {
    if (result > 0) {
      System.out.println("Published!");
    } else if (result == Publication.BACK_PRESSURED) {
      System.out.println("Offer failed due to back pressure");
    } else if (result == Publication.NOT_CONNECTED) {
      System.out.println("Offer failed because publisher is not connected to a subscriber");
    } else if (result == Publication.ADMIN_ACTION) {
      System.out.println("Offer failed because of an administration action in the system");
    } else if (result == Publication.CLOSED) {
      System.out.println("Offer failed because publication is closed");
    } else if (result == Publication.MAX_POSITION_EXCEEDED) {
      System.out.println("Offer failed due to publication reaching its max position");
    } else {
      System.out.println("Offer failed due to unknown reason: " + result);
    }
  }

  protected void setAeronDirectory(final String directory) {
    this.context.aeronDirectoryName(directory);
  }

}
