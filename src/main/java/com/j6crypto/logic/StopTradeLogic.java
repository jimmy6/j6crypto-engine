package com.j6crypto.logic;

import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.logic.entity.state.State;
import com.j6crypto.to.setup.AutoTradeOrderSetup;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import static com.j6crypto.to.setup.AutoTradeOrderSetup.Status.PM;
import static com.j6crypto.to.setup.AutoTradeOrderSetup.Status.STOP;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public abstract class StopTradeLogic<T extends State> extends TradeLogic<T> {
  private static Set<AutoTradeOrderSetup.Status> IS_RUNS = new HashSet(Arrays.asList(PM, STOP));

  public StopTradeLogic(AutoTradeOrder autoTradeOrder, T state, Supplier<LocalDateTime> currentDateTimeSupplier) {
    super(autoTradeOrder, state, currentDateTimeSupplier);
  }

  @Override
  public boolean isRun() {
    return IS_RUNS.contains(getAutoTradeOrder().getStatus());
  }
}
