package com.github.comrada.crypto.wbc.blockchain.exception;

public class NetworkException extends RuntimeException {

  public NetworkException(String message) {
    super(message);
  }

  public NetworkException(String message, Throwable cause) {
    super(message, cause);
  }

  public NetworkException(Throwable cause) {
    super(cause);
  }
}
