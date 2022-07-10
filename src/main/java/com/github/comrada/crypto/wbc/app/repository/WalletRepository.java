package com.github.comrada.crypto.wbc.app.repository;

import com.github.comrada.crypto.wbc.app.entity.WalletEntity;
import com.github.comrada.crypto.wbc.app.entity.WalletId;
import com.github.comrada.crypto.wbc.domain.WalletStatus;
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
        w.locked = false and
        w.status = 'OK'
      limit 1
      """, nativeQuery = true)
  Optional<WalletEntity> selectForUpdate(Set<String> blockchains);

  @Modifying(flushAutomatically = true)
  @Query(value = """
      update Wallet set locked = true
      where id.blockchain = :#{#walletId.blockchain} and id.address = :#{#walletId.address} and locked = false
      """)
  void lock(WalletId walletId);

  @Modifying(flushAutomatically = true)
  @Query(value = """
      update Wallet set locked = false, checkedAt = :checkedAt
      where id.blockchain = :#{#walletId.blockchain} and id.address = :#{#walletId.address} and locked = true
      """)
  void unlock(WalletId walletId, Instant checkedAt);

  @Modifying(flushAutomatically = true)
  @Query(value = """
      update Wallet set balance = :#{#walletEntity.balance}, checkedAt = :#{#walletEntity.checkedAt},
      locked = :#{#walletEntity.locked}
      where id.blockchain = :#{#walletEntity.id.blockchain} and id.address = :#{#walletEntity.id.address}
      """)
  void update(WalletEntity walletEntity);


  @Modifying(flushAutomatically = true)
  @Query(value = """
      update Wallet set status = :status, locked = false, checkedAt = :checkedAt
      where id.blockchain = :#{#walletId.blockchain} and id.address = :#{#walletId.address}
      """)
  void changeStatus(WalletId walletId, WalletStatus status, Instant checkedAt);
}
