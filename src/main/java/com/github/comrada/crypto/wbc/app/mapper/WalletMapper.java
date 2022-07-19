package com.github.comrada.crypto.wbc.app.mapper;

import com.github.comrada.crypto.wbc.app.entity.WalletEntity;
import com.github.comrada.crypto.wbc.app.entity.WalletId;
import com.github.comrada.crypto.wbc.domain.Wallet;

public final class WalletMapper {

  public WalletId mapWalletId(Wallet wallet) {
    return new WalletId(wallet.blockchain(), wallet.address(), wallet.asset());
  }

  public WalletEntity map(Wallet wallet) {
    WalletId walletId = new WalletId(wallet.blockchain(), wallet.address(), wallet.asset());
    WalletEntity entity = new WalletEntity();
    entity.setId(walletId);
    entity.setBalance(wallet.balance());
    entity.setExchange(wallet.exchange());
    entity.setStatus(wallet.status());
    return entity;
  }

  public Wallet map(WalletEntity walletEntity) {
    return new Wallet(
        walletEntity.getBlockchain(),
        walletEntity.getAddress(),
        walletEntity.getAsset(),
        walletEntity.getBalance(),
        walletEntity.isExchange(),
        walletEntity.getStatus()
    );
  }
}
