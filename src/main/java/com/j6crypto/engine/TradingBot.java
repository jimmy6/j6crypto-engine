package com.j6crypto.engine;

import com.j6crypto.exception.TradeException;
import com.j6crypto.logic.TradeLogic;
import com.j6crypto.logic.TriggerTradeLogic;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.repo.AutoTradeOrderRepo;
import com.j6crypto.to.TimeData;
import com.j6crypto.to.Trade;
import com.j6crypto.to.setup.AutoTradeOrderSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.j6crypto.logic.entity.state.AutoTradeOrder.Action.OPEN_TRADE;
import static com.j6crypto.to.Trade.LongShort.LONG;
import static com.j6crypto.to.Trade.LongShort.SHORT;
import static com.j6crypto.to.Trade.OrderType.MARKET;
import static com.j6crypto.to.setup.AutoTradeOrderSetup.Status.*;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Component
public class TradingBot {
  private static Logger logger = LoggerFactory.getLogger(TradingBot.class);
  @Autowired
  private AutoTradeOrderRepo autoTradeOrderRepo;

  @PersistenceContext
  private EntityManager em;

  @Autowired
  private Supplier<LocalDateTime> currentDateTimeSupplier;

  @Transactional
  public AutoTradeOrder monitorPm(AutoTradeOrder ato, TimeData timeData) {
    try {
      runMonitor(AutoTradeOrderSetup.LogicOperator.AND, ato.getPositionMgmtLogics(), timeData);
      Trade.LongShort longShort = LogicOperatorEngine.sumPmSignals(ato);
      if (LONG.equals(longShort) && ato.getTradePlatform().isTimeDataAllowToProcess(ato, timeData, currentDateTimeSupplier)) {
        ato.setStatus(STOP);
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      ato.setStatus(ERR);
    }
    return autoTradeOrderRepo.save(ato);
  }

  @Transactional
  public AutoTradeOrder monitorStop(AutoTradeOrder ato, TimeData timeData) {
    try {
      runMonitor(ato.getStopLogicOperator(), ato.getStopLogics(), timeData);
      Trade.LongShort longShort = LogicOperatorEngine.sumStopSignals(ato);
      if (LONG.equals(longShort) &&
        ato.getTradePlatform().isTimeDataAllowToProcess(ato, timeData, currentDateTimeSupplier)) {//TODO  is it isTimeData1PeriodDelay right to put in stop?
        ato.setAction(OPEN_TRADE);
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      ato.setStatus(ERR);//TODO save error to DB
    }
    return autoTradeOrderRepo.save(ato);
  }

  @Transactional
  public void closeTrade(AutoTradeOrder ato, TimeData timeData) throws TradeException {
    new OpenTraderHandler() {
      @Override
      public void openOrder(AutoTradeOrder ato) throws TradeException {
        Trade trade = new Trade(ato, timeData.getLast(), ato.getPositionQty(),
          ato.getFirstTrade().getLongShort() == LONG ? SHORT : LONG, MARKET);
        ato.getTradePlatform().openMarket(ato.getClientExchangeId(), ato.getSymbol(), trade);
        ato.setLastTrade(trade);
        ato.setStatus(FINISH);
        em.persist(trade);
      }
    }.run(ato);
  }

  @Transactional
  public void openTrade(AutoTradeOrder ato, TimeData timeData) throws TradeException {
    new OpenTraderHandler() {
      @Override
      public void openOrder(AutoTradeOrder ato) throws TradeException {
        boolean nextStatusPm = ato.getPm().openOrder(timeData);
        if (nextStatusPm && !isEmpty(ato.getPositionMgmtLogics()))
          ato.setStatus(PM);
        else {
          ato.setStatus(STOP);
        }
      }
    }.run(ato);
  }

  public abstract class OpenTraderHandler {
    public abstract void openOrder(AutoTradeOrder ato) throws TradeException;

    public void run(AutoTradeOrder ato) throws TradeException {
      try {
        openOrder(ato);
      } catch (TradeException e) {
        if (e.getRetryMode() == null) {
          logger.error(e.getMessage(), e);//TODO store log to db? retry?
          ato.setStatus(ERR);
        } else {
          throw e;
        }
      } catch (Exception e) {
        logger.error(e.getMessage(), e);//TODO store log to db? retry?
        ato.setStatus(ERR);
      }
      ato.setAction(null);
      autoTradeOrderRepo.save(ato);
    }
  }

  @Transactional
  public AutoTradeOrder monitorTrigger(AutoTradeOrderSetup.LogicOperator logicOperator, List<TriggerTradeLogic> triggerTradeLogics, AutoTradeOrder ato, TimeData timeData) {
    try {
      runMonitor(logicOperator, triggerTradeLogics, timeData);
      if (!isEmpty(triggerTradeLogics) && ato.getTradePlatform().isTimeDataAllowToProcess(ato, timeData, currentDateTimeSupplier)) {
        Trade.LongShort signal = LogicOperatorEngine.sumTriggerSignals(triggerTradeLogics, logicOperator, ato.getLongShort());
        if (signal != null && ato.getLongShort().equals(signal)) {
          ato.setAction(OPEN_TRADE);
        }
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      ato.setStatus(ERR);
    }
    return autoTradeOrderRepo.save(ato);
  }

  private void runMonitor(AutoTradeOrderSetup.LogicOperator logicOperator, List<? extends TradeLogic> tradeLogics, TimeData timeData) {
    Boolean[] onPreviousLogicSignal = new Boolean[]{false};
    Predicate<TradeLogic> tradeLogicPredicate = (tradeLogic) -> {
      if (onPreviousLogicSignal[0]) {
        tradeLogic.onPreviousLogicSignal();
        onPreviousLogicSignal[0] = false;
      }
      Trade.LongShort signalb4 = tradeLogic.getSignal();//TODO signal store in state? for restore
      Trade.LongShort signal = tradeLogic.monitor(timeData);
      if (signalb4 == null && signal != null) {
        onPreviousLogicSignal[0] = true;
      }
      return signal != null;
    };

    runLogicOperator(logicOperator, tradeLogics.stream().filter(tradeLogic -> tradeLogic.isRun()), tradeLogicPredicate);
  }

  private void runLogicOperator(AutoTradeOrderSetup.LogicOperator logicOperator, Stream<? extends TradeLogic> stream,
                                Predicate<TradeLogic> tradeLogicPredicate) {
    if (AutoTradeOrderSetup.LogicOperator.A.equals(logicOperator)) {
      stream.takeWhile(tradeLogicPredicate).forEach((a) -> {
      });
    } else if (AutoTradeOrderSetup.LogicOperator.AND.equals(logicOperator)) {
      stream.forEach(tradeLogicPredicate::test);
    } else {
      throw new RuntimeException("Not supproted logicOperator " + logicOperator.name());
    }
  }

  public static void main(String[] args) {
    IntStream.range(1, 20).filter(a -> a <= 5).takeWhile(a -> {
      System.out.println(a);
      return a <= 5;
    }).summaryStatistics();
  }
}
