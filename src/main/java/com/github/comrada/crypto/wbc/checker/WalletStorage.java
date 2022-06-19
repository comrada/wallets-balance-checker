package com.github.comrada.crypto.wbc.checker;

import com.github.comrada.crypto.wbc.checker.entity.Wallet;
import java.util.Optional;
import java.util.Set;

public interface WalletStorage {

  /**
   * Finds a wallet in the database that meets certain requirements and locks it.
   *
   * @param assets for which we need to find a wallet
   * @return a wallet or {@link Optional#empty()} if no wallet meets the conditions
   */
  Optional<Wallet> selectForUpdate(Set<String> assets);

  Wallet update(Wallet wallet);
}
