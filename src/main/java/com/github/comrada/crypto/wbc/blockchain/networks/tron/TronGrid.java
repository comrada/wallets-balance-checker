package com.github.comrada.crypto.wbc.blockchain.networks.tron;

import static java.util.Collections.singleton;
import static java.util.Objects.requireNonNull;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.blockchain.ContractFacade;
import com.github.comrada.crypto.wbc.blockchain.rest.BaseHttpClient;
import com.github.comrada.crypto.wbc.blockchain.rest.ResponseMapper;
import com.github.comrada.crypto.wbc.checker.NetworkConfig;
import com.github.comrada.crypto.wbc.domain.Wallet;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.util.Map;
import java.util.Set;

public final class TronGrid extends BaseHttpClient implements BlockchainApi {

  private static final String ACCOUNT_URL = "https://api.trongrid.io/v1/accounts/%s?only_confirmed=true";
  public static final String BLOCKCHAIN_NAME = "Tron";
  public static final Set<String> SUPPORTED_ASSETS = singleton("TRX");
  private final BalanceExtractor balanceExtractor;
  private final Set<String> usingAssets;
  private final ContractFacade contractFacade;

  public TronGrid(HttpClient client, ResponseMapper responseMapper, NetworkConfig networkConfig,
      ContractFacade contractFacade) {
    super(client, responseMapper, Map.of("TRON-PRO-API-KEY", networkConfig.getStringParam("api-key")));
    this.contractFacade = requireNonNull(contractFacade);
    this.usingAssets = networkConfig.getArray("assets", SUPPORTED_ASSETS);
    this.balanceExtractor = new BalanceExtractor();
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
    if (wallet.asset().equals("TRX")) {
      Account account = get(ACCOUNT_URL.formatted(wallet.address()), Account.class);
      return balanceExtractor.extract(account).movePointLeft(6);
    }
    return contractFacade.balanceOf(wallet);
  }
}
