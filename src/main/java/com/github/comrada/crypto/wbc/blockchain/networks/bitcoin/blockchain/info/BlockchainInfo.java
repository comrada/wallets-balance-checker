package com.github.comrada.crypto.wbc.blockchain.networks.bitcoin.blockchain.info;

import static java.util.Collections.singleton;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.blockchain.rest.BaseHttpClient;
import com.github.comrada.crypto.wbc.blockchain.rest.ResponseMapper;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.util.Set;

public class BlockchainInfo extends BaseHttpClient implements BlockchainApi {

  private static final String ADDRESS_URL = "https://blockchain.info/balance?active=";
  public static final Set<String> SUPPORTED_ASSETS = singleton("BTC");
  public static final String BLOCKCHAIN_NAME = "Bitcoin";

  public BlockchainInfo(HttpClient client, ResponseMapper responseMapper) {
    super(client, responseMapper);
  }

  @Override
  public String name() {
    return BLOCKCHAIN_NAME;
  }

  @Override
  public Set<String> assets() {
    return SUPPORTED_ASSETS;
  }

  @Override
  public BigDecimal balance(String address) {
    Response response = get(ADDRESS_URL + address, Response.class);
    return response.getFinalBalance(address);
  }
}
