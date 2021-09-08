package com.j6crypto.logic.trigger;

import com.j6crypto.logic.TradeLogic;
import com.j6crypto.logic.TriggerTradeLogic;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.service.CandlestickManager;
import com.j6crypto.to.Candlestick;
import com.j6crypto.to.setup.DummyTriggerSetup;
import com.j6crypto.to.TimeData;
import com.j6crypto.to.Trade;

import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */

public class DummyTrigger extends TriggerTradeLogic<DummyTriggerSetup> {
  public DummyTrigger(AutoTradeOrder autoTradeOrder, DummyTriggerSetup state, Supplier<LocalDateTime> currentDateTimeSupplier
  , CandlestickManager candlestickManager) {
    super(autoTradeOrder, state, currentDateTimeSupplier, candlestickManager);
  }

  @Override
  public Trade.LongShort getSignal() {
    return getTradeLogicState().getRunLogic();
  }

  @Override
  public Trade.LongShort runLogic(TimeData timeData) {
    return getTradeLogicState().getRunLogic();
  }
}
