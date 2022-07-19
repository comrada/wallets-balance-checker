package com.github.comrada.crypto.wbc.blockchain.networks.ripple;

import static java.util.Collections.singleton;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.blockchain.exception.NetworkException;
import com.github.comrada.crypto.wbc.checker.NetworkConfig;
import java.math.BigDecimal;
import java.util.Set;
import okhttp3.HttpUrl;
import org.xrpl.xrpl4j.client.XrplClient;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

public final class RippleNet implements BlockchainApi {

  public static final String BLOCKCHAIN_NAME = "Ripple";
  public static final Set<String> SUPPORTED_ASSETS = singleton("XRP");
  private final XrplClient client;
  private final Set<String> usingAssets;

  public RippleNet(NetworkConfig networkConfig) {
    HttpUrl rippledUrl = HttpUrl.get(networkConfig.getStringParam("rippled-url"));
    usingAssets = networkConfig.getArray("assets", SUPPORTED_ASSETS);
    client = new XrplClient(rippledUrl);
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
    Address classicAddress = Address.of(address);
    AccountInfoRequestParams requestParams = AccountInfoRequestParams.of(classicAddress);
    try {
      AccountInfoResult accountInfoResult = client.accountInfo(requestParams);
      XrpCurrencyAmount currencyAmount = accountInfoResult.accountData().balance();
      return currencyAmount.toXrp();
    } catch (Exception e) {
      throw new NetworkException(e);
    }
  }
}
