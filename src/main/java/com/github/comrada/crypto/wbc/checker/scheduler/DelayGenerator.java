package com.github.comrada.crypto.wbc.checker.scheduler;

import java.time.Duration;

public interface DelayGenerator {

  /**
   * @return next delay, presumably can change the internal state to generate the next delay.
   */
  Duration next();

  /**
   * @return next delay without changing the internal state.
   */
  Duration peekNext();

  /**
   * In case of a successful attempt, the generator may have an internal state reset.
   */
  default void reset() {

  }
}
