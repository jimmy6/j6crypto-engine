package com.j6crypto.engine;

import com.j6crypto.engine.entity.ClientExchange;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.repo.AutoTradeOrderRepo;
import com.j6crypto.to.TimeData;
import com.j6crypto.to.Trade;
import com.j6crypto.to.setup.AutoTradeOrderSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.j6crypto.engine.EngineUtil.isTimeData1PeriodDelay;
import static com.j6crypto.engine.EngineUtil.isTimeDataAllowToProcess;
import static com.j6crypto.to.Trade.LongShort.LONG;
import static com.j6crypto.to.Trade.LongShort.SHORT;
import static com.j6crypto.to.setup.AutoTradeOrderSetup.Status.*;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Component
public class CryptoBot {
  @Autowired
  private AutoTradeOrderRepo autoTradeOrderRepo;

  @PersistenceContext
  private EntityManager em;

  @Autowired
  private Supplier<LocalDateTime> currentDateTimeSupplier;

  @Transactional
  public AutoTradeOrder monitorPm(AutoTradeOrder ato, TimeData timeData) {
    try {
      ato.getPositionMgmtLogics().stream().filter(tradeLogic -> tradeLogic.isRun()).forEach(tradeLogic -> {
        tradeLogic.monitor(timeData);
      });
      Trade.LongShort longShort = LogicOperatorEngine.sumPmSignals(ato);
      if (LONG.equals(longShort) && isTimeDataAllowToProcess(ato, timeData, currentDateTimeSupplier)) {
        ato.setStatus(STOP);
      }
    } catch (Exception e) {
      ato.setStatus(ERR);
    }
    return autoTradeOrderRepo.save(ato);
  }

  @Transactional
  public AutoTradeOrder monitorStop(AutoTradeOrder ato, TimeData timeData) {
    try {
      ato.getStopLogics().stream().filter(tradeLogic -> tradeLogic.isRun()).forEach(tradeLogic -> {
        tradeLogic.monitor(timeData);
      });
      Trade.LongShort longShort = LogicOperatorEngine.sumStopSignals(ato);
      if (LONG.equals(longShort) &&
        isTimeDataAllowToProcess(ato, timeData, currentDateTimeSupplier)) {//TODO  is it isTimeData1PeriodDelay right to put in stop?
        Trade trade = new Trade(ato, timeData.getLast(), ato.getPositionQty(),
          ato.getFirstTrade().getLongShort() == LONG ? SHORT : LONG, Trade.OrderType.MARKET);
        //TODO what happen if openMarket fail/EXPIRED - status=EXPIRED,timeInForce=GTC,type=MARKET,side=SELL,fills=
        try {
          ato.getTradePlatform().openMarket(ato.getClientExchangeId(), ato.getSymbol(), trade);
          ato.setLastTrade(trade);
          ato.setStatus(FINISH);
          em.persist(trade);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } catch (Exception e) {
      ato.setStatus(ERR);
    }
    return autoTradeOrderRepo.save(ato);
  }

  @Transactional
  public AutoTradeOrder monitorTrigger(AutoTradeOrder ato, TimeData timeData) {
    try {
      ato.getTriggerLogics().stream().filter(tradeLogic -> tradeLogic.isRun()).forEach(tradeLogic -> {
        tradeLogic.monitor(timeData);
      });
      if (!isEmpty(ato.getTriggerLogics()) && isTimeDataAllowToProcess(ato, timeData, currentDateTimeSupplier)) {
        Trade.LongShort signal = LogicOperatorEngine.sumTriggerSignals(ato);
        if (signal != null) {
          ato.setStatus(AutoTradeOrderSetup.Status.PM);
        }
      }
    } catch (Exception e) {
      ato.setStatus(ERR);
    }
    return autoTradeOrderRepo.save(ato);
  }

}
