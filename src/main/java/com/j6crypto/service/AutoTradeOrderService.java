package com.j6crypto.service;

import com.j6crypto.engine.*;
import com.j6crypto.engine.entity.ClientExchange;
import com.j6crypto.engine.entity.ClientExchange.Exchange;
import com.j6crypto.engine.entity.MasterData;
import com.j6crypto.engine.entity.MasterDataKey;
import com.j6crypto.logic.PmTradeLogic;
import com.j6crypto.logic.StopTradeLogic;
import com.j6crypto.logic.TradeLogic;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.logic.entity.state.State;
import com.j6crypto.repo.AutoTradeOrderRepo;
import com.j6crypto.repo.MasterDataRepo;
import com.j6crypto.to.setup.AutoTradeOrderSetup;
import com.j6crypto.to.setup.AutoTradeOrderSetup.ProductType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.j6crypto.engine.CryptoEngine.INACTIVE_ATO_STATUS;
import static com.j6crypto.engine.entity.ClientExchange.Exchange.BINANCE;
import static com.j6crypto.engine.entity.ClientExchange.Exchange.DUMMY;
import static com.j6crypto.to.setup.AutoTradeOrderSetup.Status.*;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Service
public class AutoTradeOrderService {
  private static Logger logger = LoggerFactory.getLogger(AutoTradeOrderService.class);
  @Autowired
  private AutoTradeOrderRepo autoTradeOrderRepo;
  @PersistenceContext
  private EntityManager em;
  @Autowired
  private SecurityService securityService;
  @Autowired
  private EngineClient engineClient;
  @Autowired
  private Supplier<LocalDateTime> currentDateTimeSupplier;
  @Autowired
  private MasterDataRepo masterDataRepo;
  @Autowired
  private CryptoEngine cryptoEngine;
  @Autowired
  private EngineDiscoveryService engineDiscoveryService;

  public Set<String> getSupportSymbol() {
    List<MasterData> masterDatas = masterDataRepo.findByMasterDataKeyCategoryAndActiveOrderBySequence(MasterDataKey.Category.COIN, true);
    return masterDatas.stream().map(m -> m.getMasterDataKey().getId()).collect(Collectors.toSet());
  }

  public AutoTradeOrder create(AutoTradeOrder ato) {
    //TODO validate clientExchangeId
    ato.setClientId(securityService.getClientId());
    assignMsId(ato);
    //TODO state order is important. maybe use regular expression using logicCode l1 + b2 + c3
    setAutoTradeOrder(ato, ato.getTriggerStates());
    setAutoTradeOrder(ato, ato.getPmStates());
    setAutoTradeOrder(ato, ato.getStopStates());
    AutoTradeOrder ret = autoTradeOrderRepo.saveAndFlush(ato);
    engineClient.addAtoToCryptoEngine(ret.getMsId(), ret.getId());
    return ret;
  }

  private void assignMsId(AutoTradeOrder ato) {
    //TODO 2. Scheduler
    //TODO 2.1, reassign msid for shuting down unessacery ms
    ato.setMsId(engineDiscoveryService.getMsId());
  }

  private void setAutoTradeOrder(AutoTradeOrder ato, List<State> setups) {
    setups.forEach(state -> state.getCommonState().setAutoTradeOrder(ato));
  }

  @Transactional(readOnly = true)
  public List<AutoTradeOrder> restoreAllAutoTradeOrders(Map<Exchange, Map<ProductType, TradePlatform>> tradePlatforms) {
    List<AutoTradeOrder> atos = autoTradeOrderRepo.findByMsIdAndStatusIn(engineDiscoveryService.getMsId(), asList(TRIGGER, PM, STOP));
    atos.forEach(ato -> restoreAutoTradeOrder(ato.getId(), tradePlatforms));
    return atos;
  }

  @Transactional(readOnly = true)
  public AutoTradeOrder restoreAutoTradeOrder(Integer atoId, Map<Exchange, Map<ProductType, TradePlatform>> tradePlatforms) {
    AutoTradeOrder ato = autoTradeOrderRepo.getById(atoId);
    ato.setTradePlatform(tradePlatforms
      .get(em.find(ClientExchange.class, ato.getClientExchangeId()).getExchange())
      .get(ato.getProductType()));
    ato.getTriggerStates().forEach(state -> {
      String logicCode = (state.getClass().getAnnotation(Entity.class)).name();
      try {
        Class aClass = getClass().forName("com.j6crypto.logic.trigger." + logicCode);
        TradeLogic tradeLogic = (TradeLogic)
          aClass.getDeclaredConstructor(AutoTradeOrder.class, state.getClass(), Supplier.class, CandlestickManager.class)
            .newInstance(ato, state, currentDateTimeSupplier, cryptoEngine.getCandlestickManager());
        ato.getTriggerLogics().add(tradeLogic);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
    ato.getPmStates().forEach(state -> {
      String logicCode = (state.getClass().getAnnotation(Entity.class)).name();
      try {
        Class aClass = getClass().forName("com.j6crypto.logic.positionmgmt." + logicCode);
        PmTradeLogic tradeLogic = (PmTradeLogic)
          aClass.getDeclaredConstructor(AutoTradeOrder.class, state.getClass(), Supplier.class)
            .newInstance(ato, state, currentDateTimeSupplier);
        ato.getPositionMgmtLogics().add(tradeLogic);
        tradeLogic.setTradePlatform(ato.getTradePlatform());
        tradeLogic.setEm(em);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
    ato.getStopStates().forEach(state -> {
      String logicCode = (state.getClass().getAnnotation(Entity.class)).name();
      try {
        Class aClass = getClass().forName("com.j6crypto.logic.stop." + logicCode);
        ato.getStopLogics().add((StopTradeLogic)
          aClass.getDeclaredConstructor(AutoTradeOrder.class, state.getClass(), Supplier.class).newInstance(ato, state, currentDateTimeSupplier));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
    return ato;
  }

  public AutoTradeOrder getAto(Integer id) {
    AutoTradeOrder autoTradeOrder = autoTradeOrderRepo.findById(id).get();
    autoTradeOrder.getTriggerStates();
    autoTradeOrder.getPositionMgmtLogics().size();
    autoTradeOrder.getStopLogics();
    return autoTradeOrder;
  }

  @Transactional
  public void terminateAutoTradeOrder(Integer atoId) {
    int updated = autoTradeOrderRepo.updateStatus(atoId, securityService.getClientId(), TERMINATED, INACTIVE_ATO_STATUS);
    if (updated <= 0)
      throw new ResponseStatusException(BAD_GATEWAY, "Fail to update AutoTradeOrder to terminated.");
    try {
      cryptoEngine.terminateAutoTradeOrder(atoId);
    } catch (Exception e) {
      logger.error("terminateAutoTradeOrder error", e);
      throw new ResponseStatusException(NOT_FOUND, "Fail to terminate AutoTradeOrder.");
    }

  }
}
