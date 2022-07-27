package com.github.comrada.crypto.wbc.blockchain.networks.ethereum;

import static java.util.Collections.singleton;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.blockchain.exception.NetworkException;
import com.github.comrada.crypto.wbc.checker.NetworkConfig;
import com.github.comrada.crypto.wbc.domain.Wallet;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;

public final class EthereumApi implements BlockchainApi, AutoCloseable {

  public static final String BLOCKCHAIN_NAME = "Ethereum";
  public static final Set<String> SUPPORTED_ASSETS = singleton("ETH");
  private final Web3j client;
  private final int timeout;
  private final Set<String> usingAssets;

  public EthereumApi(NetworkConfig networkConfig) {
    timeout = networkConfig.getIntegerParam("timeout-sec", 10);
    usingAssets = networkConfig.getArray("assets", SUPPORTED_ASSETS);
    String nodeUrl = networkConfig.getStringParam("node-url");
    client = Web3j.build(new HttpService(nodeUrl));
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
    try {
      if (wallet.asset().equals("ETH")) {
        return getEthBalance(wallet.address());
      }
      return getContractBalance(wallet);
    } catch (Exception e) {
      throw new NetworkException(e);
    }
  }

  private BigDecimal getEthBalance(String address) throws Exception {
    EthGetBalance ethGetBalance = requestBalance(address);
    return convertBalance(ethGetBalance.getBalance());
  }

  private BigDecimal getContractBalance(Wallet wallet) {
    throw new IllegalArgumentException("Obtaining the balance of the contract is not implemented");
  }

  private BigDecimal convertBalance(BigInteger weiBalance) {
    return Convert.fromWei(weiBalance.toString(), Unit.ETHER);
  }

  private EthGetBalance requestBalance(String address) throws Exception {
    return client
        .ethGetBalance(address, DefaultBlockParameterName.LATEST)
        .sendAsync()
        .get(timeout, TimeUnit.SECONDS);
  }

  @Override
  public void close() {
    client.shutdown();
  }
}
