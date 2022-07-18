package com.github.comrada.crypto.wbc.blockchain.networks.tron;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.blockchain.rest.BaseHttpClient;
import com.github.comrada.crypto.wbc.blockchain.rest.ResponseMapper;
import com.github.comrada.crypto.wbc.checker.NetworkConfig;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.util.Map;

public final class TronGrid extends BaseHttpClient implements BlockchainApi {

  private static final String ACCOUNT_URL = "https://api.trongrid.io/v1/accounts/%s?only_confirmed=true";
  public static final String BLOCKCHAIN_NAME = "Tron";
  private final BalanceExtractor balanceExtractor;

  public TronGrid(HttpClient client, ResponseMapper responseMapper, NetworkConfig networkConfig) {
    super(client, responseMapper, Map.of("TRON-PRO-API-KEY", networkConfig.getStringParam("api-key")));
    balanceExtractor = new BalanceExtractor();
  }

  @Override
  public String name() {
    return BLOCKCHAIN_NAME;
  }

  @Override
  public BigDecimal balance(String address) {
    Account account = get(ACCOUNT_URL.formatted(address), Account.class);
    return balanceExtractor.extract(account).movePointLeft(6);
  }
}
