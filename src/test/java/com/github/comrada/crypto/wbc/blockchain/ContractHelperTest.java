package com.github.comrada.crypto.wbc.blockchain;

import static com.github.comrada.crypto.wbc.TestUtils.mockWallet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.github.comrada.crypto.wbc.blockchain.exception.InvalidWalletException;
import java.math.BigDecimal;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContractHelperTest {

  private ContractHelper contractHelper;
  private Supplier<Integer> decimalsSupplier;
  private Supplier<String> symbolSupplier;

  @BeforeEach
  void initContractHelper() {
    contractHelper = new ContractHelper();
    decimalsSupplier = mock(Supplier.class);
    when(decimalsSupplier.get()).thenReturn(6);
    symbolSupplier = mock(Supplier.class);
    when(symbolSupplier.get()).thenReturn("USDT");
  }

  @Test
  void whenDecimalsForTheContractCalledFirstTime_thenDecimalsSupplierCalled() {
    int actual = contractHelper.getDecimals(mockWallet("Tron", "test-address", "USDT"), decimalsSupplier);
    verify(decimalsSupplier, times(1)).get();
    assertEquals(6, actual);
  }

  @Test
  void whenDecimalsForTheSameAssetSeveralTimesRequested_thenRealCallOnlyOnceExecuted() {
    contractHelper.getDecimals(mockWallet("Tron", "test-address", "USDT"), decimalsSupplier);
    contractHelper.getDecimals(mockWallet("Tron", "test-address", "USDT"), decimalsSupplier);
    contractHelper.getDecimals(mockWallet("Tron", "test-address", "BTT"), decimalsSupplier);
    contractHelper.getDecimals(mockWallet("Tron", "test-address", "BTT"), decimalsSupplier);
    verify(decimalsSupplier, times(2)).get();
  }

  @Test
  void whenNewContractHasNullBalance_thenSymbolCallExecuted() {
    String actual = contractHelper.getSymbol(mockWallet("Tron", "test-address", "USDT", null), symbolSupplier);
    verify(symbolSupplier, times(1)).get();
    assertEquals("USDT", actual);
  }

  @Test
  void whenContractHasAlreadyPreviousBalance_thenSymbolCallNotExecuted() {
    String actual = contractHelper.getSymbol(mockWallet("Tron", "test-address", "USDT", BigDecimal.ONE),
        symbolSupplier);
    verifyNoInteractions(symbolSupplier);
    assertEquals("USDT", actual);
  }

  @Test
  void whenNewContractHasAnotherAssetThanSymbol_thenExceptionThrown() {
    when(symbolSupplier.get()).thenReturn("BTT");
    assertThrows(InvalidWalletException.class,
        () -> contractHelper.getSymbol(mockWallet("Tron", "test-address", "USDT", null), symbolSupplier));
    verify(symbolSupplier, times(1)).get();
  }
}