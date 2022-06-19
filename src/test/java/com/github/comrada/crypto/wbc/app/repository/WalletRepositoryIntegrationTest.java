package com.github.comrada.crypto.wbc.app.repository;

import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.comrada.crypto.wbc.checker.entity.Wallet;
import com.github.comrada.crypto.wbc.checker.entity.WalletId;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@EntityScan(basePackages = "com.github.comrada.crypto.wbc.checker.entity")
class WalletRepositoryIntegrationTest {

  @Autowired
  private WalletRepository testRepository;

  @Test
  @Sql("wallets.sql")
  void selectXrpForUpdate() {
    Optional<Wallet> foundXrpWallet = testRepository.selectForUpdate(singleton("XRP"));
    assertTrue(foundXrpWallet.isPresent());
    Wallet xrpWallet = foundXrpWallet.get();
    assertEquals("XRP", xrpWallet.getId().getAsset());
    assertEquals("rHWcuuZoFvDS6gNbmHSdpb7u1hZzxvCoMt", xrpWallet.getId().getAddress());
  }

  @Test
  @Sql("wallets.sql")
  void selectEthForUpdate() {
    Optional<Wallet> foundEthWallet = testRepository.selectForUpdate(singleton("ETH"));
    assertTrue(foundEthWallet.isPresent());
    Wallet ethWallet = foundEthWallet.get();
    assertEquals("ETH", ethWallet.getId().getAsset());
    assertEquals("0x0259512d4c4386327a5a2faf78fbabed7202c971", ethWallet.getId().getAddress());
  }

  @Test
  void whenWalletIsRecentlyUpdated_thenNothingFound() {
    Wallet wallet = new Wallet();
    WalletId id = new WalletId("ETH", "0x0259512d4c4386327a5a2faf78fbabed7202c971");
    wallet.setId(id);
    wallet.setExchange(false);
    wallet.setCheckedAt(Instant.now());
    testRepository.saveAndFlush(wallet);
    Optional<Wallet> foundEthWallet = testRepository.selectForUpdate(singleton("ETH"));
    assertFalse(foundEthWallet.isPresent());
  }

  @Test
  void whenWalletIsOutdated_thenItIsFound() {
    Wallet wallet = new Wallet();
    WalletId id = new WalletId("ETH", "0x0259512d4c4386327a5a2faf78fbabed7202c971");
    wallet.setId(id);
    wallet.setExchange(false);
    wallet.setCheckedAt(Instant.parse("2022-06-18T00:00:00Z"));
    testRepository.saveAndFlush(wallet);
    Optional<Wallet> foundEthWallet = testRepository.selectForUpdate(singleton("ETH"));
    assertTrue(foundEthWallet.isPresent());
    Wallet ethWallet = foundEthWallet.get();
    assertEquals("ETH", ethWallet.getId().getAsset());
    assertEquals("0x0259512d4c4386327a5a2faf78fbabed7202c971", ethWallet.getId().getAddress());
  }
}