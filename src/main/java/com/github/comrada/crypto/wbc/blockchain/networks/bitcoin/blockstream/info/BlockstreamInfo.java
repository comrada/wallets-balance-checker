package com.github.comrada.crypto.wbc.blockchain.networks.bitcoin.blockstream.info;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.blockchain.exception.NetworkException;
import com.github.comrada.crypto.wbc.blockchain.networks.bitcoin.BaseHttpClient;
import java.math.BigDecimal;
import java.net.http.HttpClient;

public class BlockstreamInfo extends BaseHttpClient implements BlockchainApi {

  private static final String ADDRESS_URL = "https://blockstream.info/api/address/";
  private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

  public BlockstreamInfo(HttpClient client) {
    super(client);
  }

  @Override
  public String name() {
    return "Bitcoin";
  }

  @Override
  public BigDecimal balance(String address) {
    String rawResponse = get(ADDRESS_URL + address);
    try {
      Response response = objectMapper.readValue(rawResponse, Response.class);
      return response.getFinalBalance();
    } catch (JsonProcessingException e) {
      throw new NetworkException(e);
    }
  }
}
