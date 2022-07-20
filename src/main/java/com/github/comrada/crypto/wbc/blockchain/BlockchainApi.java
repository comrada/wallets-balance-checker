package com.github.comrada.crypto.wbc.blockchain;

import com.github.comrada.crypto.wbc.domain.Wallet;
import java.math.BigDecimal;
import java.util.Set;

public interface BlockchainApi {

  String name();

  Set<String> assets();

  BigDecimal balance(Wallet wallet);
}
