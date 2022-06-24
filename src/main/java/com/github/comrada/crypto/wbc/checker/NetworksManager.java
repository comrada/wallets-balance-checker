package com.github.comrada.crypto.wbc.checker;

import static java.util.Collections.unmodifiableSet;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.checker.entity.Wallet;
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
    BlockchainApi blockchainApi = networks.get(wallet.getBlockchain());
    return blockchainApi.balance(wallet.getAddress());
  }

  public Set<String> blockchains() {
    return unmodifiableSet(networks.keySet());
  }
}
