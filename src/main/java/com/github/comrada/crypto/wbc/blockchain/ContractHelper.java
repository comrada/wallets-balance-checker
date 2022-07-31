package com.github.comrada.crypto.wbc.blockchain;

import com.github.comrada.crypto.wbc.blockchain.exception.InvalidWalletException;
import com.github.comrada.crypto.wbc.domain.Wallet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class ContractHelper {

  private final Map<String, Integer> decimalsCache = new ConcurrentHashMap<>();

  public int getDecimals(Wallet wallet, Supplier<Integer> decimalsGetter) {
    return decimalsCache.computeIfAbsent(wallet.asset(), asset -> decimalsGetter.get());
  }

  public String getSymbol(Wallet wallet, Supplier<String> symbolGetter) {
    if (wallet.balance() == null) {
      String symbol = symbolGetter.get();
      if (!symbol.equals(wallet.asset())) {
        throw new InvalidWalletException("The symbol '%s' in the contract '%s' does not match the wallet's asset '%s'"
            .formatted(symbol, wallet.contract(), wallet.asset()));
      }
      return symbol;
    }
    return wallet.asset();
  }
}
