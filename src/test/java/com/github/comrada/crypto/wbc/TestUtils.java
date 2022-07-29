package com.github.comrada.crypto.wbc;

import com.github.comrada.crypto.wbc.domain.Wallet;
import com.github.comrada.crypto.wbc.domain.WalletStatus;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

public final class TestUtils {

  private TestUtils() {
  }

  public static String readFile(Class<?> loader, String fileName) throws IOException {
    try (InputStream inputStream = loader.getResourceAsStream(fileName)) {
      return new String(inputStream.readAllBytes());
    }
  }

  public static Wallet mockWallet(String address) {
    return mockWallet("testchain", address);
  }

  public static Wallet mockWallet(String blockchain, String address) {
    return mockWallet(blockchain, address, "TKN");
  }

  public static Wallet mockWallet(String blockchain, String address, String asset) {
    return mockWallet(blockchain, address, asset, BigDecimal.ZERO);
  }

  public static Wallet mockWallet(String blockchain, String address, String asset, BigDecimal balance) {
    return new Wallet(blockchain, address, asset, balance, false, WalletStatus.OK);
  }

  public static Wallet mockWallet(String blockchain, String address, String asset, BigDecimal balance, boolean token,
      String contract) {
    return new Wallet(blockchain, address, asset, balance, false, WalletStatus.OK, token, contract);
  }
}
