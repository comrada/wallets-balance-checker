package com.github.comrada.crypto.wbc.app.repository;

import com.github.comrada.crypto.wbc.app.entity.WalletEntity;
import com.github.comrada.crypto.wbc.app.entity.WalletId;
import com.github.comrada.crypto.wbc.checker.WalletStorage;
import com.github.comrada.crypto.wbc.domain.Wallet;
import com.github.comrada.crypto.wbc.domain.WalletStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.*;

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
    Optional<Wallet> foundXrpWallet = testStorage.selectForUpdate(singletonMap("Ripple", singleton("XRP")));
    assertTrue(foundXrpWallet.isPresent());
    Wallet xrpWallet = foundXrpWallet.get();
    assertEquals("Ripple", xrpWallet.blockchain());
    assertEquals("rHWcuuZoFvDS6gNbmHSdpb7u1hZzxvCoMt", xrpWallet.address());
  }

  @Test
  @Sql("wallets.sql")
  void update() {
    Optional<Wallet> foundXrpWallet = testStorage.selectForUpdate(singletonMap("Ripple", singleton("XRP")));
    assertTrue(foundXrpWallet.isPresent());
    Wallet xrpWallet = foundXrpWallet.get();
    assertNull(xrpWallet.balance());

    Wallet walletForUpdate = new Wallet(xrpWallet.blockchain(), xrpWallet.address(), xrpWallet.asset(),
        BigDecimal.valueOf(123), xrpWallet.exchange(), WalletStatus.OK);
    testStorage.update(walletForUpdate);

    Optional<WalletEntity> foundUpdated = testRepository.findById(
        new WalletId(xrpWallet.blockchain(), xrpWallet.address(), xrpWallet.asset()));
    assertTrue(foundUpdated.isPresent());
    entityManager.refresh(foundUpdated.get());
    assertEquals(BigDecimal.valueOf(123).setScale(2, RoundingMode.UP), foundUpdated.get().getBalance());
  }

  @Test
  @Sql("wallets.sql")
  void invalidate() {
    Optional<Wallet> foundXrpWallet = testStorage.selectForUpdate(singletonMap("Ripple", singleton("XRP")));
    assertTrue(foundXrpWallet.isPresent());
    Wallet xrpWallet = foundXrpWallet.get();
    assertEquals(WalletStatus.OK, xrpWallet.status());

    testStorage.invalidate(xrpWallet);

    Optional<WalletEntity> foundUpdated = testRepository.findById(
        new WalletId(xrpWallet.blockchain(), xrpWallet.address(), xrpWallet.asset()));
    assertTrue(foundUpdated.isPresent());
    entityManager.refresh(foundUpdated.get());
    assertEquals(WalletStatus.INVALID, foundUpdated.get().getStatus());
  }
}