package com.github.comrada.crypto.wbc.domain;

import java.math.BigDecimal;

public record Wallet(
    String blockchain,
    String address,
    BigDecimal balance,
    boolean exchange,
    WalletStatus status
) {

  public static Wallet newBalance(Wallet oldWallet, BigDecimal newBalance) {
    return new Wallet(oldWallet.blockchain, oldWallet.address(), newBalance, oldWallet.exchange(), oldWallet.status);
  }

  @Override
  public String toString() {
    return "Blockchain: " + blockchain + ", address: " + address + (balance != null ? ", balance: " + balance : "");
  }
}
