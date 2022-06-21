package com.github.comrada.crypto.wbc.checker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.checker.entity.Wallet;
import com.github.comrada.crypto.wbc.checker.entity.WalletId;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NetworksManagerIntegrationTest {

  private NetworksManager networksManager;
  private Wallet xrpWallet;
  private Wallet ethWallet;

  @BeforeEach
  void initNetworksManager() {
    xrpWallet = mockWallet("XRP", "r3qZhy6sKxn43uaHMnEMKQ6tpzhCo18ULZ");
    ethWallet = mockWallet("ETH", "0x0259512d4c4386327a5a2faf78fbabed7202c971");
    BlockchainApi ripple = mock(BlockchainApi.class);
    when(ripple.balance((xrpWallet.getAddress()))).thenReturn(BigDecimal.valueOf(1));
    BlockchainApi ethereum = mock(BlockchainApi.class);
    when(ethereum.balance((ethWallet.getAddress()))).thenReturn(BigDecimal.valueOf(2));
    networksManager = new NetworksManager(Map.of(
        "XRP", ripple,
        "ETH", ethereum
    ));
  }

  @Test
  void balance() {
    assertEquals(BigDecimal.valueOf(1), networksManager.balance(xrpWallet));
    assertEquals(BigDecimal.valueOf(2), networksManager.balance(ethWallet));
  }

  @Test
  void assets() {
    assertEquals(Set.of("XRP", "ETH"), networksManager.assets());
  }

  private Wallet mockWallet(String asset, String address) {
    Wallet wallet = new Wallet();
    WalletId id = new WalletId(asset, address);
    wallet.setId(id);
    return wallet;
  }
}