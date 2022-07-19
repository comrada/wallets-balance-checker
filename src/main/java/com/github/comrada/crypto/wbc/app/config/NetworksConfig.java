package com.github.comrada.crypto.wbc.app.config;


import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.comrada.crypto.wbc.app.mapper.JacksonResponseMapper;
import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.blockchain.RoundRobinBalancer;
import com.github.comrada.crypto.wbc.blockchain.networks.binance.BinanceChain;
import com.github.comrada.crypto.wbc.blockchain.networks.bitcoin.BitcoinRestApi;
import com.github.comrada.crypto.wbc.blockchain.networks.bitcoin.blockchain.info.BlockchainInfo;
import com.github.comrada.crypto.wbc.blockchain.networks.bitcoin.blockstream.info.BlockstreamInfo;
import com.github.comrada.crypto.wbc.blockchain.networks.ethereum.EthereumApi;
import com.github.comrada.crypto.wbc.blockchain.networks.ripple.RippleNet;
import com.github.comrada.crypto.wbc.blockchain.networks.stellar.StellarApi;
import com.github.comrada.crypto.wbc.blockchain.networks.tron.TronGrid;
import com.github.comrada.crypto.wbc.blockchain.rest.ResponseMapper;
import com.github.comrada.crypto.wbc.checker.NetworkConfig;
import com.github.comrada.crypto.wbc.checker.NetworksManager;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
@EnableConfigurationProperties(NetworkParameters.class)
public class NetworksConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(NetworksConfig.class);

  @Bean
  ResponseMapper responseMapper() {
    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    return new JacksonResponseMapper(objectMapper);
  }

  @Bean
  NetworksManager networksManager(List<BlockchainApi> apis, NetworkParameters parameters) {
    Set<String> enabledNetworks = parameters.getEnabledNetworks();
    Map<String, BlockchainApi> mappedApis = apis.stream()
        .filter(api -> enabledNetworks.contains(api.name()))
        .collect(toMap(BlockchainApi::name, identity()));
    LOGGER.info("Balance checkers for assets {} have been registered", mappedApis.keySet());
    return new NetworksManager(mappedApis);
  }

  @Bean
  @ConditionalOnExpression("'${app.network.enabled-networks}'.contains('Ethereum')")
  BlockchainApi ethereumBalance(NetworkParameters parameters) {
    NetworkConfig ethereumConfig = parameters.getConfigFor(EthereumApi.BLOCKCHAIN_NAME);
    return new EthereumApi(ethereumConfig);
  }

  @Bean
  @ConditionalOnExpression("'${app.network.enabled-networks}'.contains('Ripple')")
  BlockchainApi rippleBalance(NetworkParameters parameters) {
    NetworkConfig rippleNetConfig = parameters.getConfigFor(RippleNet.BLOCKCHAIN_NAME);
    return new RippleNet(rippleNetConfig);
  }

  @Bean
  @ConditionalOnExpression("'${app.network.enabled-networks}'.contains('Bitcoin')")
  RoundRobinBalancer bitcoinRoundRobinBalancer(ResponseMapper responseMapper) {
    HttpClient client = HttpClient.newBuilder()
        .followRedirects(Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(20))
        .build();
    BlockstreamInfo blockstreamInfo = new BlockstreamInfo(client, responseMapper);
    BlockchainInfo blockchainInfo = new BlockchainInfo(client, responseMapper);
    return new RoundRobinBalancer(List.of(blockchainInfo, blockstreamInfo), Duration.ofMinutes(10));
  }

  @Bean
  @ConditionalOnExpression("'${app.network.enabled-networks}'.contains('Bitcoin')")
  BlockchainApi bitcoinWebServicesBalancer(NetworkParameters parameters, RoundRobinBalancer roundRobinBalancer) {
    NetworkConfig stellarConfig = parameters.getConfigFor(BitcoinRestApi.BLOCKCHAIN_NAME);
    return new BitcoinRestApi(stellarConfig, roundRobinBalancer);
  }

  @Bean
  @ConditionalOnExpression("'${app.network.enabled-networks}'.contains('Stellar')")
  BlockchainApi stellarBalance(NetworkParameters parameters) {
    NetworkConfig stellarConfig = parameters.getConfigFor(StellarApi.BLOCKCHAIN_NAME);
    return new StellarApi(stellarConfig);
  }

  @Bean
  @ConditionalOnExpression("'${app.network.enabled-networks}'.contains('Binance Chain')")
  BlockchainApi binanceChainBalance(NetworkParameters parameters, ResponseMapper responseMapper) {
    NetworkConfig binanceChainConfig = parameters.getConfigFor(BinanceChain.BLOCKCHAIN_NAME);
    HttpClient client = HttpClient.newBuilder()
        .followRedirects(Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(20))
        .build();
    return new BinanceChain(client, responseMapper, binanceChainConfig);
  }

  @Bean
  @ConditionalOnExpression("'${app.network.enabled-networks}'.contains('Tron')")
  BlockchainApi tronBalance(NetworkParameters parameters, ResponseMapper responseMapper) {
    NetworkConfig tronConfig = parameters.getConfigFor(TronGrid.BLOCKCHAIN_NAME);
    HttpClient client = HttpClient.newBuilder()
        .followRedirects(Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(20))
        .build();
    return new TronGrid(client, responseMapper, tronConfig);
  }
}
