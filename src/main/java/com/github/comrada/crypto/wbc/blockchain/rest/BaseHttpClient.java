package com.github.comrada.crypto.wbc.blockchain.rest;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.comrada.crypto.wbc.blockchain.exception.NetworkException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public abstract class BaseHttpClient {

  private final HttpClient client;
  private final ObjectMapper objectMapper;

  protected BaseHttpClient(HttpClient client) {
    this.client = requireNonNull(client);
    this.objectMapper = new ObjectMapper().findAndRegisterModules();
  }

  protected <T> T get(String url, Class<T> responseClass) {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .build();
    try {
      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
      if (response.body() != null) {
        return objectMapper.readValue(response.body(), responseClass);
      }
      throw new NetworkException("HTTP response for [" + url + " is empty");
    } catch (Exception e) {
      throw new NetworkException(e);
    }
  }
}
