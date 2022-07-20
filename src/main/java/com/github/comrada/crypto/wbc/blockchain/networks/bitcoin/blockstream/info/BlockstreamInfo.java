package com.github.comrada.crypto.wbc.blockchain.networks.bitcoin.blockstream.info;

import static java.util.Collections.singleton;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.blockchain.exception.NetworkException;
import com.github.comrada.crypto.wbc.blockchain.rest.BaseHttpClient;
import com.github.comrada.crypto.wbc.blockchain.rest.ResponseMapper;
import com.github.comrada.crypto.wbc.domain.Wallet;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.util.Set;

public class BlockstreamInfo extends BaseHttpClient implements BlockchainApi {

  private static final String ADDRESS_URL = "https://blockstream.info/api/address/";
  public static final String BLOCKCHAIN_NAME = "Bitcoin";
  public static final Set<String> SUPPORTED_ASSETS = singleton("BTC");

  public BlockstreamInfo(HttpClient client, ResponseMapper responseMapper) {
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
  public BigDecimal balance(Wallet wallet) {
    Response response = get(ADDRESS_URL + wallet.address(), Response.class);
    if (!response.address().equals(wallet.address())) {
      throw new NetworkException(
          "Blockstream.info returned a response with a different balance for the wallet than requested.");
    }
    return response.getFinalBalance();
  }
}
