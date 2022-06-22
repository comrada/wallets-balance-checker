package com.github.comrada.crypto.wbc.app.config;


import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.blockchain.networks.ethereum.EthereumApi;
import com.github.comrada.crypto.wbc.checker.NetworkConfig;
import com.github.comrada.crypto.wbc.checker.NetworksManager;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(NetworkParameters.class)
public class NetworksConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(NetworksConfig.class);

  @Bean
  NetworksManager networksManager(List<BlockchainApi> apis) {
    Map<String, BlockchainApi> mappedApis = apis.stream()
        .collect(toMap(BlockchainApi::asset, identity()));
    LOGGER.info("Balance checkers for assets {} have been registered", mappedApis.keySet());
    return new NetworksManager(mappedApis);
  }

  @Bean
  BlockchainApi ethereumBalance(NetworkParameters parameters) {
    NetworkConfig ethereumConfig = parameters.getConfigMap().get("Ethereum");
    return new EthereumApi(ethereumConfig);
  }
}
