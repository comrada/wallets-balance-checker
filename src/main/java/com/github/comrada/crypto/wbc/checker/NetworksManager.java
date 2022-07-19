package com.github.comrada.crypto.wbc.checker;

import static java.util.stream.Collectors.toUnmodifiableMap;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.domain.Wallet;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NetworksManager {

  private final Map<String, BlockchainApi> networks;

  public NetworksManager(Map<String, BlockchainApi> networks) {
    this.networks = new ConcurrentHashMap<>(networks);
  }

  public BigDecimal balance(Wallet wallet) {
    BlockchainApi blockchainApi = networks.get(wallet.blockchain());
    return blockchainApi.balance(wallet.address());
  }

  public Map<String, Set<String>> blockchains() {
    return networks.values().stream().collect(toUnmodifiableMap(BlockchainApi::name, BlockchainApi::assets));
  }
}
