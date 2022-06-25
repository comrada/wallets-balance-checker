package com.github.comrada.crypto.wbc.blockchain.networks.bitcoin;

import static java.util.Objects.requireNonNull;

import com.github.comrada.crypto.wbc.blockchain.exception.NetworkException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public abstract class BaseHttpClient {

  private final HttpClient client;

  protected BaseHttpClient(HttpClient client) {
    this.client = requireNonNull(client);
  }

  protected String get(String url) {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .build();
    try {
      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
      if (response.body() != null) {
        return response.body();
      }
      throw new NetworkException("HTTP response for [" + url + " is empty");
    } catch (Exception e) {
      throw new NetworkException(e);
    }
  }
}
