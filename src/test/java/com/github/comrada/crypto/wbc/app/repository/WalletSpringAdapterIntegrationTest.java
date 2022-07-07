package com.github.comrada.crypto.wbc.app.repository;

import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.comrada.crypto.wbc.app.entity.WalletEntity;
import com.github.comrada.crypto.wbc.app.entity.WalletId;
import com.github.comrada.crypto.wbc.checker.WalletStorage;
import com.github.comrada.crypto.wbc.domain.Wallet;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
  @PersistenceContext
  private EntityManager entityManager;

  @Test
  @Sql("wallets.sql")
  void whenSelectForUpdateCalled_thenWalletIsLocked() {
    Optional<Wallet> foundXrpWallet = testStorage.selectForUpdate(singleton("Ripple"));
    assertTrue(foundXrpWallet.isPresent());
    Wallet xrpWallet = foundXrpWallet.get();
    assertEquals("Ripple", xrpWallet.blockchain());
    assertEquals("rHWcuuZoFvDS6gNbmHSdpb7u1hZzxvCoMt", xrpWallet.address());
  }

  @Test
  @Sql("wallets.sql")
  void update() {
    Optional<Wallet> foundXrpWallet = testStorage.selectForUpdate(singleton("Ripple"));
    assertTrue(foundXrpWallet.isPresent());
    Wallet xrpWallet = foundXrpWallet.get();
    assertNull(xrpWallet.balance());

    Wallet walletForUpdate = new Wallet(xrpWallet.blockchain(), xrpWallet.address(), BigDecimal.valueOf(123),
        xrpWallet.exchange());
    testStorage.update(walletForUpdate);

    Optional<WalletEntity> foundUpdated = testRepository.findById(
        new WalletId(xrpWallet.blockchain(), xrpWallet.address()));
    assertTrue(foundUpdated.isPresent());
    entityManager.refresh(foundUpdated.get());
    assertEquals(BigDecimal.valueOf(123).setScale(2, RoundingMode.UP), foundUpdated.get().getBalance());
  }
}