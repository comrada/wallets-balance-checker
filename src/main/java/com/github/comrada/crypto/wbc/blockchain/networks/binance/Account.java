package com.github.comrada.crypto.wbc.blockchain.networks.binance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
class Account {

  @JsonProperty("account_number")
  private Integer accountNumber;
  private String address;
  private List<Balance> balances;
  @JsonProperty("public_key")
  private List<Integer> publicKey;
  private Long sequence;

  public Integer getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(Integer accountNumber) {
    this.accountNumber = accountNumber;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public List<Balance> getBalances() {
    return balances;
  }

  public void setBalances(List<Balance> balances) {
    this.balances = balances;
  }

  public List<Integer> getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(List<Integer> publicKey) {
    this.publicKey = publicKey;
  }

  public Long getSequence() {
    return sequence;
  }

  public void setSequence(Long sequence) {
    this.sequence = sequence;
  }
}
