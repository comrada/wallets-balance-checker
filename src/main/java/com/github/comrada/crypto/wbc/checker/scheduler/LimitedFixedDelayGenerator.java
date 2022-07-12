package com.github.comrada.crypto.wbc.checker.scheduler;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class LimitedFixedDelayGenerator implements DelayGenerator {

  private final Duration initialDelay;
  private final int retryLimit;
  private final AtomicInteger attempt;

  public LimitedFixedDelayGenerator(Duration initialDelay, int retryLimit) {
    this.initialDelay = requireNonNull(initialDelay);
    this.retryLimit = retryLimit;
    this.attempt = new AtomicInteger();
  }

  @Override
  public Duration next() {
    if (attempt.incrementAndGet() > retryLimit) {
      throw new AttemptLimitReachedException("Attempt limit: " + retryLimit + " reached.");
    }
    return initialDelay;
  }

  @Override
  public Duration peekNext() {
    return initialDelay;
  }

  @Override
  public void reset() {
    attempt.set(0);
  }

  public static final class AttemptLimitReachedException extends RuntimeException {

    public AttemptLimitReachedException(String message) {
      super(message);
    }
  }
}
