package com.github.comrada.crypto.wbc.blockchain.rest;

import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;

import com.github.comrada.crypto.wbc.blockchain.exception.NetworkException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;
import java.util.stream.Stream;

public abstract class BaseHttpClient {

  private final HttpClient client;
  private final ResponseMapper responseMapper;
  private final String[] headers;

  protected BaseHttpClient(HttpClient client, ResponseMapper responseMapper, Map<String, Object> headers) {
    this.client = requireNonNull(client);
    this.responseMapper = requireNonNull(responseMapper);
    this.headers = flatten(headers);
  }

  public BaseHttpClient(HttpClient client, ResponseMapper responseMapper) {
    this(client, responseMapper, emptyMap());
  }

  protected <T> T get(String url, Class<T> responseClass) {
    HttpRequest request = buildRequest(url);
    try {
      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
      if (response.body() != null) {
        return responseMapper.map(response.body(), responseClass);
      }
      throw new NetworkException("HTTP response for [" + url + " is empty");
    } catch (Exception e) {
      throw new NetworkException(e);
    }
  }

  private HttpRequest buildRequest(String url) {
    if (headers.length == 0) {
      return HttpRequest.newBuilder()
          .uri(URI.create(url))
          .build();
    }
    return HttpRequest.newBuilder()
        .headers(headers)
        .uri(URI.create(url))
        .build();
  }

  private String[] flatten(Map<String, Object> values) {
    if (values.isEmpty()) {
      return new String[0];
    }
    return values
        .entrySet()
        .stream()
        .flatMap(entry -> Stream.of(entry.getKey(), String.valueOf(entry.getValue())))
        .toArray(String[]::new);
  }
}
