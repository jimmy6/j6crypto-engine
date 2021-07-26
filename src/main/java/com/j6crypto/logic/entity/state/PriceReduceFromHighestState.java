package com.j6crypto.logic.entity.state;

import com.j6crypto.engine.CryptoEngine;
import com.j6crypto.engine.EngineConstant;
import com.j6crypto.to.setup.PriceReduceFromHighestSetup;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Entity(name = "PriceReduceFromHighest")
@javax.persistence.Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE) //Provide cache strategy.
public class PriceReduceFromHighestState extends PriceReduceFromHighestSetup implements State {

  private BigDecimal highestPrice;
  @Embedded
  private CommonState commonState = new CommonState();

  public PriceReduceFromHighestState() {
    setLogicCode(EngineConstant.StopLogicCodes.PriceReduceFromHighest.name());
  }

  public PriceReduceFromHighestState(PriceReduceFromHighestSetup priceReduceFromHighestSetup) {
    setTpOnPriceReducePerc(priceReduceFromHighestSetup.getTpOnPriceReducePerc());
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getId() {
    return id;
  }

  public void setCommonState(CommonState commonState) {
    this.commonState = commonState;
  }

  public CommonState getCommonState() {
    return commonState;
  }

  public BigDecimal getHighestPrice() {
    return highestPrice;
  }

  public void setHighestPrice(BigDecimal highestPrice) {
    this.highestPrice = highestPrice;
  }

  @Override
  public String toString() {
    return "PriceReduceFromHighestState{" +
      "id=" + id +
      ", highestPrice=" + highestPrice +
      ", commonState=" + commonState +
      ", tpOnPriceReducePerc=" + tpOnPriceReducePerc +
      ", createdDate=" + createdDate +
      ", updatedDate=" + updatedDate +
      '}';
  }
}
