package com.github.comrada.crypto.wbc.app.config;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage-poller")
public class StoragePollerParameters {

  private Map<String, DelayGenerator> delayGenerators = new HashMap<>(2);

  public StoragePollerParameters() {
  }

  public void setDelayGenerators(Map<String, DelayGenerator> delayGenerators) {
    this.delayGenerators = delayGenerators;
  }

  public FixedGenerator getFixedDelayFor(String generator) {
    DelayGenerator delayGenerator = delayGenerators.get(generator);
    return delayGenerator.getFixedGenerator();
  }

  public LimitedFixedGenerator getLimitedFixedDelayFor(String generator) {
    DelayGenerator delayGenerator = delayGenerators.get(generator);
    return delayGenerator.getLimitedFixedGenerator();
  }

  public RandomGenerator getRandomFixedDelayFor(String generator) {
    DelayGenerator delayGenerator = delayGenerators.get(generator);
    return delayGenerator.getRandomGenerator();
  }

  public static final class DelayGenerator {

    private GeneratorType type;
    private FixedGenerator fixedGenerator;
    private LimitedFixedGenerator limitedFixedGenerator;
    private RandomGenerator randomGenerator;

    public DelayGenerator() {
    }

    public GeneratorType getType() {
      return type;
    }

    public void setType(GeneratorType type) {
      this.type = type;
    }

    public FixedGenerator getFixedGenerator() {
      return fixedGenerator;
    }

    public void setFixedGenerator(FixedGenerator fixedGenerator) {
      this.fixedGenerator = fixedGenerator;
    }

    public LimitedFixedGenerator getLimitedFixedGenerator() {
      return limitedFixedGenerator;
    }

    public void setLimitedFixedGenerator(
        LimitedFixedGenerator limitedFixedGenerator) {
      this.limitedFixedGenerator = limitedFixedGenerator;
    }

    public RandomGenerator getRandomGenerator() {
      return randomGenerator;
    }

    public void setRandomGenerator(
        RandomGenerator randomGenerator) {
      this.randomGenerator = randomGenerator;
    }
  }

  public static final class FixedGenerator {

    private Duration delay;

    public FixedGenerator() {
    }

    public Duration getDelay() {
      return requireNonNull(delay);
    }

    public void setDelay(Duration delay) {
      this.delay = delay;
    }

  }

  public static final class LimitedFixedGenerator {

    private Duration delay;
    private Integer retryLimit;

    public LimitedFixedGenerator() {
    }

    public Duration getDelay() {
      return requireNonNull(delay);
    }

    public void setDelay(Duration delay) {
      this.delay = delay;
    }

    public Integer getRetryLimit() {
      return requireNonNull(retryLimit);
    }

    public void setRetryLimit(Integer retryLimit) {
      this.retryLimit = retryLimit;
    }
  }

  public static final class RandomGenerator {

    private Duration minDelay;
    private Duration maxDelay;

    public RandomGenerator() {
    }

    public Duration getMinDelay() {
      return minDelay;
    }

    public void setMinDelay(Duration minDelay) {
      this.minDelay = minDelay;
    }

    public Duration getMaxDelay() {
      return maxDelay;
    }

    public void setMaxDelay(Duration maxDelay) {
      this.maxDelay = maxDelay;
    }
  }

  public enum GeneratorType {
    /**
     * Fixed generator always returns the same delay
     */
    FIXED,
    /**
     * Limited fixed generator always returns the same delay, but internally accumulates the number of calls. Throws an
     * exception when the limit is reached and if the reset method was not called.
     */
    LIMITED_FIXED,
    /**
     * Random generator always returns different delays
     */
    RANDOM
  }
}
