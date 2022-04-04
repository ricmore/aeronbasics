package com.github.ricmore.aeronbasics.plain.subscription;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.agrona.concurrent.IdleStrategy;

import io.aeron.FragmentAssembler;
import io.aeron.Subscription;
import io.aeron.driver.Configuration;
import io.aeron.logbuffer.FragmentHandler;

public class SubscriptionHandlersFactory {

  public FragmentHandler asciiMessagePrinter(final int streamId) {
    return (buffer, offset, length, header) -> {
      final String msg = buffer.getStringWithoutLengthAscii(offset, length);
      System.out.printf(
        "Message to stream %d from session %d (%d@%d) <<%s>>%n",
        streamId, header.sessionId(), length, offset, msg);
    };
  }

  public Consumer<Subscription> subscriberLoop(
    final FragmentHandler fragmentHandler,
    final int limit,
    final AtomicBoolean running) {

    //IdleStrategy idleStrategy = new YieldingIdleStrategy();
    IdleStrategy idleStrategy = Configuration.agentIdleStrategy("org.agrona.concurrent.BusySpinIdleStrategy", null);
    return
      (subscription) ->
      {
        final FragmentAssembler assembler = new FragmentAssembler(fragmentHandler);
        while (running.get()) {
          final int fragmentsRead = subscription.poll(assembler, limit);
          idleStrategy.idle(fragmentsRead);
        }
      };
  }

}
