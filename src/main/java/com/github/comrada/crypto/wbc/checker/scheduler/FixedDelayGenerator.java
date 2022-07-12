package com.github.comrada.crypto.wbc.checker.scheduler;

import static java.util.Objects.requireNonNull;

import java.time.Duration;

public class FixedDelayGenerator implements DelayGenerator {

  private final Duration initialDelay;

  public FixedDelayGenerator(Duration initialDelay) {
    this.initialDelay = requireNonNull(initialDelay);
  }

  @Override
  public Duration next() {
    return initialDelay;
  }

  @Override
  public Duration peekNext() {
    return initialDelay;
  }
}
