package com.github.comrada.crypto.wbc.domain;

import java.math.BigDecimal;

public record Wallet(
    String blockchain,
    String address,
    BigDecimal balance,
    boolean exchange
) {

  @Override
  public String toString() {
    return "Blockchain: " + blockchain + ", address: " + address + (balance != null ? ", balance: " + balance : "");
  }
}
