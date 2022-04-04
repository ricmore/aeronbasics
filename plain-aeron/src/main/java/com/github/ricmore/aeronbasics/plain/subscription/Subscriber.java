package com.github.ricmore.aeronbasics.plain.subscription;

import static com.github.ricmore.aeronbasics.plain.PlainAeronConfig.CHANNEL;
import static com.github.ricmore.aeronbasics.plain.PlainAeronConfig.STREAM_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.ricmore.aeronbasics.plain.PlainAeronConfig;

import org.agrona.CloseHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.aeron.Aeron;
import io.aeron.Image;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;

public class Subscriber implements AutoCloseable {

  protected final static Logger logger = LogManager.getLogger(Subscriber.class);

  private final Aeron.Context context;
  private final SubscriptionHandlersFactory subscriptionHandlersFactory = new SubscriptionHandlersFactory();
  private final List<AutoCloseable> autoCloseables = new ArrayList<>();
  private final AtomicBoolean running;

  private Subscription subscription;

  public static Subscriber startWithExternalMediaDriverUsingDefaultDir() {
    final Subscriber subscriber = new Subscriber();
    subscriber.setup();
    return subscriber;
  }

  private Subscriber() {
    this(null);
  }

  public Subscriber(String aeronDirectoryName) {
    this.context = new Aeron.Context()
      .unavailableImageHandler(this::printUnavailableImage)
      .availableImageHandler(this::printAvailableImage);
    if (aeronDirectoryName != null) {
      System.out.println("Subscriber - Aeron directory: " + aeronDirectoryName);
      this.setAeronDirectory(aeronDirectoryName);
    }
    this.running = new AtomicBoolean(false);
  }

  public void setup() {
    Aeron aeron = Aeron.connect(context);
    autoCloseables.add(aeron);
    this.subscription = aeron.addSubscription(CHANNEL, STREAM_ID);
    autoCloseables.add(subscription);
    logger.info("Subscriber: Adding subscription to channel {} and stream {}", CHANNEL, STREAM_ID);
  }

  public boolean isConnected() {
    return this.subscription.isConnected();
  }

  public void listen() {
    logger.info("Subscriber: Listening for publications.");
    final FragmentHandler fragmentHandler = this.subscriptionHandlersFactory.asciiMessagePrinter(STREAM_ID);
    this.running.set(true);

    this.subscriptionHandlersFactory.subscriberLoop(fragmentHandler, PlainAeronConfig.FRAGMENT_COUNT_LIMIT, running).accept(this.subscription);
  }

  @Override
  public void close() {
    this.running.set(false);
    CloseHelper.closeAll(this.autoCloseables);
  }

  /**
   * Print the information for an available image to stdout.
   *
   * @param image that has been created.
   */
  private void printAvailableImage(final Image image) {
    final Subscription subscription = image.subscription();
    System.out.printf(
      "Available image on %s streamId=%d sessionId=%d from %s%n",
      subscription.channel(), subscription.streamId(), image.sessionId(), image.sourceIdentity());
  }

  /**
   * Print the information for an unavailable image to stdout.
   *
   * @param image that has gone inactive.
   */
  private void printUnavailableImage(final Image image) {
    final Subscription subscription = image.subscription();
    System.out.printf(
      "Unavailable image on %s streamId=%d sessionId=%d%n",
      subscription.channel(), subscription.streamId(), image.sessionId());
  }

  protected void setAeronDirectory(final String directory) {
    this.context.aeronDirectoryName(directory);
  }

}
