package com.j6crypto.to.setup;

import com.j6crypto.logic.entity.state.CommonState;
import com.j6crypto.logic.entity.state.State;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@MappedSuperclass
public class ReboundMartingaleSetup extends SetupBase {

  protected int noOfMartingale;
  protected boolean tradeSizeDoubleMartingale;
  protected BigDecimal reboundEnterPerc;
  protected BigDecimal tradeQty;

  /**
   * Min Mandatory
   */
  public ReboundMartingaleSetup(int noOfMartingale, boolean tradeSizeDoubleMartingale, BigDecimal reboundEnterPerc, BigDecimal tradeQty) {
    this.noOfMartingale = noOfMartingale;
    this.tradeSizeDoubleMartingale = tradeSizeDoubleMartingale;
    this.reboundEnterPerc = reboundEnterPerc;
    this.tradeQty = tradeQty;
//    setLogicCode(getClass().getSimpleName().substring(0, getClass().getSimpleName().length() - 5));
  }

  public ReboundMartingaleSetup() {
  }


  public ReboundMartingaleSetup(ReboundMartingaleSetup martingaleSetup) {
    this.noOfMartingale = martingaleSetup.noOfMartingale;
    this.tradeSizeDoubleMartingale = martingaleSetup.tradeSizeDoubleMartingale;
    this.reboundEnterPerc = martingaleSetup.reboundEnterPerc;
    this.tradeQty = martingaleSetup.tradeQty;
  }

  public BigDecimal getTradeQty() {
    return tradeQty;
  }

  public void setTradeQty(BigDecimal tradeQty) {
    this.tradeQty = tradeQty;
  }

  public int getNoOfMartingale() {
    return noOfMartingale;
  }

  public void setNoOfMartingale(int noOfMartingale) {
    this.noOfMartingale = noOfMartingale;
  }

  public boolean isTradeSizeDoubleMartingale() {
    return tradeSizeDoubleMartingale;
  }

  public void setTradeSizeDoubleMartingale(boolean tradeSizeDoubleMartingale) {
    this.tradeSizeDoubleMartingale = tradeSizeDoubleMartingale;
  }

  public void setReboundEnterPerc(BigDecimal reboundEnterPerc) {
    this.reboundEnterPerc = reboundEnterPerc;
  }

  public BigDecimal getReboundEnterPerc() {
    return reboundEnterPerc;
  }

  @Override
  public String toString() {
    return "ReboundMartingaleSetup{" +
      "noOfMartingale=" + noOfMartingale +
      ", tradeSizeDoubleMartingale=" + tradeSizeDoubleMartingale +
      ", reboundEnterPerc=" + reboundEnterPerc +
      ", tradeQty=" + tradeQty +
      '}';
  }
}
