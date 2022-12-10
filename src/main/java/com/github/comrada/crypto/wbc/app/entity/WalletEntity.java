package com.github.comrada.crypto.wbc.app.entity;

import com.github.comrada.crypto.wbc.domain.WalletStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity(name = "Wallet")
@Table(name = "wallets",
    indexes = {
        @Index(name = "IDX_CHECKED_AT", columnList = "checked_at"),
        @Index(name = "IDX_BALANCE", columnList = "balance"),
        @Index(name = "IDX_STATUS", columnList = "status"),
        @Index(name = "IDX_CONTRACT", columnList = "contract")
    },
    uniqueConstraints = @UniqueConstraint(columnNames = {"blockchain", "address", "asset"})
)
public class WalletEntity {

  @EmbeddedId
  private WalletId id;

  @Column(precision = 19, scale = 2)
  private BigDecimal balance;

  @Column(name = "checked_at")
  private Instant checkedAt;

  @Column
  private boolean exchange;

  @Column
  private boolean locked = false;

  @Column
  private boolean token = false;

  @Column
  private String contract;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private WalletStatus status = WalletStatus.OK;

  public WalletEntity() {
  }

  public WalletId getId() {
    return id;
  }

  public void setId(WalletId id) {
    this.id = id;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public Instant getCheckedAt() {
    return checkedAt;
  }

  public void setCheckedAt(Instant checkedAt) {
    this.checkedAt = checkedAt;
  }

  public boolean isExchange() {
    return exchange;
  }

  public void setExchange(boolean exchange) {
    this.exchange = exchange;
  }

  public boolean isLocked() {
    return locked;
  }

  public void setLocked(boolean locked) {
    this.locked = locked;
  }

  public String getBlockchain() {
    return id.getBlockchain();
  }

  public String getAddress() {
    return id.getAddress();
  }

  public String getAsset() {
    return id.getAsset();
  }

  public WalletStatus getStatus() {
    return status;
  }

  public void setStatus(WalletStatus status) {
    this.status = status;
  }

  public boolean isToken() {
    return token;
  }

  public void setToken(boolean token) {
    this.token = token;
  }

  public String getContract() {
    return contract;
  }

  public void setContract(String contract) {
    this.contract = contract;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WalletEntity walletEntity = (WalletEntity) o;
    return id.equals(walletEntity.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Blockchain: " + id.getBlockchain() + ", address: " + id.getAddress() + ", asset: " + id.getAsset() +
        (balance != null ? ", balance: " + balance : "");
  }
}
