package com.github.comrada.crypto.wbc.blockchain;

import java.math.BigDecimal;

public interface BlockchainApi {

  String name();

  BigDecimal balance(String address);

  String asset();
}
