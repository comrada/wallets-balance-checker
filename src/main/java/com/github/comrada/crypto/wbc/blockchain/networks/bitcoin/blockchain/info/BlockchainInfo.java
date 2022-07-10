package com.github.comrada.crypto.wbc.blockchain.networks.bitcoin.blockchain.info;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.blockchain.rest.BaseHttpClient;
import java.math.BigDecimal;
import java.net.http.HttpClient;

public class BlockchainInfo extends BaseHttpClient implements BlockchainApi {

  private static final String ADDRESS_URL = "https://blockchain.info/balance?active=";
  public static final String BLOCKCHAIN_NAME = "Bitcoin";

  public BlockchainInfo(HttpClient client) {
    super(client);
  }

  @Override
  public String name() {
    return BLOCKCHAIN_NAME;
  }

  @Override
  public BigDecimal balance(String address) {
    Response response = get(ADDRESS_URL + address, Response.class);
    return response.getFinalBalance(address);
  }
}
