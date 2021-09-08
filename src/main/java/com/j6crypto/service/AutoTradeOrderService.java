package com.j6crypto.service;

import com.j6crypto.engine.CryptoEngine;
import com.j6crypto.engine.EngineClient;
import com.j6crypto.engine.TradePlatform;
import com.j6crypto.engine.entity.ClientExchange;
import com.j6crypto.engine.entity.ClientExchange.Exchange;
import com.j6crypto.engine.entity.MasterData;
import com.j6crypto.logic.StopTradeLogic;
import com.j6crypto.logic.TradeLogic;
import com.j6crypto.logic.TriggerTradeLogic;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.logic.positionmgmt.Pm;
import com.j6crypto.repo.AutoTradeOrderRepo;
import com.j6crypto.repo.MasterDataRepo;
import com.j6crypto.to.setup.AutoTradeOrderSetup.ProductType;
import com.j6crypto.to.setup.SetupBase;
import com.j6crypto.web.ClientContext;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.j6crypto.engine.CryptoEngine.INACTIVE_ATO_STATUS;
import static com.j6crypto.engine.entity.MasterDataKey.Category.COIN;
import static com.j6crypto.to.setup.AutoTradeOrderSetup.Status.*;
import static java.util.Arrays.asList;
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
  private ClientContext clientContext;
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
    List<MasterData> masterDatas = masterDataRepo.findByMasterDataKeyCategoryAndActiveOrderBySequence(COIN, true);
    return masterDatas.stream().map(m -> m.getMasterDataKey().getId()).collect(Collectors.toSet());
  }

  //TODO validate number of active ato
  @Transactional
  public AutoTradeOrder create(AutoTradeOrder ato) {
    assignMsId(ato);
    ato.setClientId(clientContext.getClientId());
    //TODO state order is important. maybe use regular expression using logicCode l1 + b2 + c3
    setAutoTradeOrder(ato, ato.getTriggerStates());
    setAutoTradeOrder(ato, ato.getPmStates());
    setAutoTradeOrder(ato, ato.getStopStates());
    ato.getPmState().setAutoTradeOrder(ato);

    AutoTradeOrder ret = autoTradeOrderRepo.saveAndFlush(ato);
    cryptoEngine.addAutoTradeOrder(ret.getId());
    return ret;
  }

  private void assignMsId(AutoTradeOrder ato) {
    //TODO 2. Scheduler
    //TODO 2.1, reassign msid for shuting down unessacery ms
    ato.setMsId(engineDiscoveryService.getMsId());
  }

  private void setAutoTradeOrder(AutoTradeOrder ato, List<SetupBase> setups) {
    setups.forEach(state -> state.setAutoTradeOrder(ato));
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
      try {
        ato.getTriggerLogics().add(getTriggerInstance(ato, state, getLogicCode(state)));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
    populateValueFromSupplier(ato.getTriggerLogics(), ato);

    ato.getPmStates().forEach(state -> {
      try {
        ato.getPositionMgmtLogics().add(getTriggerInstance(ato, state, getLogicCode(state)));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

    if (ato.getPmState() != null) {
      try {
        Class aClass = getClass().forName("com.j6crypto.logic.positionmgmt." + getLogicCode(ato.getPmState()));
        ato.setPm((Pm) aClass.getDeclaredConstructor(AutoTradeOrder.class, EntityManager.class)
          .newInstance(ato, em));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    populateValueFromSupplier(ato.getPositionMgmtLogics(), ato);

    ato.getStopStates().forEach(state ->
    {
      try {
        Class aClass = getClass().forName("com.j6crypto.logic.stop." + getLogicCode(state));
        ato.getStopLogics().add((StopTradeLogic)
          aClass.getDeclaredConstructor(AutoTradeOrder.class, state.getClass(), Supplier.class, CandlestickManager.class)
            .newInstance(ato, state, currentDateTimeSupplier, cryptoEngine.getCandlestickManager()));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

    populateValueFromSupplier(ato.getStopLogics(), ato);

    return ato;
  }

  @NotNull
  private String getLogicCode(SetupBase state) {
    return (state.getClass().getAnnotation(Entity.class)).name();
  }

  private TriggerTradeLogic getTriggerInstance(AutoTradeOrder ato, SetupBase state, String logicCode) throws ClassNotFoundException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
    Class aClass = getClass().forName("com.j6crypto.logic.trigger." + logicCode);
    TriggerTradeLogic tradeLogic = (TriggerTradeLogic)
      aClass.getDeclaredConstructor(AutoTradeOrder.class, state.getClass(), Supplier.class, CandlestickManager.class)
        .newInstance(ato, state, currentDateTimeSupplier, cryptoEngine.getCandlestickManager());
    return tradeLogic;
  }

  private void populateValueFromSupplier(List<? extends TradeLogic> tradeLogics, AutoTradeOrder ato) {
    tradeLogics.forEach(tradeLogic -> {
      String valueFrom = tradeLogic.getTradeLogicState().getValueFrom();
      if (!StringUtils.isBlank(valueFrom)) {
        tradeLogic.setValueFromSupplier(() -> ato.getTriggerLogics().stream().filter(logic -> logic.getClass().getSimpleName().equals(valueFrom))
          .findFirst().get().getTradeLogicState().getValue());
      }
    });
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
    int updated = autoTradeOrderRepo.updateStatus(atoId, clientContext.getClientId(), TERMINATED, INACTIVE_ATO_STATUS);
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
