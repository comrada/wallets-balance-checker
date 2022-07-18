package com.github.comrada.crypto.wbc.blockchain.networks.tron;

import static com.github.comrada.crypto.wbc.TestUtils.readFile;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.comrada.crypto.wbc.app.mapper.JacksonResponseMapper;
import java.io.IOException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class BalanceExtractorTest {

  @Test
  void balanceOnly() throws IOException {
    BigDecimal balance = getBalanceFromFile("account-response-without-frozen.json");
    assertEquals(new BigDecimal("2052504986906760"), balance);
  }

  @Test
  void balanceWithFrozenBalance() throws IOException {
    BigDecimal balance = getBalanceFromFile("account-response-with-frozen.json");
    assertEquals(new BigDecimal("109635731990"), balance);
  }

  @Test
  void balanceWithFrozenBalanceAndFrozenEnergy() throws IOException {
    BigDecimal balance = getBalanceFromFile("account-response-with-frozen-balance-and-gas.json");
    assertEquals(new BigDecimal("160488731990"), balance);
  }

  @Test
  void whenThereIsNoTrxAsset_thenReturnsZero() throws IOException {
    BigDecimal balance = getBalanceFromFile("account-response-without-balance.json");
    assertEquals(BigDecimal.ZERO, balance);
  }

  private BigDecimal getBalanceFromFile(String filaName) throws IOException {
    JacksonResponseMapper responseMapper = new JacksonResponseMapper(new ObjectMapper().findAndRegisterModules());
    String accountResponse = readFile(TronGridTest.class, filaName);
    Account account = responseMapper.map(accountResponse, Account.class);
    BalanceExtractor balanceExtractor = new BalanceExtractor();
    return balanceExtractor.extract(account);
  }
}