package com.github.comrada.crypto.wbc.blockchain.networks.tron;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
record Account(List<AccountData> data) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  record AccountData(
      @JsonProperty("account_resource")
      AccountResource accountResource,
      @JsonProperty
      List<Frozen> frozen,
      @JsonProperty
      BigDecimal balance
  ) {

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
}
