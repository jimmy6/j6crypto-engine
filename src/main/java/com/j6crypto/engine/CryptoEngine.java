package com.j6crypto.engine;

import com.j6crypto.engine.entity.ClientExchange;
import com.j6crypto.exception.TradeException;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.logic.entity.state.AutoTradeOrder.Action;
import com.j6crypto.repo.AutoTradeOrderRepo;
import com.j6crypto.repo.StateBaseRepo;
import com.j6crypto.repo.TradeRepo;
import com.j6crypto.service.AutoTradeOrderService;
import com.j6crypto.service.CandlestickManager;
import com.j6crypto.service.EngineDiscoveryService;
import com.j6crypto.to.TimeData;
import com.j6crypto.to.setup.AutoTradeOrderSetup;
import com.j6crypto.to.setup.AutoTradeOrderSetup.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.j6crypto.engine.EngineConstant.COIN_TOPIC_PREFIX;
import static com.j6crypto.engine.entity.ClientExchange.Exchange.BINANCE;
import static com.j6crypto.engine.entity.ClientExchange.Exchange.DUMMY;
import static com.j6crypto.exception.TradeException.RetryMode.NO_LIMIT;
import static com.j6crypto.to.setup.AutoTradeOrderSetup.Status.*;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.concurrent.ConcurrentHashMap.newKeySet;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Component
public class CryptoEngine {
  private static Logger logger = LoggerFactory.getLogger(CryptoEngine.class);
  public final static Set<Status> INACTIVE_ATO_STATUS = new HashSet(asList(FINISH, TERMINATED, ERR));
  public final static Set<Status> ACTIVE_ATO_STATUS = stream(values()).filter(
    status -> !INACTIVE_ATO_STATUS.contains(status)).collect(Collectors.toSet());

  private Map<String, Map<Integer, AutoTradeOrder>> atosBySymbol = new ConcurrentHashMap<>();
  @Autowired
  private TradeRepo tradeRepo;
  @Autowired
  private AutoTradeOrderRepo atoRepo;
  @Autowired
  private StateBaseRepo stateBaseRepo;
  @Autowired
  private AutoTradeOrderService atoService;
  @Autowired
  private EngineDiscoveryService engineDiscoveryService;
  @Autowired
  private TradingBot tradingBot;
  @PersistenceContext
  private EntityManager em;

  //TODO may need safe shutdown only after all bot finish
//  private ThreadPoolExecutor botPoolExe = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
//  private ThreadPoolExecutor tradePoolExe = (ThreadPoolExecutor) Executors.newFixedThreadPool(40);
  private ThreadPoolTaskExecutor tradePoolExe = new ThreadPoolTaskExecutor();
  private ThreadPoolTaskExecutor botPoolExe = new ThreadPoolTaskExecutor();

  private Map<ClientExchange.Exchange, Map<AutoTradeOrderSetup.ProductType, TradePlatform>> tradePlatforms = new HashMap();
  private CandlestickManager candlestickManager = new CandlestickManager();
  private int atoCount;

//  @PostConstruct
//  @Scheduled(fixedRate = 1000 * 60 * 10)//10 minute
//  public void syncSupportedSymbol() {
//    Set<String> supportedSymbols = atoService.getSupportSymbol();
//    atosBySymbol = new HashMap<>(Map.ofEntries(supportedSymbols.stream().map(
//      symbol -> Map.entry(symbol, new HashSet<>())
//    ).toArray(Map.Entry[]::new)));
//  }

  @PostConstruct
  public void initThread(){
    tradePoolExe.setCorePoolSize(40);
    tradePoolExe.setMaxPoolSize(40);
    tradePoolExe.setWaitForTasksToCompleteOnShutdown(true);
    tradePoolExe.setAwaitTerminationSeconds(60);

    botPoolExe.setCorePoolSize(20);
    botPoolExe.setMaxPoolSize(20);
    botPoolExe.setWaitForTasksToCompleteOnShutdown(true);
    botPoolExe.setAwaitTerminationSeconds(60);

  }

  public void init() {
    initTradingPlatform();
    List<AutoTradeOrder> atos = atoService.restoreAllAutoTradeOrders(tradePlatforms);
    atos.forEach(this::addToAtosBySymbol);
  }

  public CandlestickManager getCandlestickManager() {
    return candlestickManager;
  }

  public void initTradingPlatform() {
    tradePlatforms.put(BINANCE, Map.of(AutoTradeOrderSetup.ProductType.SPOT, new BinanceSpotTradingPlatform(em)));
    initDummyTradingPlatform();
  }

  private void initDummyTradingPlatform() {
    tradePlatforms.put(DUMMY, stream(AutoTradeOrderSetup.ProductType.values()).collect(Collectors.toMap(
      productType -> productType,
      productType -> new DummyTradingPlatform())
    ));
  }

  public void addAutoTradeOrder(Integer atoId) {
    AutoTradeOrder atoRestored = atoService.restoreAutoTradeOrder(atoId, tradePlatforms);
    addToAtosBySymbol(atoRestored);
  }

  private void addToAtosBySymbol(AutoTradeOrder atoRestored) {
    atosBySymbol.computeIfAbsent(atoRestored.getSymbol(),
      symbol -> new HashMap<>()).put(atoRestored.getId(), atoRestored);
  }

  //  @KafkaListeners({@KafkaListener(topics = {BNBBUSD})})
  public void monitor2(TimeData timeData) {
    logger.debug("222 " + timeData.toString());
  }

  public class MonitorBot implements Runnable {
    private TimeData timeData;
    private AutoTradeOrder ato;

    public MonitorBot() {
    }

    public MonitorBot(TimeData timeData, AutoTradeOrder ato) {
      this.timeData = timeData;
      this.ato = ato;
    }

    @Override
    public void run() {
      if (TRIGGER.equals(ato.getStatus())) {
        tradingBot.monitorTrigger(ato.getTriggerLogicOperator(), ato.getTriggerLogics(), ato, timeData);
        if (Action.OPEN_TRADE.equals(ato.getAction()))
          tradePoolExe.submit(new OpenTradeBot(timeData, ato));
      } else if (PM.equals(ato.getStatus()) && ato.getAction() == null) {
        tradingBot.monitorTrigger(ato.getTriggerLogicOperator(), ato.getPositionMgmtLogics(), ato, timeData);
        if (Action.OPEN_TRADE.equals(ato.getAction()))
          tradePoolExe.submit(new OpenTradeBot(timeData, ato));
      }
      Action action = ato.getAction();
      if ((STOP.equals(ato.getStatus()) || PM.equals(ato.getStatus())) && !ato.getPositionQty().equals(BigDecimal.ZERO)) {
        tradingBot.monitorStop(ato, timeData);
        if (Action.OPEN_TRADE.equals(ato.getAction()) && action == null)
          tradePoolExe.submit(new CloseTradeBot(timeData, ato));
      }
    }
  }

  public class OpenTradeBot implements Runnable {
    private TimeData timeData;
    private AutoTradeOrder ato;

    public OpenTradeBot(TimeData timeData, AutoTradeOrder ato) {
      this.timeData = timeData;
      this.ato = ato;
    }

    @Override
    public void run() {
      try {
        tradingBot.openTrade(ato, timeData);
      } catch (TradeException e) {
        if (NO_LIMIT.equals(e.getRetryMode())) {
          tradePoolExe.submit(this);
        } else {
          logger.error("OpenTradeBot RetryMode " + e.getRetryMode() + " not supported");
        }
      }
    }
  }

  public class CloseTradeBot implements Runnable {
    private TimeData timeData;
    private AutoTradeOrder ato;

    public CloseTradeBot(TimeData timeData, AutoTradeOrder ato) {
      this.timeData = timeData;
      this.ato = ato;
    }

    @Override
    public void run() {
      try {
        tradingBot.closeTrade(ato, timeData);
      } catch (TradeException e) {
        if (NO_LIMIT.equals(e.getRetryMode())) {
          tradePoolExe.submit(this);
        } else {
          logger.error("CloseTradeBot RetryMode " + e.getRetryMode() + " not supported");
        }
      }
    }
  }

  @Scheduled(cron = "55 * * * * *")
  public void atoCountUpdate() {
//    atosBySymbol.values().forEach(atos -> {
//      atos.removeAll(
//        atos.parallelStream().filter(ato -> INACTIVE_ATO_STATUS.contains(ato.getStatus()))
//          .collect(Collectors.toList()));
//    });
    atoCount = atosBySymbol.values().stream().mapToInt(atos -> atos.size()).sum();
  }

  @KafkaListeners({@KafkaListener(id = "monitor", topicPattern = COIN_TOPIC_PREFIX + "*")})
//TODO make it dynamic. what is  partition 0 1??
  public void monitor(TimeData timeData) {
    logger.debug(timeData.toString());
    candlestickManager.addData(timeData);
    Optional.ofNullable(atosBySymbol.get(timeData.getCode())).ifPresent(atos -> {
      atos.forEach((id, ato) -> {
        if (ato.getAction() == null)
          botPoolExe.submit(new MonitorBot(timeData, ato));
      });
      botPoolExe.submit(() -> {
        logger.info("Bot Finish. " + atos.size() + " " + timeData.getCode() + " bot");
        atos.values().stream().filter(ato -> INACTIVE_ATO_STATUS.contains(ato.getStatus())).map(ato -> ato.getId())
          .collect(Collectors.toList()).forEach(id -> atos.remove(id));
      });
    });
  }

  //  @KafkaListeners({@KafkaListener(id = "c1", topicPattern = COIN_TOPIC_PREFIX + "*")})
  public void monitorBk(TimeData timeData) {
    logger.debug(timeData.toString());
    candlestickManager.addData(timeData);

    Collection<AutoTradeOrder> atos = atosBySymbol.get(timeData.getCode()).values();

    if (!isEmpty(atos)) {
      logger.debug("atos {}", atos.toString());
      atos.parallelStream().filter(ato -> TRIGGER.equals(ato.getStatus())).forEach(ato -> {
        tradingBot.monitorTrigger(ato.getTriggerLogicOperator(), ato.getTriggerLogics(), ato, timeData);
      });
      atos.parallelStream().filter(ato -> PM.equals(ato.getStatus())).forEach(ato -> {
        tradingBot.monitorTrigger(ato.getTriggerLogicOperator(), ato.getPositionMgmtLogics(), ato, timeData);
      });
      atos.parallelStream().filter(
        ato -> ((STOP.equals(ato.getStatus()) || PM.equals(ato.getStatus())) && !ato.getPositionQty().equals(BigDecimal.ZERO))
      ).forEach(ato -> {
        tradingBot.monitorStop(ato, timeData);
      });
      atos.stream().filter(ato -> INACTIVE_ATO_STATUS.contains(ato.getStatus())).map(ato -> ato.getId())
        .collect(Collectors.toList()).forEach(id -> atos.remove(id));
    }
  }

  public void terminateAutoTradeOrder(Integer id) {
    atosBySymbol.values().stream().filter(atosMap -> atosMap.containsKey(id))
      .findAny().get().get(id).setStatus(TERMINATED);
  }

  public int getAtoCount() {
    return atoCount;
  }
}
