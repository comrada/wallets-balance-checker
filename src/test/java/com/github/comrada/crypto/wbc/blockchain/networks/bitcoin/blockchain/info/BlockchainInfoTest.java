package com.github.comrada.crypto.wbc.blockchain.networks.bitcoin.blockchain.info;

import static com.github.comrada.crypto.wbc.TestUtils.mockWallet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.comrada.crypto.wbc.app.mapper.JacksonResponseMapper;
import com.github.comrada.crypto.wbc.blockchain.rest.ResponseMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import org.junit.jupiter.api.Test;

class BlockchainInfoTest {

  @Test
  void balance() throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://blockchain.info/balance?active=15H8vDVWZvySPnYYTd4FmRUXnMAqykKTN3"))
        .build();
    HttpResponse<String> response = mock(HttpResponse.class);
    when(response.body()).thenReturn("""
        {
          "15H8vDVWZvySPnYYTd4FmRUXnMAqykKTN3": {
            "final_balance": 173123431080,
            "n_tx": 6,
            "total_received": 173123431080
          }
        }
        """);
    HttpClient client = mock(HttpClient.class);
    when(client.send(request, BodyHandlers.ofString())).thenReturn(response);

    ResponseMapper responseMapper = new JacksonResponseMapper(new ObjectMapper().findAndRegisterModules());
    BlockchainInfo blockchainInfo = new BlockchainInfo(client, responseMapper);
    BigDecimal actualBalance = blockchainInfo.balance(mockWallet("15H8vDVWZvySPnYYTd4FmRUXnMAqykKTN3"));
    assertEquals(BigDecimal.valueOf(1731.23431080).setScale(8, RoundingMode.HALF_UP), actualBalance);
  }
}