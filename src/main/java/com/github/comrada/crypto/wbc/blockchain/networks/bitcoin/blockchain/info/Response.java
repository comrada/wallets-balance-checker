package com.github.comrada.crypto.wbc.blockchain.networks.bitcoin.blockchain.info;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

final class Response {

  private final Map<String, Balance> balances = new HashMap<>(1);

  @JsonAnySetter
  public void add(String address, Balance balance) {
    balances.put(address, balance);
  }

  public BigDecimal getFinalBalance(String address) {
    return balances.get(address)
        .finalBalance
        .movePointLeft(8);
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  record Balance(
      @JsonProperty("final_balance")
      BigDecimal finalBalance) {

  }
}
