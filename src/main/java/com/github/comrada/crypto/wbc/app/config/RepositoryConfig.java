package com.github.comrada.crypto.wbc.app.config;

import com.github.comrada.crypto.wbc.app.repository.WalletRepository;
import com.github.comrada.crypto.wbc.app.repository.WalletSpringAdapter;
import com.github.comrada.crypto.wbc.checker.WalletStorage;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

  @Bean
  WalletStorage walletStorage(WalletRepository walletRepository, EntityManager entityManager) {
    return new WalletSpringAdapter(walletRepository, entityManager);
  }
}
