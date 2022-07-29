package com.github.comrada.crypto.wbc.blockchain.networks.tron;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
record Account(List<AccountData> data) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  record AccountData(
      @JsonProperty("account_resource")
      AccountResource accountResource,
      @JsonProperty
      List<Frozen> frozen,
      @JsonProperty
      BigDecimal balance,
      @JsonProperty
      List<Trc20Contract> trc20
  ) {

    public Map<String, BigDecimal> trc20Balances() {
      return trc20.stream()
          .map(Trc20Contract::getBalances)
          .flatMap(c -> c.entrySet().stream())
          .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  record AccountResource(
      @JsonProperty("frozen_balance_for_energy")
      Frozen frozenBalanceForEnergy
  ) {

  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  record Frozen(
      @JsonProperty("frozen_balance")
      BigDecimal frozenBalance
  ) {

  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  static class Trc20Contract {

    private final Map<String, BigDecimal> balances = new HashMap<>(1);

    public Map<String, BigDecimal> getBalances() {
      return balances;
    }

    @JsonAnySetter
    public void add(String address, BigDecimal balance) {
      balances.put(address, balance);
    }
  }
}
