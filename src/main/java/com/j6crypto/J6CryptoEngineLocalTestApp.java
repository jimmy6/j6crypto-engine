package com.j6crypto;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.CandlestickEvent;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j6crypto.engine.CryptoEngine;
import com.j6crypto.engine.EngineConstant;
import com.j6crypto.engine.EngineUtil;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.logic.entity.state.ProfitReduceFromHighestState;
import com.j6crypto.logic.entity.state.ReboundMartingaleState;
import com.j6crypto.service.AutoTradeOrderService;
import com.j6crypto.service.SecurityService;
import com.j6crypto.to.TimeData;
import com.j6crypto.to.setup.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

import java.math.BigDecimal;

import static com.j6crypto.engine.EngineUtil.toTimeData;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@SpringBootApplication
@EnableCaching
@ComponentScan(basePackages = "com.j6crypto",
  excludeFilters = @ComponentScan.Filter(SpringBootApplication.class))
public class J6CryptoEngineLocalTestApp implements CommandLineRunner {
  private static Logger logger = LoggerFactory.getLogger(J6CryptoEngineLocalTestApp.class);
  @Autowired
  private SecurityService securityService;
  @Autowired
  private CryptoEngine cryptoEngine;
  @Autowired
  private AutoTradeOrderService autoTradeOrderService;

  public static void main(String[] args) {
    SpringApplication.run(J6CryptoEngineLocalTestApp.class, args);
  }

  @Override
  public void run(String... args) {
    try {
      run();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void runBreakSupportResistanceTriggerSetup() {
    BinanceApiWebSocketClient client = BinanceApiClientFactory.newInstance().newWebSocketClient();

    String symbol = "bnbbusd".toUpperCase();
    AutoTradeOrderSetup autoTradeOrderSetup = new AutoTradeOrderSetup();
    autoTradeOrderSetup.setSymbol(symbol);

    //    OpenMarketPriceSetup openMarketPriceState = new OpenMarketPriceSetup();
//    openMarketPriceState.setQty(new BigDecimal("0.05"));
//    OpenMarketPrice openMarketPrice = new OpenMarketPrice(autoTradeOrderSetup, openMarketPriceState);
    BreakSupportResistanceTriggerSetup setup = new BreakSupportResistanceTriggerSetup();
    setup.setBreakPeriod(60 * 24 * 7);
    setup.setBreakWithGapBelow(BigDecimal.valueOf(1));
//    setup.getPriceRange(BigDecimal.valueOf(1));

    ReboundMartingaleState reboundMartingaleState = new ReboundMartingaleState(
      new ReboundMartingaleSetup(5, false, new BigDecimal("0.5"), new BigDecimal("0.2")));

    autoTradeOrderSetup.getPmStates().add(reboundMartingaleState);

    ProfitPercentageTpSetup profitPercentageTpSetup = new ProfitPercentageTpSetup(new BigDecimal("0.5"));
    profitPercentageTpSetup.setLogicCode(EngineConstant.StopLogicCodes.ProfitPercentageTp.name());

    ProfitReduceFromHighestState profitReduceFromHighestState =
      new ProfitReduceFromHighestState(new ProfitReduceFromHighestSetup(new BigDecimal("0.2")));
    profitReduceFromHighestState.setLogicCode(EngineConstant.StopLogicCodes.ProfitReduceFromHighest.name());

    autoTradeOrderSetup.getStopStates().add(profitPercentageTpSetup);
    autoTradeOrderSetup.getStopStates().add(profitReduceFromHighestState);
    AutoTradeOrder atoFromJson = null;
    try {
      String json = new ObjectMapper().writeValueAsString(autoTradeOrderSetup);
      atoFromJson = new ObjectMapper().readerFor(AutoTradeOrder.class).readValue(json);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

//    PriceReduceFromHighestSetup priceReduceFromHighestSetup = new PriceReduceFromHighestSetup(new BigDecimal("0.2")));
//    autoTradeOrderSetup.getStopSetups().add(priceReduceFromHighestSetup);

    securityService.setClientId(17);
    autoTradeOrderService.create(atoFromJson);

    cryptoEngine.addAutoTradeOrder(atoFromJson.getId());
//    cryptoEngine.init(); //TODO
    client.onCandlestickEvent(symbol.toLowerCase(), CandlestickInterval.ONE_MINUTE,
      response -> {
        try {
          cryptoEngine.monitor(toTimeData(response));
        } catch (Exception e) {
          logger.error(e.getMessage(), e);
        }
      }
    );
  }

  public void run() {
    BinanceApiWebSocketClient client = BinanceApiClientFactory.newInstance().newWebSocketClient();

    String symbol = "bnbbusd".toUpperCase();//btcusdt
    AutoTradeOrderSetup autoTradeOrderSetup = new AutoTradeOrderSetup();
    autoTradeOrderSetup.setSymbol(symbol);
    autoTradeOrderSetup.setStatus(AutoTradeOrder.Status.PM);

    //    OpenMarketPriceSetup openMarketPriceState = new OpenMarketPriceSetup();
//    openMarketPriceState.setQty(new BigDecimal("0.05"));
//    OpenMarketPrice openMarketPrice = new OpenMarketPrice(autoTradeOrderSetup, openMarketPriceState);

    ReboundMartingaleState reboundMartingaleState = new ReboundMartingaleState(
      new ReboundMartingaleSetup(5, false, new BigDecimal("0.5"), new BigDecimal("0.2")));
    reboundMartingaleState.setLogicCode(EngineConstant.PmLogicCodes.ReboundMartingale.name());

    autoTradeOrderSetup.getPmStates().add(reboundMartingaleState);

    ProfitPercentageTpSetup profitPercentageTpSetup = new ProfitPercentageTpSetup(new BigDecimal("0.5"));
    profitPercentageTpSetup.setLogicCode(EngineConstant.StopLogicCodes.ProfitPercentageTp.name());

    ProfitReduceFromHighestState profitReduceFromHighestState =
      new ProfitReduceFromHighestState(new ProfitReduceFromHighestSetup(new BigDecimal("0.2")));
    profitReduceFromHighestState.setLogicCode(EngineConstant.StopLogicCodes.ProfitReduceFromHighest.name());

    autoTradeOrderSetup.getStopStates().add(profitPercentageTpSetup);
    autoTradeOrderSetup.getStopStates().add(profitReduceFromHighestState);
    AutoTradeOrder atoFromJson = null;
    try {
      String json = new ObjectMapper().writeValueAsString(autoTradeOrderSetup);
      atoFromJson = new ObjectMapper().readerFor(AutoTradeOrder.class).readValue(json);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

//    PriceReduceFromHighestSetup priceReduceFromHighestSetup = new PriceReduceFromHighestSetup(new BigDecimal("0.2")));
//    autoTradeOrderSetup.getStopSetups().add(priceReduceFromHighestSetup);

    securityService.setClientId(17);
    autoTradeOrderService.create(atoFromJson);

    cryptoEngine.addAutoTradeOrder(atoFromJson.getId());
//    cryptoEngine.init(); //TODO
    client.onCandlestickEvent(symbol.toLowerCase(), CandlestickInterval.ONE_MINUTE,
      response -> {
        try {
          cryptoEngine.monitor(toTimeData(response));
        } catch (Exception e) {
          logger.error(e.getMessage(), e);
        }
      }
    );
  }

}