package com.github.comrada.crypto.wbc.blockchain.networks.tron;

import com.github.comrada.crypto.wbc.blockchain.ContractFacade;
import com.github.comrada.crypto.wbc.blockchain.exception.InvalidWalletException;
import com.github.comrada.crypto.wbc.domain.Wallet;
import com.github.comrada.tron4j.client.TronClient;
import com.github.comrada.tron4j.client.contract.Contract;
import com.github.comrada.tron4j.client.contract.Trc20Contract;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class Tron4jClient implements ContractFacade, AutoCloseable {

  private final TronClient client;

  public Tron4jClient(String hexPrivateKey) {
    client = TronClient.ofMainnet(hexPrivateKey);
  }

  @Override
  public BigDecimal balanceOf(Wallet wallet) {
    Trc20Contract trc20Contract = contractFor(wallet);
    String symbol = trc20Contract.symbol();
    if (!symbol.equals(wallet.asset())) {
      throw new InvalidWalletException("The symbol '%s' in the contract '%s' does not match the wallet's asset '%s'"
          .formatted(symbol, wallet.contract(), wallet.asset()));
    }
    BigInteger balance = trc20Contract.balanceOf(wallet.address());
    BigInteger decimals = trc20Contract.decimals();
    return new BigDecimal(balance).movePointLeft(decimals.intValue());
  }

  private Trc20Contract contractFor(Wallet wallet) {
    Contract contract = client.getContract(wallet.contract());
    return new Trc20Contract(contract, wallet.address(), client);
  }

  @Override
  public String symbol(Wallet wallet) {
    return contractFor(wallet).symbol();
  }

  @Override
  public int decimals(Wallet wallet) {
    return contractFor(wallet).decimals().intValue();
  }

  @Override
  public void close() {
    client.close();
  }
}
