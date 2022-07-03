package com.github.comrada.crypto.wbc.blockchain.networks.bitcoin;

import static java.util.Objects.requireNonNull;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.blockchain.RoundRobinBalancer;
import java.math.BigDecimal;

public class BitcoinWeb implements BlockchainApi {

  private final RoundRobinBalancer balancer;

  public BitcoinWeb(RoundRobinBalancer balancer) {
    this.balancer = requireNonNull(balancer);
  }

  @Override
  public String name() {
    return "Bitcoin";
  }

  @Override
  public BigDecimal balance(String address) {
    return balancer.getBalance(address);
  }
}
