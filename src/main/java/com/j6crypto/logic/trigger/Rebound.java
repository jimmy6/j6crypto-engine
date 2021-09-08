package com.j6crypto.logic.trigger;

import com.j6crypto.logic.TriggerTradeLogic;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.logic.entity.state.MartingaleDoublePmState;
import com.j6crypto.logic.entity.state.ReboundState;
import com.j6crypto.service.CandlestickManager;
import com.j6crypto.to.TimeData;
import com.j6crypto.to.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.function.Supplier;

import static com.j6crypto.to.setup.AutoTradeOrderSetup.Status.PM;
import static com.j6crypto.to.setup.AutoTradeOrderSetup.Status.TRIGGER;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class Rebound extends TriggerTradeLogic<ReboundState> {
  private static Logger logger = LoggerFactory.getLogger(Rebound.class);

  public Rebound(AutoTradeOrder autoTradeOrder, ReboundState state, Supplier<LocalDateTime> currentDateTimeSupplier,
                 CandlestickManager candlestickManager) {
    super(autoTradeOrder, state, currentDateTimeSupplier, candlestickManager);
  }

  @Override
  public Trade.LongShort runLogic(TimeData timeData) {
    if (getTradeLogicState().getLowestPrice() == null) {
      getTradeLogicState().setLowestPrice(getCurrentPrice());
      getTradeLogicState().setHighestPrice(getCurrentPrice());
    }
    if (getTradeLogicState().getLowestPrice().compareTo(getCurrentPrice()) > 0) {
      getTradeLogicState().setLowestPrice(getCurrentPrice());
    } else if (getTradeLogicState().getHighestPrice().compareTo(getCurrentPrice()) < 0) {
      getTradeLogicState().setHighestPrice(getCurrentPrice());
    }

    if (getCurrentPrice().compareTo(getAutoTradeOrder().getLastTrade().getPrice()) < 0 &&
      (getTradeLogicState().getPreviousTradedLowestPrice() == null
        || getTradeLogicState().getPreviousTradedLowestPrice().compareTo(getCurrentPrice()) > 0)) {
      BigDecimal reboundPerc = getReboundPerc(getCurrentPrice(), getTradeLogicState());
      logger.info("reboundPerc = {}", reboundPerc.toPlainString());
      if (reboundPerc.compareTo(getTradeLogicState().getReboundEnterPerc()) >= 0) {
        logger.info("Martingale entry reboundPerc={}, {}", reboundPerc, getTradeLogicState());
        postOpenOrder(null);
        return getAutoTradeOrder().getLongShort();
      }
    }
    return null;
  }

  private static BigDecimal getReboundPerc(BigDecimal currentPrice, ReboundState reboundState) {
    if (reboundState.getLowestPrice().compareTo(currentPrice) < 0) {
      BigDecimal diffPrice = currentPrice.subtract(reboundState.getLowestPrice());
      return diffPrice.movePointRight(2).divide(reboundState.getLowestPrice(), 2, RoundingMode.HALF_UP);
    }
    return BigDecimal.ZERO;
  }

  @Override
  public void postOpenOrder(Trade trade) {
    getTradeLogicState().setLowestPrice(getCurrentPrice());//is this 3 suitable in openOrder if trade fail/expired to open it will be skip
    getTradeLogicState().setHighestPrice(getCurrentPrice());//is this 3 suitable in openOrder
    getTradeLogicState().setPreviousTradedLowestPrice(getCurrentPrice());//is this 3 suitable in openOrder
  }

  @Override
  public boolean isRun() {
    return PM.equals(getAutoTradeOrder().getStatus()) && getAutoTradeOrder().getFirstTrade() != null;
  }
}
