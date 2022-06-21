package com.github.comrada.crypto.wbc.checker.scheduler;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.delayedExecutor;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

import com.github.comrada.crypto.wbc.checker.WalletStorage;
import com.github.comrada.crypto.wbc.checker.entity.Wallet;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoragePoller implements AutoCloseable {

  private static final Logger LOGGER = LoggerFactory.getLogger(StoragePoller.class);

  private final ExecutorService executor;
  private final WalletStorage walletStorage;
  private final DelayGenerator delayGenerator;
  private final Set<String> assetsToPoll;
  private final Consumer<Wallet> walletHandler;

  public StoragePoller(WalletStorage walletStorage, DelayGenerator delayGenerator, Set<String> assetsToPoll,
      Consumer<Wallet> walletHandler) {
    this.walletStorage = requireNonNull(walletStorage);
    this.delayGenerator = requireNonNull(delayGenerator);
    this.assetsToPoll = requireNonNull(assetsToPoll);
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
    Optional<Wallet> foundWallet = walletStorage.selectForUpdate(assetsToPoll);
    if (foundWallet.isPresent()) {
      LOGGER.info("Start checking: {}", foundWallet.get());
      handleWithDelay(foundWallet.get(), Duration.ZERO);
    } else {
      LOGGER.debug("No wallets to update, sleeping...");
      delayGenerator.reset();
      runTask(this::poll, nextDelayFor(null));
    }
  }

  private void handleWithDelay(Wallet wallet, Duration delay) {
    runTask(() -> walletHandler.accept(wallet), delay)
        .thenRun(() -> {
          delayGenerator.reset();
          poll();
        })
        .exceptionally(throwable -> {
          LOGGER.error("Execution failed. Attempt will be repeated in %s".formatted(delayGenerator.peekNext()),
              throwable);
          handleWithDelay(wallet, nextDelayFor(wallet));
          return null;
        });
  }

  private Duration nextDelayFor(Wallet wallet) {
    try {
      return delayGenerator.next();
    } catch (Throwable t) {
      LOGGER.error(t.getMessage(), t);
      if (wallet != null && wallet.isLocked()) {
        wallet.setLocked(false);
        walletStorage.update(wallet);
      }
      recoverPolling();
      throw t;
    }
  }

  private void recoverPolling() {
    LOGGER.info("An attempt to restore consumption will be made in 1 hour.");
    delayGenerator.reset();
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
