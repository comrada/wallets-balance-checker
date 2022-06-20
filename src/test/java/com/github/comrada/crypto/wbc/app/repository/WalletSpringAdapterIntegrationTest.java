package com.github.comrada.crypto.wbc.app.repository;

import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.comrada.crypto.wbc.checker.WalletStorage;
import com.github.comrada.crypto.wbc.checker.entity.Wallet;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class WalletSpringAdapterIntegrationTest {

  @Autowired
  private WalletStorage testStorage;

  @Autowired
  private WalletRepository testRepository;

  @Test
  @Sql("wallets.sql")
  void whenSelectForUpdateCalled_thenWalletIsLocked() {
    Optional<Wallet> foundXrpWallet = testStorage.selectForUpdate(singleton("XRP"));
    assertTrue(foundXrpWallet.isPresent());
    Wallet xrpWallet = foundXrpWallet.get();
    assertTrue(xrpWallet.isLocked());
    assertEquals("XRP", xrpWallet.getId().getAsset());
    assertEquals("rHWcuuZoFvDS6gNbmHSdpb7u1hZzxvCoMt", xrpWallet.getId().getAddress());
  }

  @Test
  @Sql("wallets.sql")
  void update() {
    Optional<Wallet> foundXrpWallet = testStorage.selectForUpdate(singleton("XRP"));
    assertTrue(foundXrpWallet.isPresent());
    Wallet xrpWallet = foundXrpWallet.get();
    assertNull(xrpWallet.getBalance());

    xrpWallet.setBalance(BigDecimal.valueOf(123));
    testStorage.update(xrpWallet);

    Optional<Wallet> foundUpdated = testRepository.findById(xrpWallet.getId());
    assertTrue(foundUpdated.isPresent());
    assertEquals(BigDecimal.valueOf(123), foundUpdated.get().getBalance());
  }
}