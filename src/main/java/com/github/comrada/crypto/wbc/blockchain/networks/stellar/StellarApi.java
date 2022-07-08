package com.github.comrada.crypto.wbc.blockchain.networks.stellar;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.blockchain.exception.NetworkException;
import com.github.comrada.crypto.wbc.checker.NetworkConfig;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.stream.Stream;
import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.AccountResponse.Balance;

public final class StellarApi implements BlockchainApi, AutoCloseable {

  public static final String BLOCKCHAIN_NAME = "Stellar";
  private final Server server;
  private final String asset;

  public StellarApi(NetworkConfig networkConfig) {
    String horizonUrl = networkConfig.getStringParam("horizon-url");
    server = new Server(horizonUrl);
    asset = networkConfig.getStringParam("asset");
  }

  @Override
  public String name() {
    return BLOCKCHAIN_NAME;
  }

  @Override
  public BigDecimal balance(String address) {
    try {
      AccountResponse account = server.accounts().account(address);
      return Stream.of(account.getBalances())
          .filter(balance -> balance.getAssetType().equals(asset))
          .findFirst()
          .map(Balance::getBalance)
          .map(BigDecimal::new)
          .orElseThrow(() -> new NetworkException("XLS balance not found, address: " + address));
    } catch (IOException e) {
      throw new NetworkException(e);
    }
  }

  @Override
  public void close() {
    server.close();
  }
}
