package com.github.comrada.crypto.wbc.blockchain;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.comrada.crypto.wbc.blockchain.exception.NetworkException;
import com.github.comrada.crypto.wbc.blockchain.exception.NoLiveServicesException;
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

  @BeforeEach
  void initBalancer() {
    service1 = mock(BlockchainApi.class);
    when(service1.balance("addr1")).thenThrow(new NetworkException(""));
    service2 = mock(BlockchainApi.class);
    when(service2.balance("addr1")).thenThrow(new NetworkException(""));
    balancer = new RoundRobinBalancer(List.of(service1, service2), Duration.ofSeconds(3));
  }

  @AfterEach
  void stopBalancer() {
    balancer.close();
  }

  @Test
  void whenServicesThrowExceptions_thenTheyAreCalledOnlyThreeTimes() {
    assertThrows(NoLiveServicesException.class, () -> balancer.getBalance("addr1"));
    verify(service1, times(3)).balance("addr1");
    verify(service2, times(3)).balance("addr1");
  }

  @Test
  void whenFrozenServicesReturnAfterTimeout_thenTheyContinueToCall() throws InterruptedException {
    assertThrows(NoLiveServicesException.class, () -> balancer.getBalance("addr1"));
    await()
        .atMost(5, TimeUnit.SECONDS)
        .untilAsserted(() -> {
          verify(service1, times(3)).balance("addr1");
          verify(service2, times(3)).balance("addr1");
        });

    Thread.sleep(4000);
    assertThrows(NoLiveServicesException.class, () -> balancer.getBalance("addr1"));
    await()
        .atMost(5, TimeUnit.SECONDS)
        .untilAsserted(() -> {
          verify(service1, times(6)).balance("addr1");
          verify(service2, times(6)).balance("addr1");
        });
  }
}