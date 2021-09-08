package com.j6crypto.logic;

import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.logic.entity.state.State;
import com.j6crypto.service.CandlestickManager;
import com.j6crypto.to.Candlestick;
import com.j6crypto.to.TimeData;
import com.j6crypto.to.Trade;
import com.j6crypto.to.setup.AutoTradeOrderSetup;
import com.j6crypto.to.setup.SetupBase;

import java.math.BigDecimal;
import java.text.Bidi;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.LinkedList;
import java.util.function.Supplier;

import static com.j6crypto.engine.EngineUtil.*;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public abstract class TradeLogic<T extends SetupBase> {
  private T tradeLogicState;
  private AutoTradeOrder autoTradeOrder;
  private BigDecimal currentPrice;
  private Trade.LongShort signal;
  private TimeData timeData;
  public Supplier<LocalDateTime> currentDateTimeSupplier;
  protected CandlestickManager candlestickManager;
  private Supplier<BigDecimal> valueFromSupplier;

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
    if (tradeLogicState.getCacheSignalForPeriod() != 0) {
      if (tradeLogicState.getCacheSignalExpiredAt() != null && timeData.getDateTime().isBefore(tradeLogicState.getCacheSignalExpiredAt())) {
        signal = autoTradeOrder.getLongShort();
      } else {
        signal = runLogic(timeData);
        setCacheSignalExpiredAt(timeData);
        setValue();
      }
    } else {
      signal = runLogic(timeData);
      setValue();
    }

    return signal;
  }

  private void setValue() {
    if (signal != null && getAutoTradeOrder().getLongShort().equals(signal)) {
      tradeLogicState.setValue(getValue());
    } else {
      tradeLogicState.setValue(null);
    }
  }

  private void setCacheSignalExpiredAt(TimeData timeData) {
    LocalDateTime cacheSignalExpiredAt = null;
    if (signal != null && getAutoTradeOrder().getLongShort().equals(signal)) {
      if (AutoTradeOrderSetup.Period.MIN1.equals(getAutoTradeOrder().getPeriod())) {
        cacheSignalExpiredAt = timeData.getDateTime().plus(
          tradeLogicState.getCacheSignalForPeriod() + 1, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.MINUTES);
      } else {
        throw new RuntimeException("Not supported getAutoTradeOrder().getPeriod -> " + getAutoTradeOrder().getPeriod());
      }
    }
    tradeLogicState.setCacheSignalExpiredAt(cacheSignalExpiredAt);
  }

  public void setValueFromSupplier(Supplier<BigDecimal> valueFromSupplier) {
    this.valueFromSupplier = valueFromSupplier;
  }

  public BigDecimal getValueFromSupplier() {
    return valueFromSupplier.get();
  }

  protected BigDecimal getValue() {
    return getCurrentPrice();
  }

  protected LinkedList<Candlestick> getCandlesticks() {
    return candlestickManager.getCandleSticks(timeData.getCode());
  }

  public boolean validateOpenOrder() {
    if (getAutoTradeOrder().getTradePlatform().isTimeDataAllowToProcess(getAutoTradeOrder(), timeData, currentDateTimeSupplier)) {
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

  public int getPeriod4PrepareTimeData() {
    return 0;
  }

  public void onPreviousLogicSignal() {
  }

  public TimeData getTimeData() {
    return timeData;
  }

  public abstract Trade.LongShort runLogic(TimeData timeData);

  public abstract boolean isRun();

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
    return signal;
  }
}
