package com.j6crypto.engine;

import com.j6crypto.exception.TradeException;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.to.TimeData;
import com.j6crypto.to.Trade;

import java.time.LocalDateTime;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public abstract class TradePlatform {

  public abstract void openMarket(Integer clientId, String code, Trade trade) throws TradeException;

  public boolean isTimeDataAllowToProcess(AutoTradeOrder ato, TimeData timeData, Supplier<LocalDateTime> currentDateTimeSupplier){
    return true;
  }
}
