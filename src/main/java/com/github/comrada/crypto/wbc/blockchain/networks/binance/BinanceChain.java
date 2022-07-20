package com.github.comrada.crypto.wbc.blockchain.networks.binance;

import static java.util.Collections.singleton;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.blockchain.exception.InvalidWalletException;
import com.github.comrada.crypto.wbc.blockchain.rest.BaseHttpClient;
import com.github.comrada.crypto.wbc.blockchain.rest.ResponseMapper;
import com.github.comrada.crypto.wbc.checker.NetworkConfig;
import com.github.comrada.crypto.wbc.domain.Wallet;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Set;

public final class BinanceChain extends BaseHttpClient implements BlockchainApi {

  public static final String BLOCKCHAIN_NAME = "Binance Chain";
  public static final Set<String> SUPPORTED_ASSETS = singleton("BNB");
  private static final String ACCOUNT_URL = "https://dex.binance.org/api/v1/account/";
  private final Set<String> usingAssets;

  public BinanceChain(HttpClient client, ResponseMapper responseMapper, NetworkConfig networkConfig) {
    super(client, responseMapper);
    usingAssets = networkConfig.getArray("assets", SUPPORTED_ASSETS);
  }

  @Override
  public String name() {
    return BLOCKCHAIN_NAME;
  }

  @Override
  public Set<String> assets() {
    return usingAssets;
  }

  @Override
  public BigDecimal balance(Wallet wallet) {
    Account account = get(ACCOUNT_URL + wallet.address(), Account.class);
    List<Balance> balances = account.getBalances();
    if (balances == null || balances.isEmpty()) {
      throw new InvalidWalletException("Wallet [" + wallet + "] does not have balances");
    }
    return balances.stream()
        .filter(balance -> usingAssets.contains(balance.getSymbol()))
        .findFirst()
        .map(balance -> new BigDecimal(balance.getFree())
            .add(new BigDecimal(balance.getFrozen()))
            .add(new BigDecimal(balance.getLocked())))
        .orElse(BigDecimal.ZERO);
  }
}
