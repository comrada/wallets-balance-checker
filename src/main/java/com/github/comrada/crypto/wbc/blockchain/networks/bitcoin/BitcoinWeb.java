package com.github.comrada.crypto.wbc.blockchain.networks.bitcoin;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.google.common.collect.Iterables;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitcoinWeb implements BlockchainApi {

  private static final Logger LOGGER = LoggerFactory.getLogger(BitcoinWeb.class);
  private final Iterator<BlockchainApi> webServices;

  public BitcoinWeb(List<BlockchainApi> webServices) {
    this.webServices = Iterables.cycle(webServices).iterator();
  }

  @Override
  public String name() {
    return "Bitcoin";
  }

  @Override
  public BigDecimal balance(String address) {
    BlockchainApi webService = webServices.next();
    LOGGER.info("Requesting balance with {}", webService.getClass().getSimpleName());
    return webService.balance(address);
  }
}
