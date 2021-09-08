package com.j6crypto.engine;

import com.binance.api.client.domain.event.CandlestickEvent;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.to.Candlestick;
import com.j6crypto.to.TimeData;
import com.j6crypto.to.setup.AutoTradeOrderSetup;
import com.j6crypto.to.setup.AutoTradeOrderSetup.Period;
import org.apache.tomcat.jni.Local;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;

import static java.time.Instant.ofEpochMilli;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class EngineUtil {
  public static void main(String[] args) {

  }

  public static BigDecimal priceAfterPercentage(BigDecimal price, BigDecimal percentage) {
    return percentage.movePointLeft(2).multiply(price);
  }

  public static TimeData toTimeData(CandlestickEvent response) {
    return new TimeData(response.getSymbol(), new BigDecimal(response.getClose()),
      LocalDateTime.ofInstant(ofEpochMilli(response.getCloseTime()), ZoneId.systemDefault()),
      new BigDecimal(response.getVolume()));
  }

  public static long getEpochMinute(LocalDateTime now) {
    return now.toEpochSecond(ZoneOffset.UTC) / 60;
  }

  public static boolean isTimeData1PeriodDelay(Period period, TimeData timeData, Supplier<LocalDateTime> currentDateTimeSupplier) {
    int minute;
    if (Period.MIN1.equals(period)) {
      minute = 1;
    } else {
      throw new UnsupportedOperationException(period + " not supported.");
    }

    return getEpochMinute(timeData.getDateTime()) + minute == getEpochMinute(currentDateTimeSupplier.get());
  }

  public static Candlestick buildCandleStick(TimeData timeData) {
    Candlestick candlestick = new Candlestick(timeData.getLast(), timeData.getDateTime());

//    candlestick.setOpen(timeData.getOpen()==null?timeData.getLast():timeData.getOpen());
//    candlestick.setHigh( timeData.getHigh()==null?timeData.getLast():timeData.getHigh());
//    candlestick.setLow(timeData.getLow()==null?timeData.getLast():timeData.getLow());
//    candlestick.setVol(timeData.getVol()==null?0:timeData.getVol());
//    candlestick.setStartVol(timeData.getVol()==null?0:timeData.getVol());
    return candlestick;
  }
//  public static BigDecimal getProfit(BigDecimal currentPrice, AutoTradeOrder autoTradeOrder) {
//    BigDecimal profit = currentPrice.multiply(autoTradeOrder.getPositionQty()).subtract(autoTradeOrder.getTotalCost());
//    return profit;
//  }
//
//  public static BigDecimal getPercFromTo(BigDecimal ){
//    BigDecimal profitReduceFromHighPerc = getTradeLogicState().getHighestProfit().subtract(profit)
//      .movePointRight(2).divide(percentageBaseCost, 2, RoundingMode.HALF_UP);
//  }
}
