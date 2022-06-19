package com.github.comrada.crypto.wbc.blockchain;

import java.math.BigDecimal;

public interface BalanceChecker {

  BigDecimal fetch(String address);

  String asset();
}
