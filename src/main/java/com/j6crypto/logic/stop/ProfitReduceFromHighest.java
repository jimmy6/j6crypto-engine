package com.j6crypto.logic.stop;

import com.j6crypto.logic.StopTradeLogic;
import com.j6crypto.logic.TradeLogic;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.logic.entity.state.PriceReduceFromHighestState;
import com.j6crypto.logic.entity.state.ProfitReduceFromHighestState;
import com.j6crypto.service.CandlestickManager;
import com.j6crypto.to.TimeData;
import com.j6crypto.to.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class ProfitReduceFromHighest extends StopTradeLogic<ProfitReduceFromHighestState> {
  private static Logger logger = LoggerFactory.getLogger(ProfitReduceFromHighest.class);

  public ProfitReduceFromHighest(AutoTradeOrder autoTradeOrder, ProfitReduceFromHighestState state, Supplier<LocalDateTime> currentDateTimeSupplier,
                                 CandlestickManager candlestickManager) {
    super(autoTradeOrder, state, currentDateTimeSupplier, candlestickManager);
  }

  @Override
  public Trade.LongShort runLogic(TimeData timeData) {

    BigDecimal percentageBaseCost = getAutoTradeOrder().getTotalCost();
    BigDecimal profit = getCurrentPrice().multiply(getAutoTradeOrder().getPositionQty()).subtract(percentageBaseCost);

    BigDecimal profitReduceFromHighPerc = getTradeLogicState().getHighestProfit().subtract(profit)
      .movePointRight(2).divide(percentageBaseCost, 2, RoundingMode.HALF_UP);

    logger.debug("Monitor profit ProfitReduceFromHighest={}", profitReduceFromHighPerc);
    if (profitReduceFromHighPerc.compareTo(getTradeLogicState().getTpOnProfitReducePerc()) > 0) {
      logger.info("TP profit ProfitReduceFromHighest={}", profitReduceFromHighPerc);
      return Trade.LongShort.LONG;
    }

    if (getTradeLogicState().getHighestProfit().compareTo(profit) < 0) {
      getTradeLogicState().setHighestProfit(profit);
    }
    return null;
  }
}
