package com.github.comrada.crypto.wbc.blockchain.networks.tron;

import com.github.comrada.crypto.wbc.blockchain.networks.tron.Account.AccountData;
import com.github.comrada.crypto.wbc.blockchain.networks.tron.Account.Frozen;
import java.math.BigDecimal;
import java.util.Optional;

public final class BalanceExtractor {

  public BigDecimal extract(Account account) {
    if (account.data() == null || account.data().isEmpty()) {
      return BigDecimal.ZERO;
    }
    return account.data().stream()
        .map(this::getBalanceFromAccountData)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .movePointLeft(6);
  }

  private BigDecimal getBalanceFromAccountData(AccountData accountData) {
    BigDecimal balance = getBalance(accountData);
    BigDecimal frozenBalance = getFrozenBalance(accountData).orElse(BigDecimal.ZERO);
    BigDecimal frozenBalanceForEnergy = getFrozenBalanceForEnergy(accountData).orElse(BigDecimal.ZERO);
    return balance.add(frozenBalance).add(frozenBalanceForEnergy);
  }

  private BigDecimal getBalance(AccountData accountData) {
    return accountData.balance();
  }

  private Optional<BigDecimal> getFrozenBalance(AccountData accountData) {
    if (accountData.frozen() != null && !accountData.frozen().isEmpty()) {
      BigDecimal sumOfFrozenBalance = accountData.frozen().stream()
          .map(Frozen::frozenBalance)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
      return Optional.of(sumOfFrozenBalance);
    }
    return Optional.empty();
  }

  private Optional<BigDecimal> getFrozenBalanceForEnergy(AccountData accountData) {
    if (accountData.accountResource() != null && accountData.accountResource().frozenBalanceForEnergy() != null) {
      return Optional.of(accountData.accountResource().frozenBalanceForEnergy().frozenBalance());
    }
    return Optional.empty();
  }
}
