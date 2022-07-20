package com.github.comrada.crypto.wbc.blockchain.networks.stellar;

import static java.util.Collections.singleton;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.blockchain.exception.InvalidWalletException;
import com.github.comrada.crypto.wbc.blockchain.exception.NetworkException;
import com.github.comrada.crypto.wbc.checker.NetworkConfig;
import com.github.comrada.crypto.wbc.domain.Wallet;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Stream;
import org.stellar.sdk.Server;
import org.stellar.sdk.requests.ErrorResponse;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.AccountResponse.Balance;

public final class StellarApi implements BlockchainApi, AutoCloseable {

  public static final String BLOCKCHAIN_NAME = "Stellar";
  public static final Set<String> SUPPORTED_ASSETS = singleton("XLM");
  private final Server server;
  private final String asset;
  private final Set<String> usingAssets;

  public StellarApi(NetworkConfig networkConfig) {
    asset = networkConfig.getStringParam("asset-id");
    usingAssets = networkConfig.getArray("assets", SUPPORTED_ASSETS);
    String horizonUrl = networkConfig.getStringParam("horizon-url");
    server = new Server(horizonUrl);
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
  public BigDecimal balance(Wallet wallet) {
    String address = wallet.address();
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
    } catch (ErrorResponse e) {
      if (e.getCode() == 404) {
        throw new InvalidWalletException("Wallet '" + address + "' not found");
      }
      throw new NetworkException(e);
    }
  }

  @Override
  public void close() {
    server.close();
  }
}
