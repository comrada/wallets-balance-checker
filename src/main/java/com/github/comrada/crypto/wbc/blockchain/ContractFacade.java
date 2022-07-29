package com.github.comrada.crypto.wbc.blockchain;

import com.github.comrada.crypto.wbc.domain.Wallet;
import java.math.BigDecimal;

public interface ContractFacade {

  BigDecimal balanceOf(Wallet wallet);

  String symbol(Wallet wallet);

  int decimals(Wallet wallet);
}
