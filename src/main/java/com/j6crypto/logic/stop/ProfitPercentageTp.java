package com.j6crypto.logic.stop;

import com.j6crypto.logic.StopTradeLogic;
import com.j6crypto.logic.TradeLogic;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.to.setup.ProfitPercentageTpSetup;
import com.j6crypto.to.TimeData;
import com.j6crypto.to.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.function.Supplier;

import static com.j6crypto.to.Trade.LongShort.LONG;
import static com.j6crypto.to.Trade.LongShort.SHORT;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class ProfitPercentageTp extends StopTradeLogic<ProfitPercentageTpSetup> {
  private static Logger logger = LoggerFactory.getLogger(ProfitPercentageTp.class);

  public ProfitPercentageTp(AutoTradeOrder autoTradeOrder, ProfitPercentageTpSetup state, Supplier<LocalDateTime> currentDateTimeSupplier) {
    super(autoTradeOrder, state, currentDateTimeSupplier);
  }

  @Override
  public Trade.LongShort runLogic(TimeData timeData) {
    //TODO cater for long short
    BigDecimal percentageBaseCost = getAutoTradeOrder().getTotalCost();
    BigDecimal profitPerc = getCurrentPrice().multiply(getAutoTradeOrder().getPositionQty())
      .subtract(percentageBaseCost).movePointRight(2).divide(percentageBaseCost, 2, RoundingMode.HALF_UP);
    logger.debug("Monitor profit profitPerc={} ", profitPerc);
    if (profitPerc.compareTo(getTradeLogicState().getProfitPercentageTp()) >= 0) {
      logger.info("TP profit profitPerc={} ", profitPerc);
      return Trade.LongShort.LONG;
    }
    return null;
  }

}
