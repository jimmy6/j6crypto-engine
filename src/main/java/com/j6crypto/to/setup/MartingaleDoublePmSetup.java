package com.j6crypto.to.setup;

import com.j6crypto.engine.EngineConstant;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@MappedSuperclass
public class MartingaleDoublePmSetup extends SetupBase {
  protected int noOfMartingale;
  protected boolean tradeSizeDoubleMartingale;
  protected BigDecimal tradeQty;

  public MartingaleDoublePmSetup(int noOfMartingale, boolean tradeSizeDoubleMartingale, BigDecimal tradeQty) {
    this.noOfMartingale = noOfMartingale;
    this.tradeSizeDoubleMartingale = tradeSizeDoubleMartingale;
    this.tradeQty = tradeQty;
    setLogicCode(EngineConstant.PmCodes.MartingaleDoublePm.name());
  }

  public MartingaleDoublePmSetup() {
    setLogicCode(EngineConstant.PmCodes.MartingaleDoublePm.name());
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

  @Override
  public String toString() {
    return "MartingaleDoublePmSetup{" +
      "noOfMartingale=" + noOfMartingale +
      ", tradeSizeDoubleMartingale=" + tradeSizeDoubleMartingale +
      '}';
  }
}
