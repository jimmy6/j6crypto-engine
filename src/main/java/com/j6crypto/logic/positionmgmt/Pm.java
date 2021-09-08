package com.j6crypto.logic.positionmgmt;

import com.j6crypto.exception.TradeException;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.to.TimeData;
import com.j6crypto.to.Trade;
import com.j6crypto.to.setup.SetupBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.function.Supplier;


public class Pm<T extends SetupBase> {
  private static Logger logger = LoggerFactory.getLogger(Pm.class);

  protected AutoTradeOrder ato;
  private EntityManager em;
  private Supplier<LocalDateTime> currentDateTimeSupplier;
  private T pmState;
  private TimeData timeData;

  public Pm(AutoTradeOrder autoTradeOrder, EntityManager em) {
    this.ato = autoTradeOrder;
    this.em = em;
    pmState = (T) autoTradeOrder.getPmState();
  }

  public boolean openOrder(TimeData timeData) throws TradeException {
    return false;
  }

  public T getPmState() {
    return pmState;
  }

  protected Trade openMarket(BigDecimal qty, Trade.LongShort longShort, TimeData timeData) throws TradeException {
    this.timeData = timeData;
    if (validateOpenOrder()) {
      Trade trade = new Trade(ato, timeData.getLast(), qty, longShort, Trade.OrderType.MARKET);
      initTrade(trade);
      logger.info(trade.toString());
      ato.getTradePlatform().openMarket(ato.getClientId(), ato.getSymbol(), trade);
      postOpenOrder(trade);
      em.persist(trade);
      return trade;
    }
    return null;
  }

  public void postOpenOrder(Trade trade) {
    if (ato.getFirstTrade() == null)
      ato.setFirstTrade(trade);
    ato.setLastTrade(trade);
//    getAutoTradeOrder().getTrades().add(trade);

    ato.setPositionQty(ato.getPositionQty().add(trade.getQty()));
    ato.setTotalCost(ato.getTotalCost().add(timeData.getLast().multiply(
      trade.getTransactedQty() != null ? trade.getTransactedQty() : trade.getQty()))
      .setScale(2, RoundingMode.HALF_EVEN));//TODO review this

  }

  public boolean validateOpenOrder() {
    if (ato.getTradePlatform().isTimeDataAllowToProcess(ato, timeData, currentDateTimeSupplier)) {
      return true;
    }
    return false;
  }

  public void initTrade(Trade trade) {

  }

}
