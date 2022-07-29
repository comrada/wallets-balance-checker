package com.github.comrada.crypto.wbc.blockchain.networks.tron;

import static com.github.comrada.crypto.wbc.TestUtils.mockWallet;
import static com.github.comrada.crypto.wbc.TestUtils.readFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.comrada.crypto.wbc.app.config.NetworkParameters;
import com.github.comrada.crypto.wbc.app.mapper.JacksonResponseMapper;
import com.github.comrada.crypto.wbc.blockchain.ContractFacade;
import com.github.comrada.crypto.wbc.blockchain.rest.ResponseMapper;
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

class TronGridTest {

  private HttpClient client;
  private TronGrid testNetwork;

  @BeforeEach
  void initConfig() {
    NetworkConfig networkConfig = new NetworkParameters.Blockchain("Tron", Map.of(
        "api-key", "fake-token",
        "assets", "TRX"
    ));
    client = mock(HttpClient.class);
    ResponseMapper responseMapper = new JacksonResponseMapper(new ObjectMapper().findAndRegisterModules());
    ContractFacade contractFacade = mock(ContractFacade.class);
    testNetwork = new TronGrid(client, responseMapper, networkConfig, contractFacade);
  }

  @Test
  void name() {
    assertEquals("Tron", testNetwork.name());
  }

  @Test
  void balance() throws Exception {
    HttpRequest request = HttpRequest.newBuilder()
        .header("TRON-PRO-API-KEY", "fake-token")
        .uri(URI.create("https://api.trongrid.io/v1/accounts/TYL7z7VSVRShLoJ6YRQMA4t9pSECt9ZLmz?only_confirmed=true"))
        .build();
    HttpResponse<String> response = mock(HttpResponse.class);
    String accountResponse = readFile(TronGridTest.class, "account-response-with-frozen-balance-and-gas.json");
    when(response.body()).thenReturn(accountResponse);
    when(client.send(request, BodyHandlers.ofString())).thenReturn(response);
    BigDecimal balance = testNetwork.balance(mockWallet("Tron", "TYL7z7VSVRShLoJ6YRQMA4t9pSECt9ZLmz", "TRX"));
    assertEquals(new BigDecimal("160488.731990"), balance);
  }
}