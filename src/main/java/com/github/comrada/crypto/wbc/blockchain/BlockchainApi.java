package com.github.comrada.crypto.wbc.blockchain;

import java.math.BigDecimal;
import java.util.Set;

public interface BlockchainApi {

  String name();

  Set<String> assets();

  BigDecimal balance(String address);
}
