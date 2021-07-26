package com.j6crypto.engine;

import com.j6crypto.engine.entity.ClientExchange;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.repo.AutoTradeOrderRepo;
import com.j6crypto.repo.StateBaseRepo;
import com.j6crypto.repo.TradeRepo;
import com.j6crypto.service.AutoTradeOrderService;
import com.j6crypto.service.CandlestickManager;
import com.j6crypto.service.EngineDiscoveryService;
import com.j6crypto.to.TimeData;
import com.j6crypto.to.setup.AutoTradeOrderSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

import static com.j6crypto.engine.EngineConstant.COIN_TOPIC_PREFIX;
import static com.j6crypto.engine.entity.ClientExchange.Exchange.BINANCE;
import static com.j6crypto.engine.entity.ClientExchange.Exchange.DUMMY;
import static com.j6crypto.to.setup.AutoTradeOrderSetup.Status.*;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Component
public class CryptoEngine {
  private static Logger logger = LoggerFactory.getLogger(CryptoEngine.class);
  public final static Set<AutoTradeOrderSetup.Status> INACTIVE_ATO_STATUS = new HashSet(asList(FINISH, TERMINATED, ERR));
  public final static Set<AutoTradeOrderSetup.Status> ACTIVE_ATO_STATUS = stream(values()).filter(
    status -> !INACTIVE_ATO_STATUS.contains(status)).collect(Collectors.toSet());

  private Map<String, Set<AutoTradeOrder>> atosBySymbol = new HashMap<>();
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
  private CryptoBot cryptoBot;
  @PersistenceContext
  private EntityManager em;

  private Map<ClientExchange.Exchange, Map<AutoTradeOrderSetup.ProductType, TradePlatform>> tradePlatforms = new HashMap();
  private CandlestickManager candlestickManager = new CandlestickManager();

//  @PostConstruct
//  @Scheduled(fixedRate = 1000 * 60 * 10)//10 minute
//  public void syncSupportedSymbol() {
//    Set<String> supportedSymbols = atoService.getSupportSymbol();
//    atosBySymbol = new HashMap<>(Map.ofEntries(supportedSymbols.stream().map(
//      symbol -> Map.entry(symbol, new HashSet<>())
//    ).toArray(Map.Entry[]::new)));
//  }

  public void init() {
    initTradingPlatform();
    List<AutoTradeOrder> atos = atoService.restoreAllAutoTradeOrders(tradePlatforms);
    atos.forEach(this::addToAtosBySymbol);
  }

  public CandlestickManager getCandlestickManager() {
    return candlestickManager;
  }

  private void initTradingPlatform() {
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

  private synchronized void addToAtosBySymbol(AutoTradeOrder atoRestored) {
    atosBySymbol.computeIfAbsent(atoRestored.getSymbol(),
      symbol -> new HashSet<>()).add(atoRestored);
  }

  //    @KafkaListeners({@KafkaListener(topics = {BNBBUSD})})
  public void monitor2(TimeData timeData) {
    logger.debug("222 " + timeData.toString());
  }

  @KafkaListeners({@KafkaListener(id = "c1", topicPattern = COIN_TOPIC_PREFIX + "*")})
//TODO make it dynamic. More than 1 symbol. what is  partition 0 1??
  public void monitor(TimeData timeData) {//Syncronise??
    logger.debug(timeData.toString());
    candlestickManager.addData(timeData);

    //TODO parallelStream tread testing/configurable
    //TODO after monitor return a signal. it can be removed/skip in following step like positionmgmt or stop
    Set<AutoTradeOrder> atos = atosBySymbol.get(timeData.getCode());

    if (!isEmpty(atos)) {
      logger.debug("atos {}", atos.toString());
      atos.parallelStream().forEach(ato -> {
        cryptoBot.monitorTrigger(ato, timeData);
      });
      atos.parallelStream().forEach(ato -> {
        cryptoBot.monitorPm(ato, timeData);
      });
      atos.parallelStream().forEach(ato -> {
        if (ato.getFirstTrade() != null)
          cryptoBot.monitorStop(ato, timeData);
      });
      atos.removeAll(
        atos.parallelStream().filter(
          ato -> INACTIVE_ATO_STATUS.contains(ato.getStatus())).collect(Collectors.toList()));
    }
  }

  public void terminateAutoTradeOrder(Integer id) {
    AutoTradeOrder ato = new AutoTradeOrder();
    ato.setId(id);
    atosBySymbol.values().stream().filter(atos -> atos.contains(ato)).findAny().get()
      .stream().filter(atoLoop -> atoLoop.getId().equals(id)).findAny().get().setStatus(TERMINATED);
  }
}
