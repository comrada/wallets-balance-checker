package com.github.comrada.crypto.wbc.blockchain.networks.ethereum;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.blockchain.exception.NetworkException;
import com.github.comrada.crypto.wbc.checker.NetworkConfig;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;

public class EthereumApi implements BlockchainApi {

  private final Web3j client;
  private final int timeout;

  public EthereumApi(NetworkConfig networkConfig) {
    timeout = networkConfig.getIntegerParam("timeout-sec", 10);
    String nodeUrl = networkConfig.getStringParam("node-url");
    client = Web3j.build(new HttpService(nodeUrl));
  }

  @Override
  public String name() {
    return "Ethereum";
  }

  @Override
  public BigDecimal balance(String address) {
    try {
      EthGetBalance ethGetBalance = requestBalance(address);
      return new BigDecimal(ethGetBalance.getBalance())
          .divide(new BigDecimal(1_000_000_000_000_000_000L), 18, RoundingMode.HALF_UP);
    } catch (Exception e) {
      throw new NetworkException(e);
    }
  }

  private EthGetBalance requestBalance(String address) throws Exception {
    return client
        .ethGetBalance(address, DefaultBlockParameterName.LATEST)
        .sendAsync()
        .get(timeout, TimeUnit.SECONDS);
  }

  @Override
  public String asset() {
    return "ETH";
  }
}
