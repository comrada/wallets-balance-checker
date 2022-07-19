package com.github.comrada.crypto.wbc.checker;

import static java.util.stream.Collectors.toUnmodifiableSet;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public interface NetworkConfig {

  String getName();

  Map<String, String> getParameters();

  default int getIntegerParam(String paramName) {
    String value = getParameters().get(paramName);
    assertParamValue(paramName, value);
    return Integer.parseInt(value);
  }

  default int getIntegerParam(String paramName, int defaultValue) {
    String value = getParameters().get(paramName);
    return value != null ? Integer.parseInt(value) : defaultValue;
  }

  default String getStringParam(String paramName) {
    String value = getParameters().get(paramName);
    assertParamValue(paramName, value);
    return value;
  }

  default String getStringParam(String paramName, String defaultValue) {
    String value = getParameters().get(paramName);
    return value != null ? value : defaultValue;
  }

  default Set<String> getArray(String paramName) {
    String value = getParameters().get(paramName);
    assertParamValue(paramName, value);
    return Arrays.stream(value.split(",")).collect(toUnmodifiableSet());
  }

  default Set<String> getArray(String paramName, Set<String> defaultValue) {
    String value = getParameters().get(paramName);
    return value != null ? Arrays.stream(value.split(",")).collect(toUnmodifiableSet()) : defaultValue;
  }

  private void assertParamValue(String paramName, String value) {
    if (value == null) {
      throw new IllegalArgumentException(
          "Parameter '%s' is not presented in the network '%s' config".formatted(paramName, getName()));
    }
  }
}
