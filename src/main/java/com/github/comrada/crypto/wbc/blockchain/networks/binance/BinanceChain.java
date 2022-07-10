package com.github.comrada.crypto.wbc.blockchain.networks.binance;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.blockchain.exception.InvalidWalletException;
import com.github.comrada.crypto.wbc.blockchain.rest.BaseHttpClient;
import com.github.comrada.crypto.wbc.checker.NetworkConfig;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.util.List;

public final class BinanceChain extends BaseHttpClient implements BlockchainApi {

  public static final String BLOCKCHAIN_NAME = "Binance Chain";
  private static final String ACCOUNT_URL = "https://dex.binance.org/api/v1/account/";
  private final String asset;

  public BinanceChain(HttpClient client, NetworkConfig networkConfig) {
    super(client);
    asset = networkConfig.getStringParam("asset");
  }

  @Override
  public String name() {
    return BLOCKCHAIN_NAME;
  }

  @Override
  public BigDecimal balance(String address) {
    Account account = get(ACCOUNT_URL + address, Account.class);
    List<Balance> balances = account.getBalances();
    if (balances == null || balances.isEmpty()) {
      throw new InvalidWalletException("Wallet '" + address + "' does not have balances");
    }
    return balances.stream()
        .filter(balance -> balance.getSymbol().equals(asset))
        .findFirst()
        .map(balance -> new BigDecimal(balance.getFree())
            .add(new BigDecimal(balance.getFrozen()))
            .add(new BigDecimal(balance.getLocked())))
        .orElse(BigDecimal.ZERO);
  }
}
