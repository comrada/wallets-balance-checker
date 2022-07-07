package com.github.comrada.crypto.wbc.checker;

import static java.util.Objects.requireNonNull;

import com.github.comrada.crypto.wbc.domain.Wallet;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BalanceUpdater implements Consumer<Wallet> {

  private static final Logger LOGGER = LoggerFactory.getLogger(BalanceUpdater.class);
  private final NetworksManager networksManager;
  private final WalletStorage walletStorage;

  public BalanceUpdater(NetworksManager networksManager, WalletStorage walletStorage) {
    this.networksManager = requireNonNull(networksManager);
    this.walletStorage = requireNonNull(walletStorage);
  }

  @Override
  public void accept(Wallet wallet) {
    BigDecimal balance = networksManager.balance(wallet);
    if (isNew(wallet) || hasBalanceChanged(wallet, balance)) {
      Wallet walletWithNewBalance = new Wallet(wallet.blockchain(), wallet.address(), balance, wallet.exchange());
      walletStorage.update(walletWithNewBalance);
      LOGGER.info("Updated: {}", walletWithNewBalance);
    } else {
      LOGGER.info("Wallet balance has not changed");
      walletStorage.unlock(wallet);
    }
  }

  private boolean hasBalanceChanged(Wallet wallet, BigDecimal newBalance) {
    BigDecimal oldBalance = wallet.balance();
    BigDecimal alignedNewBalance = newBalance.setScale(2, RoundingMode.HALF_UP);
    return !oldBalance.equals(alignedNewBalance);
  }

  private boolean isNew(Wallet wallet) {
    return wallet.balance() == null;
  }
}
