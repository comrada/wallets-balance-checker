package com.github.comrada.crypto.wbc.blockchain.networks.bitcoin.blockstream.info;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.comrada.crypto.wbc.app.mapper.JacksonResponseMapper;
import com.github.comrada.crypto.wbc.blockchain.exception.NetworkException;
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

class BlockstreamInfoTest {

  @Test
  void balance() throws IOException, InterruptedException {
    HttpRequest request = buildHttpRequest();
    HttpResponse<String> response = mock(HttpResponse.class);
    when(response.body()).thenReturn("""
        {
           "address": "15H8vDVWZvySPnYYTd4FmRUXnMAqykKTN3",
           "chain_stats": {
             "funded_txo_count": 6,
             "funded_txo_sum": 173123431080,
             "spent_txo_count": 0,
             "spent_txo_sum": 0,
             "tx_count": 6
           },
           "mempool_stats": {
             "funded_txo_count": 0,
             "funded_txo_sum": 0,
             "spent_txo_count": 0,
             "spent_txo_sum": 0,
             "tx_count": 0
           }
         }
        """);
    HttpClient client = mock(HttpClient.class);
    when(client.send(request, BodyHandlers.ofString())).thenReturn(response);

    ResponseMapper responseMapper = new JacksonResponseMapper(new ObjectMapper().findAndRegisterModules());
    BlockstreamInfo blockstreamInfo = new BlockstreamInfo(client, responseMapper);
    BigDecimal actualBalance = blockstreamInfo.balance("15H8vDVWZvySPnYYTd4FmRUXnMAqykKTN3");
    assertEquals(BigDecimal.valueOf(1731.23431080).setScale(8, RoundingMode.HALF_UP), actualBalance);
  }

  @Test
  void whenBlockstreamReturnsWrongWalletBalance_thenThrowsException() throws IOException, InterruptedException {
    HttpRequest request = buildHttpRequest();
    HttpResponse<String> response = mock(HttpResponse.class);
    when(response.body()).thenReturn("""
        {
          "address": "1P5ZEDWTKTFGxQjZphgWPQUpe554WKDfHQ",
          "chain_stats": {
            "funded_txo_count": 755,
            "funded_txo_sum": 22988942834381,
            "spent_txo_count": 82,
            "spent_txo_sum": 9966188448603,
            "tx_count": 756
          },
          "mempool_stats": {
            "funded_txo_count": 0,
            "funded_txo_sum": 0,
            "spent_txo_count": 0,
            "spent_txo_sum": 0,
            "tx_count": 0
          }
        }
        """);
    HttpClient client = mock(HttpClient.class);
    when(client.send(request, BodyHandlers.ofString())).thenReturn(response);

    ResponseMapper responseMapper = new JacksonResponseMapper(new ObjectMapper().findAndRegisterModules());
    BlockstreamInfo blockstreamInfo = new BlockstreamInfo(client, responseMapper);
    assertThrows(NetworkException.class, () -> blockstreamInfo.balance("15H8vDVWZvySPnYYTd4FmRUXnMAqykKTN3"));
  }

  private HttpRequest buildHttpRequest() {
    return HttpRequest.newBuilder()
        .uri(URI.create("https://blockstream.info/api/address/15H8vDVWZvySPnYYTd4FmRUXnMAqykKTN3"))
        .build();
  }
}