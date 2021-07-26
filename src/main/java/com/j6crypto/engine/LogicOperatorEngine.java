package com.j6crypto.engine;

import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.to.Trade;
import com.j6crypto.to.setup.AutoTradeOrderSetup.LogicOperator;

import static com.j6crypto.to.Trade.LongShort.LONG;
import static com.j6crypto.to.setup.AutoTradeOrderSetup.LogicOperator.AND;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class LogicOperatorEngine {
  public static Trade.LongShort sumStopSignals(AutoTradeOrder autoTradeOrder) {
    boolean matched;
    if (AND.equals(autoTradeOrder.getStopLogicOperator())) {
      matched = autoTradeOrder.getStopLogics().stream().
        allMatch(a -> LONG.equals(a.getSignal()));
    } else {
      matched = autoTradeOrder.getStopLogics().stream().
        anyMatch(a -> LONG.equals(a.getSignal()));
    }

    return matched ? LONG : null;
  }

  public static Trade.LongShort sumPmSignals(AutoTradeOrder autoTradeOrder) {
    if (autoTradeOrder.getPositionMgmtLogics().stream().allMatch(a -> LONG.equals(a.getSignal()))) {
      return LONG;
    }
    return null;
  }

  public static Trade.LongShort sumTriggerSignals(AutoTradeOrder autoTradeOrder) {
    Trade.LongShort signal = autoTradeOrder.getTriggerLogics().get(0).getSignal();
    if (autoTradeOrder.getTriggerLogics().stream().allMatch(a -> signal.equals(a.getSignal()))) {
      return signal;
    }
    return null;
  }

//  public static Trade.LongShort runStopLogics(AutoTradeOrder autoTradeOrder, Trade.LongShort expectedLongShort) {
//    boolean matched;
//    if (LogicOperator.AND.equals(autoTradeOrder.getStopLogicOperator())) {
//      matched = autoTradeOrder.getStopLogics().stream().
//        allMatch(a -> expectedLongShort.equals(a.getSignal()));
//      return expectedLongShort;
//    } else {
//      matched = autoTradeOrder.getStopLogics().stream().
//        anyMatch(a -> expectedLongShort.equals(a.getSignal()));
//    }
//
//    return matched ? expectedLongShort : null;
//  }
}
