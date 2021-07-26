package com.j6crypto.logic.stop;

import com.j6crypto.logic.TradeLogic;
import com.j6crypto.logic.entity.state.PriceReduceFromHighestState;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
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
public class PriceReduceFromHighest extends TradeLogic<PriceReduceFromHighestState> {
  private static Logger logger = LoggerFactory.getLogger(PriceReduceFromHighest.class);

  public PriceReduceFromHighest(AutoTradeOrder autoTradeOrder, PriceReduceFromHighestState state, Supplier<LocalDateTime> currentDateTimeSupplier) {
    super(autoTradeOrder, state, currentDateTimeSupplier);
  }

  @Override
  public Trade.LongShort runLogic(TimeData timeData) {
    if (getTradeLogicState().getHighestPrice() == null) {
      getTradeLogicState().setHighestPrice(getCurrentPrice());
    }
    //TODO is make the highest price clear, count only after tp profit hit
    BigDecimal priceReduceFromHighPerc = getTradeLogicState().getHighestPrice()
      .subtract(getCurrentPrice()).movePointRight(2).divide(getTradeLogicState().getHighestPrice(), 2, RoundingMode.HALF_UP);
    logger.debug("Monitor profit priceReduceFromHighPerc={}", priceReduceFromHighPerc);
    if (priceReduceFromHighPerc.compareTo(getTradeLogicState().getTpOnPriceReducePerc()) > 0) {
      logger.info("TP profit priceReduceFromHighPerc={}", priceReduceFromHighPerc);
      return Trade.LongShort.LONG;
    }
    if (getTradeLogicState().getHighestPrice().compareTo(getCurrentPrice()) < 0) {
      getTradeLogicState().setHighestPrice(getCurrentPrice());
    }
    return null;
  }
}
