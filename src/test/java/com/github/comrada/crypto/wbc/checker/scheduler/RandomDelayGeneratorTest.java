package com.github.comrada.crypto.wbc.checker.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

class RandomDelayGeneratorTest {

  @Test
  void testThatNextAlwaysNew() {
    RandomDelayGenerator delayGenerator = new RandomDelayGenerator(Duration.ofSeconds(1), Duration.ofSeconds(1000));
    for (int i = 0; i < 10; i++) {
      Duration n1 = delayGenerator.next();
      Duration n2 = delayGenerator.next();
      assertNotEquals(n1, n2);
    }
  }

  @Test
  void testThatPeekNextAlwaysTheSame() {
    RandomDelayGenerator delayGenerator = new RandomDelayGenerator(Duration.ofSeconds(1), Duration.ofSeconds(1000));
    for (int i = 0; i < 10; i++) {
      Duration n1 = delayGenerator.peekNext();
      Duration n2 = delayGenerator.peekNext();
      assertEquals(n1, n2);
    }
  }

  @RepeatedTest(10)
  void testThatNextAlwaysEqualsPeekNext() {
    RandomDelayGenerator delayGenerator = new RandomDelayGenerator(Duration.ofSeconds(5), Duration.ofSeconds(10));
    Duration peekNext = delayGenerator.peekNext();
    Duration next = delayGenerator.next();
    assertEquals(peekNext, next);
  }

  @RepeatedTest(10)
  void testThatPeekNextNeverEqualsNext() {
    RandomDelayGenerator delayGenerator = new RandomDelayGenerator(Duration.ofSeconds(1), Duration.ofSeconds(1000));
    Duration next = delayGenerator.next();
    Duration peekNext = delayGenerator.peekNext();
    assertNotEquals(next, peekNext);
  }

  @RepeatedTest(10)
  void peekNext() {
    RandomDelayGenerator delayGenerator = new RandomDelayGenerator(Duration.ofSeconds(5), Duration.ofSeconds(10));
    Duration delay = delayGenerator.peekNext();
    assertTrue(delay.getSeconds() >= 5);
    assertTrue(delay.getSeconds() < 10);
  }

  @RepeatedTest(10)
  void next() {
    RandomDelayGenerator delayGenerator = new RandomDelayGenerator(Duration.ofSeconds(5), Duration.ofSeconds(10));
    Duration delay = delayGenerator.next();
    assertTrue(delay.getSeconds() >= 5);
    assertTrue(delay.getSeconds() < 10);
  }

  @Test
  void whenMinDelayGreaterThanMaxDelay_thenExceptionThrown() {
    assertThrows(IllegalArgumentException.class,
        () -> new RandomDelayGenerator(Duration.ofSeconds(10), Duration.ofSeconds(5)));
  }

  @Test
  void whenMinDelayEqualsMaxDelay_thenExceptionThrown() {
    assertThrows(IllegalArgumentException.class,
        () -> new RandomDelayGenerator(Duration.ofSeconds(10), Duration.ofSeconds(10)));
  }
}