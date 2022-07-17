package com.github.comrada.crypto.wbc.blockchain.networks.binance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
class Balance {

  private String symbol;
  private String free;
  private String locked;
  private String frozen;

  public Balance() {
  }

  public Balance(String symbol, String free, String locked, String frozen) {
    this.symbol = symbol;
    this.free = free;
    this.locked = locked;
    this.frozen = frozen;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public String getFree() {
    return free;
  }

  public void setFree(String free) {
    this.free = free;
  }

  public String getLocked() {
    return locked;
  }

  public void setLocked(String locked) {
    this.locked = locked;
  }

  public String getFrozen() {
    return frozen;
  }

  public void setFrozen(String frozen) {
    this.frozen = frozen;
  }
}
