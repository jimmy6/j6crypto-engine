package com.j6crypto.service;

import com.j6crypto.util.DateUtil;
import com.j6crypto.engine.EngineUtil;
import com.j6crypto.to.Candlestick;
import com.j6crypto.to.TimeData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CandlestickManager {
  private static final Log log = LogFactory.getLog(CandlestickManager.class);

  private Map<String, LinkedList<Candlestick>> candlesticksMap = new HashMap<>();
  private int candleBarSize = 120;
  private Map<String, LinkedList<Candlestick>> dailyCandlesticks = new HashMap<>();
  private int dailyCandleBarSize = 0;
  private boolean transactedPrice = true;

  public CandlestickManager() {
    // TODO Auto-generated constructor stub
  }

  public CandlestickManager(int dailyCandleBarSize) {
    cleanCandleAfterIdleSecond = 999999999999999999l;//disable auto clean
    this.dailyCandleBarSize = dailyCandleBarSize;
  }

  public CandlestickManager(int candleBarSize, boolean transactedPrice) {
    this.transactedPrice = transactedPrice;
    cleanCandleAfterIdleSecond = 999999999999999999l;//disable auto clean
    this.candleBarSize = candleBarSize;
  }

  public CandlestickManager(int candleBarSize, boolean transactedPrice, int dailyCandleBarSize) {
    this.transactedPrice = transactedPrice;
    cleanCandleAfterIdleSecond = 999999999999999999l;//disable auto clean
    this.candleBarSize = candleBarSize;
    this.dailyCandleBarSize = dailyCandleBarSize;
  }

  public CandlestickManager(boolean transactedPrice) {
    this.transactedPrice = transactedPrice;
  }

  /**
   * Accumulated even overnight
   *
   * @param code
   * @return
   */
  public LinkedList<Candlestick> getCandleSticks(String code) {
    LinkedList<Candlestick> recycleCircleList = candlesticksMap.get(code);
    if (recycleCircleList == null) {
      recycleCircleList = new LinkedList<Candlestick>();
      candlesticksMap.put(code, recycleCircleList);
    }

    return recycleCircleList;
  }

  public Candlestick getCurrentHighLow(String code) {
    LinkedList<Candlestick> recycleCircleList = candlesticksMap.get(code);
    Candlestick candlestick = new Candlestick(recycleCircleList.getFirst().getOpen(), recycleCircleList.getFirst().getDate());
    for (Candlestick candlestickLoop : recycleCircleList) {
      if (candlestick.getHigh().compareTo(candlestickLoop.getHigh()) < 0) {
        candlestick.setHigh(candlestickLoop.getHigh());
      }
      if (candlestick.getLow().compareTo(candlestickLoop.getLow()) > 0) {
        candlestick.setLow(candlestickLoop.getLow());
      }
      candlestick.setClose(candlestickLoop.getClose());
    }
    return candlestick;
  }

  /**
   * due to cancle is pupulate when addData( ) being called. but how about if there is no trade happened.
   *
   * @param code
   */
  private void populateMissingCandle(String code) {
//		LinkedList<Candlestick> recycleCircleList = candlesticksMap.get(code);
//		if(recycleCircleList==null){
//			recycleCircleList = new LinkedList<Candlestick>();
//			candlesticksMap.put(code, recycleCircleList);
//		}
//		Iterator<Candlestick> inter = recycleCircleList.iterator();
//		
//		while (inter.hasNext() ) {
//			Candlestick candlestickCurrent = inter.next();
//			Calendar c = DateUtil.setTime(candlestickCurrent.getDate());
//			if(candlestickCurrent.getDate().getMonth()){
//				
//			}
//		}
  }

  /**
   * Clean if not within business hour
   *
   * @param code
   * @return
   */
  public LinkedList<Candlestick> getCandleSticks(String code, Map<String, LinkedList<Candlestick>> candleSticksMap) {

    LinkedList<Candlestick> recycleCircleList = candleSticksMap.get(code);
    if (recycleCircleList == null) {
      recycleCircleList = new LinkedList<Candlestick>();
      candleSticksMap.put(code, recycleCircleList);
    }

    return recycleCircleList;
  }

  private void updateCandleStick(Candlestick candlestick, TimeData timeData) {
    candlestick.setClose(getPrice(timeData));
    if (candlestick.getHigh().compareTo(getPrice(timeData)) < 0) {
      candlestick.setHigh(getPrice(timeData));
    }
    if (candlestick.getLow().compareTo(getPrice(timeData)) > 0) {
      candlestick.setLow(getPrice(timeData));
    }
//    if (timeData.getVol() != null)
//      candlestick.setVol(timeData.getVol() - candlestick.getStartVol());
  }

  private BigDecimal getPrice(TimeData timeData) {
    return timeData.getLast();
  }

  private long cleanCandleAfterIdleSecond = 180 * 60 ;

  private void addDailyTimeData(TimeData timeData) {

    Deque<Candlestick> candlesticks = getCandleSticks(timeData.getCode(), dailyCandlesticks);

    if (candlesticks.size() != 0 && DateUtil.isSameDay(candlesticks.getFirst().getDate(), timeData.getDateTime())) {
      Candlestick candlestick = candlesticks.getFirst();
      updateCandleStick(candlestick, timeData);
    } else {
      if (candlesticks.size() >= dailyCandleBarSize) {
        candlesticks.removeLast();
      }
      candlesticks.addFirst(EngineUtil.buildCandleStick(timeData));
    }
  }

//  public void loopDailyCandlestock(String code, CallBackCandleStick backCandleStick, Date fromDate) {
//    Deque<Candlestick> candlesticks = getCandleSticks(code, dailyCandlesticks);
//
//    for (Candlestick candlestick : candlesticks) {
//      if (fromDate.getTime() <= candlestick.getDate().getTime())
//        backCandleStick.loop(candlestick);
//    }
//
//  }
//
//  public void loopCandlestock(String code, CallBackCandleStick backCandleStick, Date fromDate) {
//    Deque<Candlestick> candlesticks = getCandleSticks(code, candlesticksMap);
//
//    for (Candlestick candlestick : candlesticks) {
//      if (fromDate.getTime() <= candlestick.getDate().getTime())
//        backCandleStick.loop(candlestick);
//    }
//
//  }

  /**
   * current only support minute candle
   *
   * @param timeStockData
   */
  public void addData(TimeData timeStockData) {
    if (getPrice(timeStockData) == null) {
      return;
    }
    if (dailyCandleBarSize != 0) {
      addDailyTimeData(timeStockData);
    }
    Candlestick candlestick;
    LinkedList<Candlestick> recycleCircleList = candlesticksMap.get(timeStockData.getCode());
    if (recycleCircleList == null) {
      recycleCircleList = new LinkedList<>();
      candlesticksMap.put(timeStockData.getCode(), recycleCircleList);
    } else {
      if (!recycleCircleList.isEmpty() && recycleCircleList.getFirst().getDate().isBefore(timeStockData.getDateTime().minusSeconds(cleanCandleAfterIdleSecond))) {
        recycleCircleList.clear();
      }
    }

    int minute = timeStockData.getDateTime().getMinute();

    int minuteLastCandle = -1;
    if (recycleCircleList.size() > 0)
      minuteLastCandle = recycleCircleList.getFirst().getDate().getMinute();

    if (minute != minuteLastCandle) {
      if (recycleCircleList.size() > 0)
        recycleCircleList.getFirst().setClosed(true);
      if (recycleCircleList.size() >= candleBarSize)
        recycleCircleList.removeLast();

      candlestick = EngineUtil.buildCandleStick(timeStockData);
//      if (timeStockData.getLastBuySell() != null)
//        candlestick.setVol(timeStockData.getLastVol() == null ? 0 : timeStockData.getLastVol());
//      else
//        candlestick.setVol(0);
      recycleCircleList.addFirst(candlestick);
    } else {
      candlestick = recycleCircleList.getFirst();
      updateCandleStick(candlestick, timeStockData);
//			candlestick.setClose(timeStockData.getLast());
//			if (candlestick.getHigh() < timeStockData.getLast()) {
//				candlestick.setHigh(timeStockData.getLast());
//			}
//			if (candlestick.getLow() > timeStockData.getLast()) {
//				candlestick.setLow(timeStockData.getLast());
//			}
    }
  }

  public void setCleanCandleAfterIdleSecond(int cleanCandleAfterIdleSecond) {
    this.cleanCandleAfterIdleSecond = cleanCandleAfterIdleSecond;
  }

  public void cleanCandle() {
    candlesticksMap.clear();
  }

  public Map<String, LinkedList<Candlestick>> getDailyCandlesticks() {
    return dailyCandlesticks;
  }

}
