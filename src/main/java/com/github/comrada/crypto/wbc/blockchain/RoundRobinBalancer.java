package com.github.comrada.crypto.wbc.blockchain;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.delayedExecutor;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.github.comrada.crypto.wbc.blockchain.exception.NoLiveServicesException;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RoundRobinBalancer implements AutoCloseable {

  private static final Logger LOGGER = LoggerFactory.getLogger(RoundRobinBalancer.class);
  private static final int MAX_FAILURES = 3;
  private final Duration recoveringDelay;
  private final Iterator<BlockchainApi> services;
  private final Map<BlockchainApi, AtomicInteger> serviceFailures;
  private final ExecutorService recoveringExecutor;

  public RoundRobinBalancer(List<BlockchainApi> services, Duration recoveringDelay) {
    this.services = Iterables.cycle(services).iterator();
    this.recoveringDelay = requireNonNull(recoveringDelay);
    this.serviceFailures = services.stream().collect(toMap(identity(), i -> new AtomicInteger()));
    this.recoveringExecutor = createExecutor();
  }

  private ExecutorService createExecutor() {
    ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
        .setNameFormat("RoundRobin-recover-%d")
        .build();
    return newSingleThreadExecutor(namedThreadFactory);
  }

  public BigDecimal getBalance(String address) {
    checkLiveServices();
    BlockchainApi service = services.next();
    if (serviceFailures.get(service).get() <= MAX_FAILURES) {
      try {
        BigDecimal balance = service.balance(address);
        resetFailuresFor(service);
        return balance;
      } catch (Exception e) {
        int failures = incrementFailuresFor(service);
        logFailures(service, failures);
        return getBalance(address);
      }
    }
    return getBalance(address);
  }

  private void logFailures(BlockchainApi service, int failures) {
    if (failures < MAX_FAILURES) {
      LOGGER.warn("Requesting Bitcoin balance with {} failed {} times, will be attempted again",
          service.getClass().getSimpleName(), failures);
    }
  }

  private void resetFailuresFor(BlockchainApi service) {
    serviceFailures.get(service).compareAndSet(MAX_FAILURES, 0);
  }

  private int incrementFailuresFor(BlockchainApi service) {
    return serviceFailures.computeIfPresent(service, (s, failures) -> {
      if (failures.incrementAndGet() == MAX_FAILURES) {
        LOGGER.warn("The number of failures of service '{}' has reached the limit, it will be stopped for {} minutes",
            service.getClass().getSimpleName(), recoveringDelay.getSeconds() / 60);
        scheduleRecoveringTimerFor(service);
      }
      return failures;
    }).get();
  }

  private void scheduleRecoveringTimerFor(BlockchainApi service) {
    runAsync(() -> {
      resetFailuresFor(service);
      LOGGER.info("Service {} is back in operation.", service.name());
    }, delayedExecutor(recoveringDelay.getSeconds(), TimeUnit.SECONDS, recoveringExecutor));
  }

  private void checkLiveServices() {
    boolean anyServiceLeft = serviceFailures.values().stream()
        .anyMatch(failures -> failures.get() < MAX_FAILURES);
    if (!anyServiceLeft) {
      throw new NoLiveServicesException("No live Bitcoin webservices available");
    }
  }

  @Override
  public void close() {
    LOGGER.info("Trying to stop recovering executor");
    recoveringExecutor.shutdown();
  }
}
