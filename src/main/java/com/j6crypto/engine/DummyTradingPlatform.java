package com.j6crypto.engine;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.j6crypto.engine.entity.ClientExchange;
import com.j6crypto.to.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.binance.api.client.domain.account.NewOrder.marketBuy;
import static com.binance.api.client.domain.account.NewOrder.marketSell;

/**
 * <pre>
 *
 * 1) cmd admin sync local time
 net stop w32time
 w32tm /unregister
 w32tm /register
 net start w32time
 w32tm /resync
 *
 *
 * https://testnet.binance.org/faucet-smart
 * 0xbCf2db71c857c3a68a8FF0b79194693dC7c401d0
 *
 * </pre>
 *
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class DummyTradingPlatform extends TradePlatform {
  private static Logger logger = LoggerFactory.getLogger(DummyTradingPlatform.class);

  @Override
  public void openMarket(Integer clientId, String code, Trade trade) {

  }
}