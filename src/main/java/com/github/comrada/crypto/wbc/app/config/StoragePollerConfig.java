package com.github.comrada.crypto.wbc.app.config;

import com.github.comrada.crypto.wbc.app.config.StoragePollerParameters.LimitedFixedGenerator;
import com.github.comrada.crypto.wbc.app.config.StoragePollerParameters.RandomGenerator;
import com.github.comrada.crypto.wbc.checker.BalanceUpdater;
import com.github.comrada.crypto.wbc.checker.NetworksManager;
import com.github.comrada.crypto.wbc.checker.WalletStorage;
import com.github.comrada.crypto.wbc.checker.scheduler.DelayGenerator;
import com.github.comrada.crypto.wbc.checker.scheduler.FixedDelayGenerator;
import com.github.comrada.crypto.wbc.checker.scheduler.LimitedFixedDelayGenerator;
import com.github.comrada.crypto.wbc.checker.scheduler.RandomDelayGenerator;
import com.github.comrada.crypto.wbc.checker.scheduler.StoragePoller;
import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
@EnableConfigurationProperties(StoragePollerParameters.class)
public class StoragePollerConfig {

  @Bean
  StoragePoller storagePoller(WalletStorage walletStorage, BalanceUpdater balanceUpdater,
      NetworksManager networksManager, DelayGenerator retryDelayGenerator, DelayGenerator pollDelayGenerator) {
    return new StoragePoller(walletStorage, retryDelayGenerator, pollDelayGenerator, networksManager::blockchains,
        balanceUpdater);
  }

  @Bean("retryDelayGenerator")
  @ConditionalOnProperty(value = "app.storage-poller.delay-generators.retry-delay.type", havingValue = "FIXED")
  DelayGenerator fixedRetryDelayGenerator(StoragePollerParameters parameters) {
    Duration delay = parameters.getFixedDelayFor("retry-delay").getDelay();
    return new FixedDelayGenerator(delay);
  }

  @Bean("retryDelayGenerator")
  @ConditionalOnProperty(value = "app.storage-poller.delay-generators.retry-delay.type", havingValue = "LIMITED_FIXED")
  DelayGenerator limitedFixedRetryDelayGenerator(StoragePollerParameters parameters) {
    LimitedFixedGenerator generatorConfig = parameters.getLimitedFixedDelayFor("retry-delay");
    return new LimitedFixedDelayGenerator(generatorConfig.getDelay(), generatorConfig.getRetryLimit());
  }

  @Bean("retryDelayGenerator")
  @ConditionalOnProperty(value = "app.storage-poller.delay-generators.retry-delay.type", havingValue = "RANDOM")
  DelayGenerator randomRetryDelayGenerator(StoragePollerParameters parameters) {
    RandomGenerator generatorConfig = parameters.getRandomFixedDelayFor("retry-delay");
    return new RandomDelayGenerator(generatorConfig.getMinDelay(), generatorConfig.getMaxDelay());
  }

  @Bean
  BalanceUpdater walletHandler(NetworksManager networksManager, WalletStorage walletStorage) {
    return new BalanceUpdater(networksManager, walletStorage);
  }

  @Bean("pollDelayGenerator")
  @ConditionalOnProperty(value = "app.storage-poller.delay-generators.poll-delay.type", havingValue = "FIXED")
  DelayGenerator fixedPollDelayGenerator(StoragePollerParameters parameters) {
    Duration delay = parameters.getFixedDelayFor("poll-delay").getDelay();
    return new FixedDelayGenerator(delay);
  }

  @Bean("pollDelayGenerator")
  @ConditionalOnProperty(value = "app.storage-poller.delay-generators.poll-delay.type", havingValue = "LIMITED_FIXED")
  DelayGenerator limitedFixedPollDelayGenerator(StoragePollerParameters parameters) {
    LimitedFixedGenerator generatorConfig = parameters.getLimitedFixedDelayFor("poll-delay");
    return new LimitedFixedDelayGenerator(generatorConfig.getDelay(), generatorConfig.getRetryLimit());
  }

  @Bean("pollDelayGenerator")
  @ConditionalOnProperty(value = "app.storage-poller.delay-generators.poll-delay.type", havingValue = "RANDOM")
  DelayGenerator randomPollDelayGenerator(StoragePollerParameters parameters) {
    RandomGenerator generatorConfig = parameters.getRandomFixedDelayFor("poll-delay");
    return new RandomDelayGenerator(generatorConfig.getMinDelay(), generatorConfig.getMaxDelay());
  }
}
