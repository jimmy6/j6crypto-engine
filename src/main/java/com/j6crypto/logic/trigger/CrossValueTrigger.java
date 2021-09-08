package com.j6crypto.logic.trigger;

import com.j6crypto.logic.TriggerTradeLogic;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.logic.entity.state.CrossValueTriggerState;
import com.j6crypto.service.CandlestickManager;
import com.j6crypto.to.Candlestick;
import com.j6crypto.to.TimeData;
import com.j6crypto.to.Trade;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class CrossValueTrigger extends TriggerTradeLogic<CrossValueTriggerState> {

  public CrossValueTrigger(AutoTradeOrder autoTradeOrder, CrossValueTriggerState state,
                           Supplier<LocalDateTime> currentDateTimeSupplier, CandlestickManager candlestickManager) {
    super(autoTradeOrder, state, currentDateTimeSupplier, candlestickManager);
  }

  @Override
  public Trade.LongShort runLogic(TimeData timeData) {
    LinkedList<Candlestick> candlesticks = getCandlesticks();

    Iterator<Candlestick> iter = candlesticks.iterator();
    if (iter.hasNext()) iter.next();
    if (iter.hasNext()) {
      BigDecimal previousClosePrice = iter.next().getClose();
      if (previousClosePrice.compareTo(getValueFromSupplier()) < 0 && getValueFromSupplier().compareTo(timeData.getLast()) < 0) {
        getTradeLogicState().setCurrentCrossNo(getTradeLogicState().getCurrentCrossNo() + 1);
        if (getTradeLogicState().getCurrentCrossNo() >= getTradeLogicState().getCrossNo()) {
          return getAutoTradeOrder().getLongShort();
        }
      }
    }

    return null;
  }

  @Override
  public void onPreviousLogicSignal() {
    getTradeLogicState().setCurrentCrossNo(0);
  }

  @Override
  public boolean isRun() {
    return super.isRun() && getValueFromSupplier() != null;
  }
}
