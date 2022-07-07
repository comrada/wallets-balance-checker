package com.github.comrada.crypto.wbc.app.repository;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;

import com.github.comrada.crypto.wbc.app.entity.WalletEntity;
import com.github.comrada.crypto.wbc.app.mapper.WalletMapper;
import com.github.comrada.crypto.wbc.checker.WalletStorage;
import com.github.comrada.crypto.wbc.domain.Wallet;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

public class WalletSpringAdapter implements WalletStorage {

  private final WalletRepository walletRepository;
  private final WalletMapper mapper;

  public WalletSpringAdapter(WalletRepository walletRepository) {
    this.walletRepository = requireNonNull(walletRepository);
    this.mapper = new WalletMapper();
  }

  @Override
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public Optional<Wallet> selectForUpdate(Set<String> blockchains) {
    Optional<WalletEntity> foundWallet = walletRepository.selectForUpdate(blockchains);
    if (foundWallet.isPresent()) {
      WalletEntity walletEntity = foundWallet.get();
      walletRepository.lock(walletEntity.getId());
      return Optional.of(mapper.map(walletEntity));
    }
    return empty();
  }

  @Override
  @Transactional
  public void update(Wallet wallet) {
    WalletEntity walletEntity = mapper.map(wallet);
    walletEntity.setCheckedAt(Instant.now());
    walletEntity.setLocked(false);
    walletRepository.update(walletEntity);
  }

  @Override
  @Transactional
  public void unlock(Wallet wallet) {
    walletRepository.unlock(mapper.mapWalletId(wallet), Instant.now());
  }
}
