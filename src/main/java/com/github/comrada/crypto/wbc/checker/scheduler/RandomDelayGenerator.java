package com.github.comrada.crypto.wbc.checker.scheduler;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.util.Random;

public class RandomDelayGenerator implements DelayGenerator {

  private final Duration minDelay;
  private final Duration maxDelay;
  private final Random random;
  private Duration nextDelay;

  public RandomDelayGenerator(Duration minDelay, Duration maxDelay) {
    if (minDelay.compareTo(maxDelay) >= 0) {
      throw new IllegalArgumentException("minDelay must be less than maxDelay");
    }
    this.minDelay = requireNonNull(minDelay);
    this.maxDelay = requireNonNull(maxDelay);
    this.random = new Random();
  }

  @Override
  public Duration next() {
    try {
      return peekNext();
    } finally {
      nextDelay = null;
    }
  }

  @Override
  public Duration peekNext() {
    if (nextDelay == null) {
      long randomDifference = random.nextLong(minDelay.getSeconds(), maxDelay.getSeconds());
      nextDelay = Duration.ofSeconds(randomDifference);
    }
    return nextDelay;
  }
}
