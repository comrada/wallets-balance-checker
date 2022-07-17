package com.github.comrada.crypto.wbc.blockchain.rest;

@FunctionalInterface
public interface ResponseMapper {

  <T> T map(String content, Class<T> responseType);
}
