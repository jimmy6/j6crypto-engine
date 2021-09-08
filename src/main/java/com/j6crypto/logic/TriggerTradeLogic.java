package com.j6crypto.logic;

import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.logic.entity.state.State;
import com.j6crypto.service.CandlestickManager;
import com.j6crypto.to.Trade;
import com.j6crypto.to.setup.AutoTradeOrderSetup;
import com.j6crypto.to.setup.SetupBase;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import static com.j6crypto.to.setup.AutoTradeOrderSetup.Status.*;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public abstract class TriggerTradeLogic<T extends SetupBase> extends TradeLogic<T> {

  public TriggerTradeLogic(AutoTradeOrder autoTradeOrder, T state, Supplier<LocalDateTime> currentDateTimeSupplier,
                           CandlestickManager candlestickManager) {
    super(autoTradeOrder, state, currentDateTimeSupplier, candlestickManager);
  }
  public void postOpenOrder(Trade trade) {}
  @Override
  public boolean isRun() {
    return TRIGGER.equals(getAutoTradeOrder().getStatus());
  }
}
