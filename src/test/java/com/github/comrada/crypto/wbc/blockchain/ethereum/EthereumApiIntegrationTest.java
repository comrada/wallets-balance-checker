package com.github.comrada.crypto.wbc.blockchain.ethereum;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.comrada.crypto.wbc.app.config.NetworkParameters;
import com.github.comrada.crypto.wbc.blockchain.networks.ethereum.EthereumApi;
import com.github.comrada.crypto.wbc.checker.NetworkConfig;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;

class EthereumApiIntegrationTest {

  private final NetworkConfig networkConfig;

  public EthereumApiIntegrationTest() throws IOException {
    MockWebServer server = new MockWebServer();
    server.enqueue(new MockResponse().setBody("""
        {
          "jsonrpc": "2.0",
          "id": 0,
          "result": "0x5603e2978182879d7ce"
        }
        """));
    server.start();
    networkConfig = new NetworkParameters.Blockchain("Ethereum", Map.of(
        "node-url", server.url("").toString(),
        "timeout-sec", "10"
    ));
  }

  @Test
  void balance() {
    try (EthereumApi ethereumApi = new EthereumApi(networkConfig)) {
      BigDecimal actualBalance = ethereumApi.balance("0x72A53cDBBcc1b9efa39c834A540550e23463AAcB");
      assertEquals(new BigDecimal("25387.199088773893314510"), actualBalance);
    }
  }

  @Test
  void name() {
    try (EthereumApi ethereumApi = new EthereumApi(networkConfig)) {
      assertEquals("Ethereum", ethereumApi.name());
    }
  }
}