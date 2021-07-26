package com.j6crypto.logic.positionmgmt;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.j6crypto.logic.PmTradeLogic;
import com.j6crypto.logic.entity.state.ReboundMartingaleState;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.service.CandlestickManager;
import com.j6crypto.to.TimeData;
import com.j6crypto.to.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class ReboundMartingale extends PmTradeLogic<ReboundMartingaleState> {
  private static Logger logger = LoggerFactory.getLogger(ReboundMartingale.class);

  public ReboundMartingale(AutoTradeOrder autoTradeOrder, ReboundMartingaleState state, Supplier<LocalDateTime> currentDateTimeSupplier,
                           CandlestickManager candlestickManager) {
    super(autoTradeOrder, state, currentDateTimeSupplier, candlestickManager);
  }

  @Override
  public Trade.LongShort runLogic(TimeData timeData) {

    if (getTradeLogicState().getLowestPrice() == null) {
      getTradeLogicState().setLowestPrice(getCurrentPrice());
      getTradeLogicState().setHighestPrice(getCurrentPrice());
    }
    if (getAutoTradeOrder().getFirstTrade() == null) {
      openOrder();
    } else {
      if (getTradeLogicState().getLowestPrice().compareTo(getCurrentPrice()) > 0) {
        getTradeLogicState().setLowestPrice(getCurrentPrice());
      } else if (getTradeLogicState().getHighestPrice().compareTo(getCurrentPrice()) < 0) {
        getTradeLogicState().setHighestPrice(getCurrentPrice());
      }

      if (getCurrentPrice().compareTo(getAutoTradeOrder().getLastTrade().getPrice()) < 0 &&
        (getTradeLogicState().getPreviousTradedLowestPrice() == null
          || getTradeLogicState().getPreviousTradedLowestPrice().compareTo(getCurrentPrice()) > 0)) {
        BigDecimal reboundPerc = getReboundPerc(getCurrentPrice(), getTradeLogicState());
        logger.info("reboundPerc = {}", reboundPerc.toPlainString());
        monitorMartingale(reboundPerc);
      }
    }
    if (getTradeLogicState().getNoOfMartingale() < getTradeLogicState().getNoOfTradeCount()) {
      return Trade.LongShort.LONG;
    }
    return null;
  }

  private void monitorMartingale(BigDecimal reboundPerc) {
    if (getTradeLogicState().getNoOfTradeCount() < getTradeLogicState().getNoOfMartingale() + 1
      && reboundPerc.compareTo(getTradeLogicState().getReboundEnterPerc()) >= 0) {
      logger.info("Martingale entry reboundPerc={}, {}", reboundPerc, getTradeLogicState());
      openOrder();
    }

  }

  private void openOrder() {
    if (getAutoTradeOrder().getFirstTrade() == null) {
      openMarket(getTradeLogicState().getTradeQty(), Trade.LongShort.LONG);
    } else {
      BigDecimal tradeSizeOnMartingale = getTradeLogicState().isTradeSizeDoubleMartingale() ?
        getTradeLogicState().getTradeQty()
          .multiply((BigDecimal.valueOf(getTradeLogicState().getNoOfTradeCount()))
            .multiply(BigDecimal.valueOf(2)))
        : getTradeLogicState().getTradeQty();
      openMarket(tradeSizeOnMartingale, Trade.LongShort.LONG);
    }
  }

  @Override
  public void postOpenOrder(Trade trade) {
    super.postOpenOrder(trade);

    getTradeLogicState().setLowestPrice(getCurrentPrice());//is this 3 suitable in openOrder if trade fail/expired to open it will be skip
    getTradeLogicState().setHighestPrice(getCurrentPrice());//is this 3 suitable in openOrder
    getTradeLogicState().setPreviousTradedLowestPrice(getCurrentPrice());//is this 3 suitable in openOrder

    getTradeLogicState().setNoOfTradeCount(getTradeLogicState().getNoOfTradeCount() + 1);
    getAutoTradeOrder().setTotalCost(getAutoTradeOrder().getTotalCost().add(getCurrentPrice().multiply(
      trade.getTransactedQty() != null ? trade.getTransactedQty() : trade.getQty()))
      .setScale(2, RoundingMode.HALF_EVEN));//TODO review this

  }
//  private static void openTrade(AutoTradeOrder autoTradeOrder, ReboundMartingaleState reboundMartingaleState, BigDecimal currentPrice) {
//    Trade trade = onOpenTrade(autoTradeOrder, reboundMartingaleState, currentPrice);
//
//    autoTradeOrder.setLastTrade(trade);
//    autoTradeOrder.getTrades().add(trade);
//
//    autoTradeOrder.setPositionQty(autoTradeOrder.getPositionQty().add(trade.getQuantity()));
//
//    logger.info("Open Trade {} {}", autoTradeOrder, reboundMartingaleState);
//    //TODO tradingPlatform.openTrade();
//
//  }

  private static BigDecimal getReboundPerc(BigDecimal currentPrice, ReboundMartingaleState reboundMartingaleTo) {
    if (reboundMartingaleTo.getLowestPrice().compareTo(currentPrice) < 0) {
      BigDecimal diffPrice = currentPrice.subtract(reboundMartingaleTo.getLowestPrice());
      return diffPrice.movePointRight(2).divide(reboundMartingaleTo.getLowestPrice(), 2, RoundingMode.HALF_UP);
    }
    return BigDecimal.ZERO;
  }

  @Override
  public boolean isRun() {
    return AutoTradeOrder.Status.PM.equals(getAutoTradeOrder().getStatus());
  }
}
