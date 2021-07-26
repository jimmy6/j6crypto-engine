package com.j6crypto.logic.positionmgmt;

import com.j6crypto.logic.PmTradeLogic;
import com.j6crypto.service.CandlestickManager;
import com.j6crypto.to.setup.OpenMarketPriceSetup;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.to.TimeData;
import com.j6crypto.to.Trade;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class OpenMarketPrice extends PmTradeLogic<OpenMarketPriceSetup> {

  public OpenMarketPrice(AutoTradeOrder autoTradeOrder, OpenMarketPriceSetup state, Supplier<LocalDateTime> currentDateTimeSupplier,
                         CandlestickManager candlestickManager) {
    super(autoTradeOrder, state, currentDateTimeSupplier, candlestickManager);
  }

  @Override
  public Trade.LongShort runLogic(TimeData timeData) {
    openMarket(getTradeLogicState().getQty(), getTradeLogicState().getLongShort());
    return null;
  }

  @Override
  public boolean isRun() {
    return getAutoTradeOrder().getFirstTrade() == null;
  }
}
