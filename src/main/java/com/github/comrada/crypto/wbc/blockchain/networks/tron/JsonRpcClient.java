package com.github.comrada.crypto.wbc.blockchain.networks.tron;

import com.github.comrada.crypto.wbc.blockchain.ContractFacade;
import com.github.comrada.crypto.wbc.blockchain.ContractHelper;
import com.github.comrada.crypto.wbc.domain.Wallet;
import com.github.comrada.tron4j.client.TronClient;
import com.github.comrada.tron4j.client.contract.Contract;
import com.github.comrada.tron4j.client.contract.Trc20Contract;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class JsonRpcClient implements ContractFacade, AutoCloseable {

  private final TronClient client;
  private final ContractHelper contractHelper;
  private final Map<String, Trc20Contract> contractsCache = new ConcurrentHashMap<>();

  public JsonRpcClient(String hexPrivateKey) {
    this.client = TronClient.ofMainnet(hexPrivateKey);
    this.contractHelper = new ContractHelper();
  }

  @Override
  public BigDecimal balanceOf(Wallet wallet) {
    Trc20Contract trc20Contract = contractFor(wallet);
    symbol(wallet);
    BigInteger balance = trc20Contract.balanceOf(wallet.address());
    int decimals = decimals(wallet);
    return new BigDecimal(balance).movePointLeft(decimals);
  }

  private Trc20Contract contractFor(Wallet wallet) {
    return contractsCache.computeIfAbsent(wallet.contract(), address -> {
      Contract contract = client.getContract(wallet.contract());
      return new Trc20Contract(contract, wallet.address(), client);
    });
  }

  @Override
  public String symbol(Wallet wallet) {
    return contractHelper.getSymbol(wallet, () -> contractFor(wallet).symbol());
  }

  @Override
  public int decimals(Wallet wallet) {
    return contractHelper.getDecimals(wallet, () -> contractFor(wallet).decimals().intValue());
  }

  @Override
  public void close() {
    client.close();
  }
}
