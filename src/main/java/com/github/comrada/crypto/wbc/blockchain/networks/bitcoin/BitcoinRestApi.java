package com.github.comrada.crypto.wbc.blockchain.networks.bitcoin;

import static java.util.Objects.requireNonNull;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.blockchain.RoundRobinBalancer;
import java.math.BigDecimal;

public class BitcoinRestApi implements BlockchainApi {

  public static final String BLOCKCHAIN_NAME = "Bitcoin";
  private final RoundRobinBalancer balancer;

  public BitcoinRestApi(RoundRobinBalancer balancer) {
    this.balancer = requireNonNull(balancer);
  }

  @Override
  public String name() {
    return BLOCKCHAIN_NAME;
  }

  @Override
  public BigDecimal balance(String address) {
    return balancer.getBalance(address);
  }
}
