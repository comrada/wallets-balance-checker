package com.github.comrada.crypto.wbc.app.repository;

import static java.util.Objects.requireNonNull;

import com.github.comrada.crypto.wbc.checker.WalletStorage;
import com.github.comrada.crypto.wbc.checker.entity.Wallet;
import java.util.Optional;
import java.util.Set;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

public class WalletSpringAdapter implements WalletStorage {

  private final WalletRepository walletRepository;

  public WalletSpringAdapter(WalletRepository walletRepository) {
    this.walletRepository = requireNonNull(walletRepository);
  }

  @Override
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public Optional<Wallet> selectForUpdate(Set<String> assets) {
    Optional<Wallet> foundWallet = walletRepository.selectForUpdate(assets);
    if (foundWallet.isPresent()) {
      Wallet wallet = foundWallet.get();
      wallet.setLocked(true);
      walletRepository.save(wallet);
    }
    return foundWallet;
  }

  @Override
  @Transactional
  public Wallet update(Wallet wallet) {
    return walletRepository.save(wallet);
  }
}
