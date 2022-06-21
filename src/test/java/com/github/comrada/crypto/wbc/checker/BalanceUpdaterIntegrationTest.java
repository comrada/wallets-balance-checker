package com.github.comrada.crypto.wbc.checker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.comrada.crypto.wbc.checker.entity.Wallet;
import com.github.comrada.crypto.wbc.checker.entity.WalletId;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class BalanceUpdaterIntegrationTest {

  @Test
  void accept() {
    NetworksManager networksManager = mock(NetworksManager.class);
    WalletStorage walletStorage = mock(WalletStorage.class);
    Wallet wallet = mockLockedWallet();
    when(networksManager.balance(wallet)).thenReturn(BigDecimal.valueOf(123));
    BalanceUpdater balanceUpdater = new BalanceUpdater(networksManager, walletStorage);
    balanceUpdater.accept(wallet);
    ArgumentCaptor<Wallet> captor = ArgumentCaptor.forClass(Wallet.class);

    verify(networksManager, times(1)).balance(wallet);
    verify(walletStorage, times(1)).update(captor.capture());

    Wallet updatedWallet = captor.getValue();
    assertEquals(BigDecimal.valueOf(123), updatedWallet.getBalance());
    assertFalse(updatedWallet.isLocked());
    assertNotNull(updatedWallet.getCheckedAt());
  }

  private Wallet mockLockedWallet() {
    Wallet wallet = new Wallet();
    WalletId id = new WalletId("XRP", "r3qZhy6sKxn43uaHMnEMKQ6tpzhCo18ULZ");
    wallet.setId(id);
    wallet.setLocked(true);
    wallet.setBalance(BigDecimal.ZERO);
    return wallet;
  }
}