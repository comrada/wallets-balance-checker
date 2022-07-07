package com.github.comrada.crypto.wbc.app.repository;

import com.github.comrada.crypto.wbc.app.entity.WalletEntity;
import com.github.comrada.crypto.wbc.app.entity.WalletId;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<WalletEntity, WalletId> {

  @Query(value = """
      select * from wallets w
      where
        w.blockchain in :blockchains and
        (w.checked_at <= current_timestamp - interval '1' day or
        w.checked_at is null) and
        w.locked = false
      limit 1
      """, nativeQuery = true)
  Optional<WalletEntity> selectForUpdate(Set<String> blockchains);

  @Modifying(flushAutomatically = true)
  @Query(value = """
      update wallets set locked = true
      where blockchain = :#{#walletId.blockchain} and address = :#{#walletId.address} and locked = false
      """, nativeQuery = true)
  void lock(WalletId walletId);

  @Modifying(flushAutomatically = true)
  @Query(value = """
      update wallets set locked = false, checked_at = :checkedAt
      where blockchain = :#{#walletId.blockchain} and address = :#{#walletId.address} and locked = true
      """, nativeQuery = true)
  void unlock(WalletId walletId, Instant checkedAt);

  @Modifying(flushAutomatically = true)
  @Query(value = """
      update wallets set balance = :#{#walletEntity.balance}, checked_at = :#{#walletEntity.checkedAt},
      locked = :#{#walletEntity.locked}
      where blockchain = :#{#walletEntity.id.blockchain} and address = :#{#walletEntity.id.address}
      """, nativeQuery = true)
  void update(WalletEntity walletEntity);
}
