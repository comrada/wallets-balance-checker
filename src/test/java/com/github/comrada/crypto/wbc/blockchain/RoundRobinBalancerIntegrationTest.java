package com.github.comrada.crypto.wbc.blockchain;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.comrada.crypto.wbc.blockchain.exception.NetworkException;
import com.github.comrada.crypto.wbc.blockchain.exception.NoLiveServicesException;
import com.github.comrada.crypto.wbc.domain.Wallet;
import com.github.comrada.crypto.wbc.domain.WalletStatus;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoundRobinBalancerIntegrationTest {

  private RoundRobinBalancer balancer;
  private BlockchainApi service1;
  private BlockchainApi service2;
  private Wallet wallet;

  @BeforeEach
  void initBalancer() {
    wallet = new Wallet("test", "addr1", "TKN", BigDecimal.ZERO, false, WalletStatus.OK);
    service1 = mock(BlockchainApi.class);
    when(service1.balance(wallet)).thenThrow(new NetworkException(""));
    service2 = mock(BlockchainApi.class);
    when(service2.balance(wallet)).thenThrow(new NetworkException(""));
    balancer = new RoundRobinBalancer(List.of(service1, service2), Duration.ofSeconds(3));
  }

  @AfterEach
  void stopBalancer() {
    balancer.close();
  }

  @Test
  void whenServicesThrowExceptions_thenTheyAreCalledOnlyThreeTimes() {
    assertThrows(NoLiveServicesException.class, () -> balancer.getBalance(wallet));
    verify(service1, times(3)).balance(wallet);
    verify(service2, times(3)).balance(wallet);
  }

  @Test
  void whenFrozenServicesReturnAfterTimeout_thenTheyContinueToCall() throws InterruptedException {
    assertThrows(NoLiveServicesException.class, () -> balancer.getBalance(wallet));
    await()
        .atMost(5, TimeUnit.SECONDS)
        .untilAsserted(() -> {
          verify(service1, times(3)).balance(wallet);
          verify(service2, times(3)).balance(wallet);
        });

    Thread.sleep(4000);
    assertThrows(NoLiveServicesException.class, () -> balancer.getBalance(wallet));
    await()
        .atMost(5, TimeUnit.SECONDS)
        .untilAsserted(() -> {
          verify(service1, times(6)).balance(wallet);
          verify(service2, times(6)).balance(wallet);
        });
  }
}