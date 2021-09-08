package com.j6crypto.logic.entity.state;

import com.j6crypto.engine.EngineConstant;
import com.j6crypto.to.setup.ReboundMartingaleSetup;
import com.j6crypto.to.setup.ReboundSetup;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity(name = "Rebound")
public class ReboundState extends ReboundSetup {
  private BigDecimal highestPrice;//TODO this should be universal same
  private BigDecimal lowestPrice;// TODO what if restore after hour. so need to manually find again???
  private BigDecimal previousTradedLowestPrice;

  public ReboundState() {
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

  public BigDecimal getLowestPrice() {
    return lowestPrice;
  }

  public void setLowestPrice(BigDecimal lowestPrice) {
    this.lowestPrice = lowestPrice;
  }

  @Override
  public String toString() {
    return "ReboundState{" +
      "highestPrice=" + highestPrice +
      ", lowestPrice=" + lowestPrice +
      ", previousTradedLowestPrice=" + previousTradedLowestPrice +
      ", reboundEnterPerc=" + reboundEnterPerc +
      ", id=" + id +
      ", createdDate=" + createdDate +
      ", updatedDate=" + updatedDate +
      '}';
  }

}
