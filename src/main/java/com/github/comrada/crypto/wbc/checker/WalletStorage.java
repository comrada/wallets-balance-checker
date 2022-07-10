package com.github.comrada.crypto.wbc.checker;

import com.github.comrada.crypto.wbc.domain.Wallet;
import java.util.Optional;
import java.util.Set;

public interface WalletStorage {

  /**
   * Finds a wallet in the database that meets certain requirements and locks it.
   *
   * @param blockchains for which we need to find a wallet
   * @return a wallet or {@link Optional#empty()} if no wallet meets the conditions
   */
  Optional<Wallet> selectForUpdate(Set<String> blockchains);

  void update(Wallet wallet);

  void unlock(Wallet wallet);

  void invalidate(Wallet wallet);
}
