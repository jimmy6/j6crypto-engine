package com.j6crypto.logic.trigger;

import com.j6crypto.logic.TradeLogic;
import com.j6crypto.logic.TriggerTradeLogic;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.service.CandlestickManager;
import com.j6crypto.to.Candlestick;
import com.j6crypto.to.TimeData;
import com.j6crypto.to.Trade;
import com.j6crypto.to.setup.AutoTradeOrderSetup;
import com.j6crypto.to.setup.BreakSupportResistanceTriggerSetup;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Supplier;

import static com.j6crypto.to.Trade.LongShort.LONG;
import static com.j6crypto.to.Trade.LongShort.SHORT;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class BreakSupportResistanceTrigger extends TriggerTradeLogic<BreakSupportResistanceTriggerSetup> {
  private BigDecimal value;

  public BreakSupportResistanceTrigger(AutoTradeOrder autoTradeOrder, BreakSupportResistanceTriggerSetup state,
                                       Supplier<LocalDateTime> currentDateTimeSupplier
    , CandlestickManager candlestickManager) {
    super(autoTradeOrder, state, currentDateTimeSupplier, candlestickManager);
  }

  @Override
  public Trade.LongShort runLogic(TimeData timeData) {
    LinkedList<Candlestick> candlesticks = getCandlesticks();

    Trade.LongShort ret = null;
    BigDecimal max = null;
    BigDecimal min = null;
    if (candlesticks.size() + 1 >= getTradeLogicState().getBreakPeriod()) {//TODO after improve preload certain data from kafka in engine this checking maybe remove.
      int count = 1;
      Iterator<Candlestick> inter = candlesticks.iterator();
      inter.next();
      // loop from back/new to front/old
      while (inter.hasNext() && count <= getTradeLogicState().getBreakPeriod()) {
        Candlestick candlestickCurrent = inter.next();
        if (max == null || candlestickCurrent.getHigh().compareTo(max) > 0) {
          max = candlestickCurrent.getHigh();
        }
        if (min == null || min.compareTo(candlestickCurrent.getLow()) > 0) {
          min = candlestickCurrent.getLow();
        }
        count++;
      }

      if ((getTradeLogicState().getPriceRange() == null || max.subtract(min).compareTo(getTradeLogicState().getPriceRange()) <= 0)
        && (getTradeLogicState().getFirstBreakVol() == null || timeData.getVol().compareTo(getTradeLogicState().getFirstBreakVol()) >= 0)) {
        if ((getAutoTradeOrder().getLongShort() == null || LONG.equals(getAutoTradeOrder().getLongShort()))
          && getCurrentPrice().compareTo(max) > 0
          && (getTradeLogicState().getBreakWithGapBelow() == null || getCurrentPrice().subtract(max).compareTo(getTradeLogicState().getBreakWithGapBelow()) <= 0)) {
          value = max;
          ret = LONG;
        } else if ((getAutoTradeOrder().getLongShort() == null || SHORT.equals(getAutoTradeOrder().getLongShort()))
          && timeData.getLast().compareTo(min) < 0
          && (getTradeLogicState().getBreakWithGapBelow() == null || min.subtract(getCurrentPrice()).compareTo(getTradeLogicState().getBreakWithGapBelow()) <= 0)) {
          value = min;
          ret = SHORT;
        }
      }
    }

    return ret;
  }

  @Override
  public BigDecimal getValue() {
    return value;
  }
}
