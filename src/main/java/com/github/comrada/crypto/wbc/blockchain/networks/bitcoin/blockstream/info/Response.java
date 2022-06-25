package com.github.comrada.crypto.wbc.blockchain.networks.bitcoin.blockstream.info;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Response(
    @JsonProperty
    String address,
    @JsonProperty("chain_stats")
    ChainStats chainStats
) {

  BigDecimal getFinalBalance() {
    return chainStats.fundedTxoSum
        .subtract(chainStats.spentTxoSum)
        .movePointLeft(8);
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record ChainStats(
      @JsonProperty("funded_txo_sum")
      BigDecimal fundedTxoSum,
      @JsonProperty("spent_txo_sum")
      BigDecimal spentTxoSum
  ) {

  }
}
