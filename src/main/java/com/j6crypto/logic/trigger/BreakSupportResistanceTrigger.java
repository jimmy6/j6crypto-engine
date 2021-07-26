package com.j6crypto.logic.trigger;

import com.j6crypto.logic.TradeLogic;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.service.CandlestickManager;
import com.j6crypto.to.Candlestick;
import com.j6crypto.to.TimeData;
import com.j6crypto.to.Trade;
import com.j6crypto.to.setup.BreakSupportResistanceTriggerSetup;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class BreakSupportResistanceTrigger extends TradeLogic<BreakSupportResistanceTriggerSetup> {

  public BreakSupportResistanceTrigger(AutoTradeOrder autoTradeOrder, BreakSupportResistanceTriggerSetup state,
                                       Supplier<LocalDateTime> currentDateTimeSupplier
    , CandlestickManager candlestickManager) {
    super(autoTradeOrder, state, currentDateTimeSupplier, candlestickManager);
  }

  @Override
  public Trade.LongShort runLogic(TimeData timeData) {
    LinkedList<Candlestick> recycleCircleList = candlestickManager.getCandleSticks(timeData.getCode());

    Trade.LongShort ret = null;
    BigDecimal max = null;
    BigDecimal min = null;
    if (recycleCircleList.size() >= getTradeLogicState().getBreakPeriod()) {
      int count = 1;
      Iterator<Candlestick> inter = recycleCircleList.iterator();

      while (inter.hasNext() && count <= getTradeLogicState().getBreakPeriod()) {
        Candlestick candlestickCurrent = inter.next();
        System.out.println(candlestickCurrent.getDate().getMinute());
        if (max == null || candlestickCurrent.getHigh().compareTo(max) > 0) {
          max = candlestickCurrent.getHigh();
        }
        if (min == null || min.compareTo(candlestickCurrent.getLow()) > 0) {
          min = candlestickCurrent.getLow();
        }
        count++;
      }

      if ((max.subtract(min).compareTo(getTradeLogicState().getPriceRange()) <= 0) &&
        (getTradeLogicState().getFirstBreakVol() == null || timeData.getVol().compareTo(getTradeLogicState().getFirstBreakVol()) >= 0)) {
        if (timeData.getLast().compareTo(max) > 0
          && timeData.getLast().subtract(max).compareTo(getTradeLogicState().getBreakWithGapBelow()) <= 0) {
          getTradeLogicState().getCommonState().setValue(timeData.getLast());
          ret = Trade.LongShort.LONG;
        } else if (timeData.getLast().compareTo(min) < 0
          && min.subtract(timeData.getLast()).compareTo(getTradeLogicState().getBreakWithGapBelow()) <= 0) {
          getTradeLogicState().getCommonState().setValue(timeData.getLast());
          ret = Trade.LongShort.SHORT;
        }
      }
    }

    return ret;
  }

}
