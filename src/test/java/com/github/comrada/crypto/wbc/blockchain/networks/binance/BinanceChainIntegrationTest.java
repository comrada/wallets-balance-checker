package com.github.comrada.crypto.wbc.blockchain.networks.binance;

import static com.github.comrada.crypto.wbc.TestUtils.readFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.comrada.crypto.wbc.app.config.NetworkParameters;
import com.github.comrada.crypto.wbc.checker.NetworkConfig;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BinanceChainIntegrationTest {

  private NetworkConfig networkConfig;
  private HttpClient client;

  @BeforeEach
  void initConfig() {
    networkConfig = new NetworkParameters.Blockchain("Binance Chain", Map.of(
        "asset", "BNB"
    ));
    client = mock(HttpClient.class);
  }

  @Test
  void name() {
    BinanceChain binanceChain = new BinanceChain(client, networkConfig);
    assertEquals("Binance Chain", binanceChain.name());
  }

  @Test
  void balance() throws Exception {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://dex.binance.org/api/v1/account/bnb1fnd0k5l4p3ck2j9x9dp36chk059w977pszdgdz"))
        .build();
    HttpResponse<String> response = mock(HttpResponse.class);
    String accountResponse = readFile(BinanceChainIntegrationTest.class, "account-response.json");
    when(response.body()).thenReturn(accountResponse);
    when(client.send(request, BodyHandlers.ofString())).thenReturn(response);

    BinanceChain binanceChain = new BinanceChain(client, networkConfig);
    BigDecimal balance = binanceChain.balance("bnb1fnd0k5l4p3ck2j9x9dp36chk059w977pszdgdz");
    assertEquals(new BigDecimal("116495.80984738"), balance);
  }
}