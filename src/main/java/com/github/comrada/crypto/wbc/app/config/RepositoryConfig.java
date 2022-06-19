package com.github.comrada.crypto.wbc.app.config;

import com.github.comrada.crypto.wbc.app.repository.WalletRepository;
import com.github.comrada.crypto.wbc.app.repository.WalletSpringAdapter;
import com.github.comrada.crypto.wbc.checker.WalletStorage;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = "com.github.comrada.crypto.wbc.checker.entity")
public class RepositoryConfig {

  @Bean
  WalletStorage walletStorage(WalletRepository walletRepository) {
    return new WalletSpringAdapter(walletRepository);
  }
}