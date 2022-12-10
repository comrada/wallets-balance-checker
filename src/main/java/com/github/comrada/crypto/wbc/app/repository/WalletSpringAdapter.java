package com.github.comrada.crypto.wbc.app.repository;

import com.github.comrada.crypto.wbc.app.entity.WalletEntity;
import com.github.comrada.crypto.wbc.app.mapper.WalletMapper;
import com.github.comrada.crypto.wbc.checker.WalletStorage;
import com.github.comrada.crypto.wbc.domain.Wallet;
import com.github.comrada.crypto.wbc.domain.WalletStatus;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.joining;

public class WalletSpringAdapter implements WalletStorage {

  private final WalletRepository walletRepository;
  private final EntityManager entityManager;
  private final WalletMapper mapper;

  public WalletSpringAdapter(WalletRepository walletRepository, EntityManager entityManager) {
    this.walletRepository = requireNonNull(walletRepository);
    this.entityManager = requireNonNull(entityManager);
    this.mapper = new WalletMapper();
  }

  @SuppressWarnings("unchecked")
  @Override
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public Optional<Wallet> selectForUpdate(Map<String, Set<String>> blockchains) {
    Optional<WalletEntity> foundWallet = entityManager.createNativeQuery("""
            select * from wallets w
              where
                (%s) and
                (w.checked_at <= current_timestamp - interval '1' day or
                w.checked_at is null) and
                w.locked = false and
                w.status = 'OK' and
                ((w.token = true and w.contract is not null) or (w.token = false))
            limit 1
            """.formatted(buildBlockchainsCondition(blockchains)), WalletEntity.class)
        .getResultStream().findFirst();
    if (foundWallet.isPresent()) {
      WalletEntity walletEntity = foundWallet.get();
      walletRepository.lock(walletEntity.getId());
      return Optional.of(mapper.map(walletEntity));
    }
    return empty();
  }

  private String buildBlockchainsCondition(Map<String, Set<String>> blockchains) {
    String conditionTemplate = "(w.blockchain = '%s' and w.asset in (%s))";
    return blockchains.entrySet().stream()
        .map(entry -> conditionTemplate.formatted(entry.getKey(), joinAssets(entry.getValue())))
        .collect(joining(" or "));
  }

  private String joinAssets(Set<String> assets) {
    return assets.stream()
        .map(asset -> "'" + asset + "'")
        .collect(joining(", "));
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

  @Override
  @Transactional
  public void invalidate(Wallet wallet) {
    walletRepository.changeStatus(mapper.mapWalletId(wallet), WalletStatus.INVALID, Instant.now());
  }
}
