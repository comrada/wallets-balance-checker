package com.github.comrada.crypto.wbc.checker.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class FixedDelayGeneratorTest {

  @Test
  void next() {
    Duration initialDelay = Duration.ofSeconds(1);
    FixedDelayGenerator delayGenerator = new FixedDelayGenerator(initialDelay);
    assertEquals(initialDelay, delayGenerator.next());
    assertEquals(initialDelay, delayGenerator.next());
  }

  @Test
  void peekNext() {
    Duration initialDelay = Duration.ofSeconds(1);
    FixedDelayGenerator delayGenerator = new FixedDelayGenerator(initialDelay);
    assertEquals(initialDelay, delayGenerator.peekNext());
    assertEquals(initialDelay, delayGenerator.peekNext());
  }
}
