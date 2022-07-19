package com.github.comrada.crypto.wbc.blockchain.networks.bitcoin;

import static java.util.Collections.singleton;
import static java.util.Objects.requireNonNull;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.blockchain.RoundRobinBalancer;
import com.github.comrada.crypto.wbc.checker.NetworkConfig;
import java.math.BigDecimal;
import java.util.Set;

public class BitcoinRestApi implements BlockchainApi {

  public static final String BLOCKCHAIN_NAME = "Bitcoin";
  public static final Set<String> SUPPORTED_ASSETS = singleton("BTC");
  private final RoundRobinBalancer balancer;
  private final Set<String> usingAssets;

  public BitcoinRestApi(NetworkConfig networkConfig, RoundRobinBalancer balancer) {
    this.balancer = requireNonNull(balancer);
    this.usingAssets = networkConfig.getArray("assets", SUPPORTED_ASSETS);
  }

  @Override
  public String name() {
    return BLOCKCHAIN_NAME;
  }

  @Override
  public Set<String> assets() {
    return usingAssets;
  }

  @Override
  public BigDecimal balance(String address) {
    return balancer.getBalance(address);
  }
}
