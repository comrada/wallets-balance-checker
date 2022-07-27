package com.github.comrada.crypto.wbc.domain;

import java.math.BigDecimal;

public record Wallet(
    String blockchain,
    String address,
    String asset,
    BigDecimal balance,
    boolean exchange,
    WalletStatus status,
    boolean token,
    String contract
) {

  public Wallet(String blockchain, String address, String asset, BigDecimal balance, boolean exchange,
      WalletStatus status) {
    this(blockchain, address, asset, balance, exchange, status, false, null);
  }

  public static Wallet newBalance(Wallet oldWallet, BigDecimal newBalance) {
    return new Wallet(oldWallet.blockchain(), oldWallet.address(), oldWallet.asset(), newBalance, oldWallet.exchange(),
        oldWallet.status(), oldWallet.token(), oldWallet.contract());
  }

  @Override
  public String toString() {
    return "Blockchain: " + blockchain + ", address: " + address + ", asset: " + asset +
        (balance != null ? ", balance: " + balance : "");
  }
}
