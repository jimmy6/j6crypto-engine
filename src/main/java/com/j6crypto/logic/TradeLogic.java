package com.j6crypto.logic;

import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.logic.entity.state.State;
import com.j6crypto.service.CandlestickManager;
import com.j6crypto.to.Candlestick;
import com.j6crypto.to.TimeData;
import com.j6crypto.to.Trade;
import com.j6crypto.to.setup.AutoTradeOrderSetup;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.function.Supplier;

import static com.j6crypto.engine.EngineUtil.*;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public abstract class TradeLogic<T extends State> {
  private T tradeLogicState;
  private AutoTradeOrder autoTradeOrder;
  private BigDecimal currentPrice;
  private Trade.LongShort signal;
  private TimeData timeData;
  public Supplier<LocalDateTime> currentDateTimeSupplier;
  protected CandlestickManager candlestickManager;

  public TradeLogic(AutoTradeOrder autoTradeOrder, T tradeLogicState, Supplier<LocalDateTime> currentDateTimeSupplier,
                    CandlestickManager candlestickManager) {
    this.tradeLogicState = tradeLogicState;
    this.autoTradeOrder = autoTradeOrder;
    this.currentDateTimeSupplier = currentDateTimeSupplier;
    this.candlestickManager = candlestickManager;
  }

  public Trade.LongShort monitor(TimeData timeData) {
    this.timeData = timeData;
    setCurrentPrice(timeData.getLast());
    prepareData();

    signal = runLogic(timeData);

    return signal;
  }

  protected boolean validateOpenOrder() {
    if (isTimeDataAllowToProcess(getAutoTradeOrder(), timeData, currentDateTimeSupplier)) {
      return true;
    }
    return false;
  }

  public Supplier<LocalDateTime> getCurrentDateTimeSupplier() {
    return currentDateTimeSupplier;
  }

  /**
   * Run even cached or
   */
  public void prepareData() {
  }

  public TimeData getTimeData() {
    return timeData;
  }

  public abstract Trade.LongShort runLogic(TimeData timeData);

  public boolean isRun() {
    return autoTradeOrder.getStatus().equals(AutoTradeOrder.Status.PM);
  }

  public void setCurrentPrice(BigDecimal currentPrice) {
    this.currentPrice = currentPrice;
  }

  public BigDecimal getCurrentPrice() {
    return currentPrice;
  }

  public T getTradeLogicState() {
    return tradeLogicState;
  }

  public AutoTradeOrder getAutoTradeOrder() {
    return autoTradeOrder;
  }

  public Trade.LongShort getSignal() {
    //TODO consider catch
    return signal;
  }
}
