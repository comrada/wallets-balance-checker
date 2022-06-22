package com.github.comrada.crypto.wbc.app.config;

import com.github.comrada.crypto.wbc.checker.BalanceUpdater;
import com.github.comrada.crypto.wbc.checker.NetworksManager;
import com.github.comrada.crypto.wbc.checker.WalletStorage;
import com.github.comrada.crypto.wbc.checker.scheduler.DelayGenerator;
import com.github.comrada.crypto.wbc.checker.scheduler.LimitedFixedDelayGenerator;
import com.github.comrada.crypto.wbc.checker.scheduler.StoragePoller;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class StoragePollerConfig {

  @Bean
  StoragePoller storagePoller(WalletStorage walletStorage, BalanceUpdater balanceUpdater,
      NetworksManager networksManager, DelayGenerator delayGenerator) {
    return new StoragePoller(walletStorage, delayGenerator, networksManager.assets(), balanceUpdater);
  }

  @Bean
  DelayGenerator delayGenerator() {
    return new LimitedFixedDelayGenerator(Duration.ofSeconds(60), 3);
  }

  @Bean
  BalanceUpdater walletHandler(NetworksManager networksManager, WalletStorage walletStorage) {
    return new BalanceUpdater(networksManager, walletStorage);
  }
}
