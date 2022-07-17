package com.github.comrada.crypto.wbc.app.mapper;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.comrada.crypto.wbc.blockchain.rest.ResponseMapper;
import java.io.UncheckedIOException;

public class JacksonResponseMapper implements ResponseMapper {

  private final ObjectMapper objectMapper;

  public JacksonResponseMapper(ObjectMapper objectMapper) {
    this.objectMapper = requireNonNull(objectMapper);
  }

  @Override
  public <T> T map(String content, Class<T> responseType) {
    try {
      return objectMapper.readValue(content, responseType);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }
}
