package com.github.comrada.crypto.wbc.checker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.comrada.crypto.wbc.domain.Wallet;
import com.github.comrada.crypto.wbc.domain.WalletStatus;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class BalanceUpdaterTest {

  private NetworksManager networksManager;
  private WalletStorage walletStorage;

  @BeforeEach
  void initMocks() {
    networksManager = mock(NetworksManager.class);
    walletStorage = mock(WalletStorage.class);
  }

  @Test
  void accept() {
    Wallet wallet = mockWallet(BigDecimal.ZERO);
    when(networksManager.balance(wallet)).thenReturn(BigDecimal.valueOf(123));
    BalanceUpdater balanceUpdater = new BalanceUpdater(networksManager, walletStorage);
    balanceUpdater.accept(wallet);
    ArgumentCaptor<Wallet> captor = ArgumentCaptor.forClass(Wallet.class);

    verify(networksManager, times(1)).balance(wallet);
    verify(walletStorage, times(1)).update(captor.capture());

    Wallet updatedWalletEntity = captor.getValue();
    assertEquals(BigDecimal.valueOf(123), updatedWalletEntity.balance());
  }

  @Test
  void whenBalanceHasNotChanged_thenUnlockOnly() {
    Wallet wallet = mockWallet(BigDecimal.valueOf(252597.24));
    when(networksManager.balance(wallet)).thenReturn(BigDecimal.valueOf(252597.23637465));
    BalanceUpdater balanceUpdater = new BalanceUpdater(networksManager, walletStorage);
    balanceUpdater.accept(wallet);

    verify(networksManager, times(1)).balance(wallet);
  }

  private Wallet mockWallet(BigDecimal balance) {
    return new Wallet(
        "Ripple",
        "r3qZhy6sKxn43uaHMnEMKQ6tpzhCo18ULZ",
        balance,
        false,
        WalletStatus.OK
    );
  }
}