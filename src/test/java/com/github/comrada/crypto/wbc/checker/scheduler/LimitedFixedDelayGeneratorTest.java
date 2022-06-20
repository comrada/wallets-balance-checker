package com.github.comrada.crypto.wbc.checker.scheduler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.comrada.crypto.wbc.checker.scheduler.LimitedFixedDelayGenerator.AttemptLimitReachedException;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class LimitedFixedDelayGeneratorTest {

  @Test
  void next_withoutReachingLimit() {
    Duration initialDelay = Duration.ofSeconds(1);
    LimitedFixedDelayGenerator delayGenerator = new LimitedFixedDelayGenerator(initialDelay, 2);
    assertEquals(initialDelay, delayGenerator.next());
    assertEquals(initialDelay, delayGenerator.next());
  }

  @Test
  void next_withReachingLimit() {
    LimitedFixedDelayGenerator delayGenerator = new LimitedFixedDelayGenerator(Duration.ofSeconds(1), 2);
    delayGenerator.next();
    delayGenerator.next();
    assertThrows(AttemptLimitReachedException.class, delayGenerator::next);
  }

  @Test
  void peekNext() {
    Duration initialDelay = Duration.ofSeconds(1);
    LimitedFixedDelayGenerator delayGenerator = new LimitedFixedDelayGenerator(initialDelay, 2);
    assertEquals(initialDelay, delayGenerator.peekNext());
    assertEquals(initialDelay, delayGenerator.peekNext());
  }

  @Test
  void reset() {
    LimitedFixedDelayGenerator delayGenerator = new LimitedFixedDelayGenerator(Duration.ofSeconds(1), 2);
    delayGenerator.next();
    delayGenerator.next();
    delayGenerator.reset();
    assertDoesNotThrow(delayGenerator::next);
  }
}
