package com.github.comrada.crypto.wbc.checker.scheduler;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.delayedExecutor;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

import com.github.comrada.crypto.wbc.checker.WalletStorage;
import com.github.comrada.crypto.wbc.domain.Wallet;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoragePoller implements AutoCloseable {

  private static final Logger LOGGER = LoggerFactory.getLogger(StoragePoller.class);

  private final ExecutorService executor;
  private final WalletStorage walletStorage;
  private final DelayGenerator retryDelayGenerator;
  private final DelayGenerator pollDelayGenerator;
  private final Supplier<Map<String, Set<String>>> blockchainsToPoll;
  private final Consumer<Wallet> walletHandler;

  public StoragePoller(WalletStorage walletStorage, DelayGenerator retryDelayGenerator,
      DelayGenerator pollDelayGenerator, Supplier<Map<String, Set<String>>> blockchainsToPoll, Consumer<Wallet> walletHandler) {
    this.walletStorage = requireNonNull(walletStorage);
    this.retryDelayGenerator = requireNonNull(retryDelayGenerator);
    this.pollDelayGenerator = requireNonNull(pollDelayGenerator);
    this.blockchainsToPoll = requireNonNull(blockchainsToPoll);
    this.walletHandler = requireNonNull(walletHandler);
    this.executor = createExecutor();
    runTask(this::poll, Duration.ZERO);
  }

  private ExecutorService createExecutor() {
    ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
        .setNameFormat(StoragePoller.class.getSimpleName() + "-%d")
        .build();
    return newSingleThreadExecutor(namedThreadFactory);
  }

  private void poll() {
    Optional<Wallet> foundWallet = walletStorage.selectForUpdate(blockchainsToPoll.get());
    if (foundWallet.isPresent()) {
      LOGGER.info("Start checking: [{}]", foundWallet.get());
      handleWithDelay(foundWallet.get(), pollDelayGenerator.next());
    } else {
      LOGGER.debug("No wallets to update, sleeping...");
      retryDelayGenerator.reset();
      runTask(this::poll, nextDelayFor(null));
    }
  }

  private void handleWithDelay(Wallet wallet, Duration delay) {
    runTask(() -> walletHandler.accept(wallet), delay)
        .thenRun(() -> {
          retryDelayGenerator.reset();
          poll();
        })
        .exceptionally(throwable -> {
          LOGGER.error("Execution failed. Attempt will be repeated in %s".formatted(retryDelayGenerator.peekNext()),
              throwable);
          handleWithDelay(wallet, nextDelayFor(wallet));
          return null;
        });
  }

  private Duration nextDelayFor(Wallet wallet) {
    try {
      return retryDelayGenerator.next();
    } catch (Throwable t) {
      LOGGER.error(t.getMessage(), t);
      if (wallet != null) {
        walletStorage.unlock(wallet);
      }
      recoverPolling();
      throw t;
    }
  }

  private void recoverPolling() {
    LOGGER.info("An attempt to restore consumption will be made in 1 hour.");
    retryDelayGenerator.reset();
    runTask(this::poll, Duration.ofHours(1));
  }

  private CompletableFuture<Void> runTask(Runnable task, Duration delay) {
    if (delay.isZero()) {
      return runAsync(task, executor);
    }
    return runAsync(task, delayedExecutor(delay.getSeconds(), TimeUnit.SECONDS, executor));
  }

  @Override
  public void close() {
    LOGGER.info("Trying to stop storage poller");
    executor.shutdown();
  }
}
