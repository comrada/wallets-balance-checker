package com.github.comrada.crypto.wbc.app.config;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static java.util.stream.Collectors.toUnmodifiableSet;

import com.github.comrada.crypto.wbc.checker.NetworkConfig;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.network")
public class NetworkParameters {

  private Set<Blockchain> blockchains = new HashSet<>();
  private Map<String, Blockchain> configMap;
  private Set<String> enabledNetworks = new HashSet<>();

  public NetworkParameters() {
  }

  public NetworkParameters(Set<Blockchain> blockchains, Set<String> enabledNetworks) {
    this.blockchains = requireNonNull(blockchains);
    this.enabledNetworks = requireNonNull(enabledNetworks);
  }

  public Set<Blockchain> getBlockchains() {
    return unmodifiableSet(blockchains);
  }

  public void setBlockchains(Set<Blockchain> blockchains) {
    this.blockchains = blockchains;
    this.configMap = blockchains.stream().collect(toUnmodifiableMap(Blockchain::getName, identity()));
  }

  public Blockchain getConfigFor(String network) {
    return configMap.get(network);
  }

  public Set<String> getEnabledNetworks() {
    return enabledNetworks;
  }

  public void setEnabledNetworks(String enabledNetworks) {
    this.enabledNetworks = Arrays.stream(enabledNetworks.split(","))
        .map(String::trim)
        .collect(toUnmodifiableSet());
  }

  public static final class Blockchain implements NetworkConfig {

    private String name;
    private Map<String, String> parameters = new HashMap<>();

    public Blockchain() {
    }

    public Blockchain(String name, Map<String, String> parameters) {
      this.name = requireNonNull(name);
      this.parameters = requireNonNull(parameters);
    }

    @Override
    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = requireNonNull(name);
    }

    @Override
    public Map<String, String> getParameters() {
      return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
      this.parameters = requireNonNull(parameters);
    }
  }
}
