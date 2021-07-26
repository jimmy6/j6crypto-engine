package com.j6crypto.engine;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.j6crypto.engine.entity.ClientExchange;
import com.j6crypto.to.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
 * net stop w32time
 * w32tm /unregister
 * w32tm /register
 * net start w32time
 * w32tm /resync
 *
 *
 * https://testnet.binance.org/faucet-smart
 * 0xbCf2db71c857c3a68a8FF0b79194693dC7c401d0
 *
 * </pre>
 *
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class BinanceSpotTradingPlatform extends TradePlatform {
  private static Logger logger = LoggerFactory.getLogger(BinanceSpotTradingPlatform.class);
  private EntityManager em;

  public BinanceSpotTradingPlatform(EntityManager em) {
    this.em = em;
  }

  //  private Map<Integer, BinanceApiRestClient> clientBinanceApiClients = new HashMap<>();

  @Override
  public void openMarket(Integer clientExchangeId, String code, Trade trade) {
//    Cache binanceApiRestClient implementation
//    BinanceApiRestClient binanceApiRestClient = clientBinanceApiClients.computeIfAbsent(clientId,
//      cId -> {
//        ClientExchange clientExchange = em.find(ClientExchange.class, cId);
//        return BinanceApiClientFactory.newInstance(clientExchange.getApiKey(), clientExchange.getSecretKey())
//          .newRestClient();
//      });// when STOP, need to clear clientBinanceApiClients

    ClientExchange clientExchange = em.find(ClientExchange.class, clientExchangeId);
    BinanceApiRestClient binanceApiRestClient = BinanceApiClientFactory
      .newInstance(clientExchange.getApiKey(), clientExchange.getSecretKey())
      .newRestClient();
    NewOrderResponse newOrderResponse;
    if (trade.getLongShort().equals(Trade.LongShort.LONG)) {
      newOrderResponse = binanceApiRestClient.newOrder(marketBuy(code, trade.getQty().toPlainString()));
    } else {
      newOrderResponse = binanceApiRestClient.newOrder(marketSell(code, trade.getQty().toPlainString()));
    }
    logger.info(newOrderResponse.toString());
    if (newOrderResponse.getStatus().equals(OrderStatus.FILLED)) {
      trade.setTransactedPrice(new BigDecimal(newOrderResponse.getPrice()));
      if (BigDecimal.ZERO.equals(trade.getTransactedPrice())) {
        trade.setTransactedPrice(null);
      }
      trade.setTransactedQty(new BigDecimal(newOrderResponse.getExecutedQty()));
    } else {
      logger.warn(newOrderResponse.toString());
      throw new RuntimeException("Order Fail. " + newOrderResponse.toString());
    }

  }
}