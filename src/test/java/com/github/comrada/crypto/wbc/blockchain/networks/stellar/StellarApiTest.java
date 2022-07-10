package com.github.comrada.crypto.wbc.blockchain.networks.stellar;

import static com.github.comrada.crypto.wbc.TestUtils.readFile;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.comrada.crypto.wbc.app.config.NetworkParameters;
import com.github.comrada.crypto.wbc.checker.NetworkConfig;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;

class StellarApiTest {

  private final NetworkConfig networkConfig;

  public StellarApiTest() throws IOException {
    MockWebServer server = new MockWebServer();
    String accountResponse = readFile(StellarApiTest.class, "account-response.json");
    server.enqueue(new MockResponse().setBody(accountResponse));
    server.start();
    networkConfig = new NetworkParameters.Blockchain("Stellar", Map.of(
        "horizon-url", server.url("").toString(),
        "asset", "native"
    ));
  }

  @Test
  void balance() {
    try (StellarApi stellarApi = new StellarApi(networkConfig)) {
      BigDecimal actualBalance = stellarApi.balance("GCWEER57MBVRXA4I426VL3PSWWM72SSZ3AZ5TGBDSWJMTDFVCABWNZIF");
      assertEquals(new BigDecimal("1253814448.0501659"), actualBalance);
    }
  }

  @Test
  void name() {
    try (StellarApi stellarApi = new StellarApi(networkConfig)) {
      assertEquals("Stellar", stellarApi.name());
    }
  }
}