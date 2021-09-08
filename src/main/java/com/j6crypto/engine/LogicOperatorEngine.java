package com.j6crypto.engine;

import com.j6crypto.logic.TriggerTradeLogic;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.to.Trade;
import com.j6crypto.to.setup.AutoTradeOrderSetup.LogicOperator;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.j6crypto.to.Trade.LongShort.LONG;
import static com.j6crypto.to.setup.AutoTradeOrderSetup.LogicOperator.*;
import static org.springframework.util.CollectionUtils.isEmpty;

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

  @Deprecated
  public static Trade.LongShort sumPmSignals(AutoTradeOrder autoTradeOrder) {
    if (!autoTradeOrder.getPositionMgmtLogics().isEmpty()
      && autoTradeOrder.getPositionMgmtLogics().stream().allMatch(a -> LONG.equals(a.getSignal()))) {
      return LONG;
    }
    return null;
  }

  public static Trade.LongShort sumTriggerSignals(List<TriggerTradeLogic> triggerTradeLogics, LogicOperator logicOperator, Trade.LongShort longShort) {
    if (!isEmpty(triggerTradeLogics)) {
      if ((AND.equals(logicOperator) || A.equals(logicOperator)) &&
        triggerTradeLogics.stream().allMatch(a -> longShort.equals(a.getSignal()))) {
        return longShort;
      } else if ((OR.equals(logicOperator) || O.equals(logicOperator))
        && triggerTradeLogics.stream().anyMatch(a -> longShort.equals(a.getSignal()))) {
        return longShort;
      }
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
