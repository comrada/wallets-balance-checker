package com.github.comrada.crypto.wbc.app.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.comrada.crypto.wbc.app.entity.WalletEntity;
import com.github.comrada.crypto.wbc.app.entity.WalletId;
import com.github.comrada.crypto.wbc.domain.WalletStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
class WalletRepositoryIntegrationTest {

  @Autowired
  private WalletRepository testRepository;

  @Test
  @Sql("wallets.sql")
  void lock() {
    WalletId id = new WalletId("Ethereum", "0x0259512d4c4386327a5a2faf78fbabed7202c971", "ETH");
    testRepository.lock(id);
    Optional<WalletEntity> foundWallet = testRepository.findById(id);
    assertTrue(foundWallet.isPresent());
    WalletEntity wallet = foundWallet.get();
    assertEquals(id, wallet.getId());
    assertTrue(wallet.isLocked());
  }

  @Test
  @Sql("wallets.sql")
  void unlock() {
    WalletId id = new WalletId("Bitcoin", "3NYQhzaUPEa9HfcSkHkv77SUUkPx2dx39o", "BTC");
    testRepository.unlock(id, Instant.now());
    Optional<WalletEntity> foundWallet = testRepository.findById(id);
    assertTrue(foundWallet.isPresent());
    WalletEntity wallet = foundWallet.get();
    assertEquals(id, wallet.getId());
    assertNotNull(wallet.getCheckedAt());
    assertFalse(wallet.isLocked());
  }

  @Test
  @Sql("wallets.sql")
  void update() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    WalletId id = new WalletId("Bitcoin", "3NYQhzaUPEa9HfcSkHkv77SUUkPx2dx39o", "BTC");
    WalletEntity walletEntity = new WalletEntity();
    walletEntity.setId(id);
    walletEntity.setCheckedAt(now);
    walletEntity.setBalance(BigDecimal.valueOf(123));
    testRepository.update(walletEntity);
    Optional<WalletEntity> foundWallet = testRepository.findById(id);
    assertTrue(foundWallet.isPresent());
    WalletEntity wallet = foundWallet.get();
    assertEquals(id, wallet.getId());
    assertFalse(wallet.isLocked());
    assertEquals(now, wallet.getCheckedAt());
    assertEquals(BigDecimal.valueOf(123).setScale(2, RoundingMode.UP), wallet.getBalance());
  }

  @Test
  @Sql("wallets.sql")
  void changeStatus() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    WalletId id = new WalletId("Bitcoin", "3NYQhzaUPEa9HfcSkHkv77SUUkPx2dx39o", "BTC");
    testRepository.changeStatus(id, WalletStatus.INVALID, now);
    Optional<WalletEntity> foundWallet = testRepository.findById(id);
    assertTrue(foundWallet.isPresent());
    WalletEntity wallet = foundWallet.get();
    assertEquals(id, wallet.getId());
    assertFalse(wallet.isLocked());
    assertEquals(now, wallet.getCheckedAt());
    assertEquals(WalletStatus.INVALID, wallet.getStatus());
  }
}