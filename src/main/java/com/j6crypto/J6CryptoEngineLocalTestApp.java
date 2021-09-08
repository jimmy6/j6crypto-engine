package com.j6crypto;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j6crypto.config.J6ZookeeperAutoServiceRegistrationAutoConfiguration;
import com.j6crypto.engine.CoinDataProducer;
import com.j6crypto.engine.CryptoEngine;
import com.j6crypto.engine.EngineConstant;
import com.j6crypto.engine.entity.Client;
import com.j6crypto.engine.entity.MasterDataKey;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.logic.entity.state.ProfitReduceFromHighestState;
import com.j6crypto.logic.entity.state.ReboundMartingaleState;
import com.j6crypto.service.AutoTradeOrderService;
import com.j6crypto.service.EngineDiscoveryService;
import com.j6crypto.web.ClientContext;
import com.j6crypto.to.setup.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.zookeeper.discovery.ConditionalOnZookeeperDiscoveryEnabled;
import org.springframework.context.annotation.*;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

import static com.j6crypto.engine.EngineUtil.toTimeData;
import static com.j6crypto.engine.entity.MasterDataKey.IdCoin.BNBBUSD;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@SpringBootApplication
@ActiveProfiles("dev")
@EnableCaching
@ComponentScan(basePackages = {"com.j6crypto"},
  excludeFilters = {@ComponentScan.Filter(SpringBootApplication.class),
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {CoinDataProducer.class})
  })
@Configuration
public class J6CryptoEngineLocalTestApp implements CommandLineRunner {
  private static Logger logger = LoggerFactory.getLogger(J6CryptoEngineLocalTestApp.class);
  private BinanceApiWebSocketClient client = BinanceApiClientFactory.newInstance().newWebSocketClient();

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
      cryptoEngine.initTradingPlatform();
//      run();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void run() {

    AutoTradeOrderSetup autoTradeOrderSetup = new AutoTradeOrderSetup();
    autoTradeOrderSetup.setSymbol(BNBBUSD.name());
    autoTradeOrderSetup.setClientExchangeId(1);

    BreakSupportResistanceTriggerSetup triggerSetup = new BreakSupportResistanceTriggerSetup(60 * 24 * 7);
    triggerSetup.setBreakWithGapBelow(BigDecimal.valueOf(1));
    triggerSetup.setCacheSignalForPeriod(14);
//    setup.getPriceRange(BigDecimal.valueOf(1));

    autoTradeOrderSetup.getTriggerStates().add(triggerSetup);

    ProfitPercentageTpSetup profitPercentageTpSetup = new ProfitPercentageTpSetup(new BigDecimal("0.5"));

    ProfitReduceFromHighestState profitReduceFromHighestState =
      new ProfitReduceFromHighestState(new ProfitReduceFromHighestSetup(new BigDecimal("0.2")));

    autoTradeOrderSetup.getStopStates().add(profitPercentageTpSetup);
    autoTradeOrderSetup.getStopStates().add(profitReduceFromHighestState);
    AutoTradeOrder atoFromJson = null;
    atoFromJson = atoSetupToAto(autoTradeOrderSetup, atoFromJson);

    autoTradeOrderService.create(atoFromJson);

//    pushDataToMonitor(autoTradeOrderSetup.getSymbol());
  }

  private AutoTradeOrder atoSetupToAto(AutoTradeOrderSetup autoTradeOrderSetup, AutoTradeOrder atoFromJson) {
    try {
      String json = new ObjectMapper().writeValueAsString(autoTradeOrderSetup);
      atoFromJson = new ObjectMapper().readerFor(AutoTradeOrder.class).readValue(json);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return atoFromJson;
  }

  public void runReboundMartingale() {

    String symbol = "bnbbusd".toUpperCase();//btcusdt
    AutoTradeOrderSetup autoTradeOrderSetup = new AutoTradeOrderSetup();
    autoTradeOrderSetup.setSymbol(symbol);
    autoTradeOrderSetup.setClientExchangeId(0);
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
    atoFromJson = atoSetupToAto(autoTradeOrderSetup, atoFromJson);

//    PriceReduceFromHighestSetup priceReduceFromHighestSetup = new PriceReduceFromHighestSetup(new BigDecimal("0.2")));
//    autoTradeOrderSetup.getStopSetups().add(priceReduceFromHighestSetup);

    autoTradeOrderService.create(atoFromJson);

    pushDataToMonitor(symbol);
  }

  private void pushDataToMonitor(String symbol) {
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

  @Primary
  @Bean
  public ClientContext getClientContext() {
    return new ClientContext(1);
  }

  @Primary
  @Bean
  public EngineDiscoveryService getEngineDiscoveryService() {
    return new EngineDiscoveryService() {
      @Override
      public int getMsId() {
        return 1;
      }
    };
  }
}