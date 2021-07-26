package com.j6crypto.logic.entity.state;

import com.j6crypto.engine.CryptoEngine;
import com.j6crypto.engine.EngineConstant;
import com.j6crypto.to.setup.ReboundMartingaleSetup;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.math.BigDecimal;
/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@javax.persistence.Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity(name = "ReboundMartingale")
public class ReboundMartingaleState extends ReboundMartingaleSetup implements State{
  private BigDecimal highestPrice;//TODO this should be universal same
  private BigDecimal lowestPrice;// TODO what if restore after hour. so need to manually find again???
  private BigDecimal previousTradedLowestPrice;
  private int noOfTradeCount = 0;
  @Embedded
  private CommonState commonState = new CommonState();
  public ReboundMartingaleState(){
    setLogicCode(EngineConstant.PmLogicCodes.ReboundMartingale.name());
  }
  public ReboundMartingaleState(ReboundMartingaleSetup martingaleSetup) {
    super(martingaleSetup);
    setLogicCode(EngineConstant.PmLogicCodes.ReboundMartingale.name());
  }

  public void setPreviousTradedLowestPrice(BigDecimal previousTradedLowestPrice) {
    this.previousTradedLowestPrice = previousTradedLowestPrice;
  }

  public BigDecimal getPreviousTradedLowestPrice() {
    return previousTradedLowestPrice;
  }

  public void setHighestPrice(BigDecimal highestPrice) {
    this.highestPrice = highestPrice;
  }

  public BigDecimal getHighestPrice() {
    return highestPrice;
  }

  public int getNoOfTradeCount() {
    return noOfTradeCount;
  }

  public void setNoOfTradeCount(int noOfTradeCount) {
    this.noOfTradeCount = noOfTradeCount;
  }

  public BigDecimal getLowestPrice() {
    return lowestPrice;
  }

  public void setLowestPrice(BigDecimal lowestPrice) {
    this.lowestPrice = lowestPrice;
  }

  @Override
  public String toString() {
    return "ReboundMartingaleState{" +
      "highestPrice=" + highestPrice +
      ", lowestPrice=" + lowestPrice +
      ", previousTradedLowestPrice=" + previousTradedLowestPrice +
      ", noOfTradeCount=" + noOfTradeCount +
      ", commonState=" + commonState +
      ", noOfMartingale=" + noOfMartingale +
      ", tradeSizeDoubleMartingale=" + tradeSizeDoubleMartingale +
      ", reboundEnterPerc=" + reboundEnterPerc +
      ", tradeQty=" + tradeQty +
      ", commonState=" + commonState +
      ", id=" + id +
      ", createdDate=" + createdDate +
      ", updatedDate=" + updatedDate +
      '}';
  }

  @Override
  public CommonState getCommonState() {
    return commonState;
  }
}
