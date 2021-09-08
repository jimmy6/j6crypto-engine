package com.j6crypto.logic.positionmgmt;

import com.j6crypto.exception.TradeException;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.logic.entity.state.MartingaleDoublePmState;
import com.j6crypto.to.TimeData;
import com.j6crypto.to.Trade;

import javax.persistence.EntityManager;
import java.math.BigDecimal;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class MartingaleDoublePm extends Pm<MartingaleDoublePmState> {

  public MartingaleDoublePm(AutoTradeOrder autoTradeOrder, EntityManager em) {
    super(autoTradeOrder, em);
  }

  @Override
  public boolean openOrder(TimeData timeData) throws TradeException {
    MartingaleDoublePmState martingaleDoublePmState = getPmState();
    if (ato.getFirstTrade() == null) {
      openMarket(martingaleDoublePmState.getTradeQty(), ato.getLongShort(), timeData);
    } else {
      BigDecimal tradeSizeOnMartingale = martingaleDoublePmState.isTradeSizeDoubleMartingale() ?
        martingaleDoublePmState.getTradeQty()
          .multiply((BigDecimal.valueOf(martingaleDoublePmState.getNoOfTradeCount()))
            .multiply(BigDecimal.valueOf(2)))
        : martingaleDoublePmState.getTradeQty();
      openMarket(tradeSizeOnMartingale, ato.getLongShort(), timeData);
    }

    if (getPmState().getNoOfTradeCount() < getPmState().getNoOfMartingale() + 1) {
      return true;
    }
    return false;
  }

  @Override
  public void postOpenOrder(Trade trade) {
    super.postOpenOrder(trade);
    getPmState().setNoOfTradeCount(getPmState().getNoOfTradeCount() + 1);
  }
}
