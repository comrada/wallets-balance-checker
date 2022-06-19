package com.github.comrada.crypto.wbc.app.repository;

import com.github.comrada.crypto.wbc.checker.entity.Wallet;
import com.github.comrada.crypto.wbc.checker.entity.WalletId;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, WalletId> {

  @Query(value = """
      select * from wallets w
      where
        w.asset in :assets and
        (w.checked_at <= current_timestamp - interval '1' day or
        w.checked_at is null) and
        w.locked = false
      limit 1
      """, nativeQuery = true)
  Optional<Wallet> selectForUpdate(Set<String> assets);
}
