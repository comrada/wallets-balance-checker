package com.github.comrada.crypto.wbc.checker;

import static java.util.Objects.requireNonNull;

import com.github.comrada.crypto.wbc.checker.entity.Wallet;
import java.math.BigDecimal;
import java.time.Instant;
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
    if (wallet.getBalance() == null || !wallet.getBalance().equals(balance)) {
      wallet.setBalance(balance);
      wallet.setCheckedAt(Instant.now());
      wallet.setLocked(false);
      Wallet updated = walletStorage.update(wallet);
      LOGGER.info("Updated: {}", updated);
    } else {
      LOGGER.info("Wallet balance has not changed");
    }
  }
}
