package com.j6crypto.logic;

import com.j6crypto.engine.EngineUtil;
import com.j6crypto.engine.TradePlatform;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.logic.entity.state.State;
import com.j6crypto.to.Trade;
import com.j6crypto.to.setup.AutoTradeOrderSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import static com.j6crypto.engine.EngineUtil.getEpochMinute;


/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public abstract class PmTradeLogic<T extends State> extends TradeLogic<T> {
  private static Logger logger = LoggerFactory.getLogger(PmTradeLogic.class);

  public TradePlatform tradePlatform;
  public EntityManager em;

  public PmTradeLogic(AutoTradeOrder autoTradeOrder, T state, Supplier<LocalDateTime> currentDateTimeSupplier) {
    super(autoTradeOrder, state, currentDateTimeSupplier);
  }

  public void openMarket(BigDecimal qty, Trade.LongShort longShort) {
    if (validateOpenOrder()) {
      Trade trade = new Trade(getAutoTradeOrder(), getCurrentPrice(), qty, longShort, Trade.OrderType.MARKET);
      initTrade(trade);
      logger.info(trade.toString());
      try {
        tradePlatform.openMarket(getAutoTradeOrder().getClientId(), getAutoTradeOrder().getSymbol(), trade);
        postOpenOrder(trade);
        em.persist(trade);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void postOpenOrder(Trade trade) {
    if (getAutoTradeOrder().getFirstTrade() == null)
      getAutoTradeOrder().setFirstTrade(trade);
    getAutoTradeOrder().setLastTrade(trade);
//    getAutoTradeOrder().getTrades().add(trade);

    getAutoTradeOrder().setPositionQty(getAutoTradeOrder().getPositionQty().add(trade.getQty()));
  }

  public void initTrade(Trade trade) {

  }

  public void setTradePlatform(TradePlatform tradePlatform) {
    this.tradePlatform = tradePlatform;
  }

  public void setEm(EntityManager em) {
    this.em = em;
  }
}
