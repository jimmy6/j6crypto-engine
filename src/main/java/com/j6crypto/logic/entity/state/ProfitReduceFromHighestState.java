package com.j6crypto.logic.entity.state;

import com.j6crypto.engine.CryptoEngine;
import com.j6crypto.engine.EngineConstant;
import com.j6crypto.to.setup.PriceReduceFromHighestSetup;
import com.j6crypto.to.setup.ProfitReduceFromHighestSetup;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Entity(name = "ProfitReduceFromHighest")
@javax.persistence.Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE) //Provide cache strategy.
public class ProfitReduceFromHighestState extends ProfitReduceFromHighestSetup {

  private BigDecimal highestProfit = BigDecimal.ZERO;


  public ProfitReduceFromHighestState(){
    setLogicCode(EngineConstant.StopLogicCodes.ProfitReduceFromHighest.name());
  }
  public ProfitReduceFromHighestState(ProfitReduceFromHighestSetup profitReduceFromHighestSetup) {
   super(profitReduceFromHighestSetup);
    setLogicCode(EngineConstant.StopLogicCodes.ProfitReduceFromHighest.name());
  }

  public BigDecimal getHighestProfit() {
    return highestProfit;
  }

  public void setHighestProfit(BigDecimal highestProfit) {
    this.highestProfit = highestProfit;
  }

  @Override
  public String toString() {
    return "ProfitReduceFromHighestState{" +
      "id=" + id +
      ", highestProfit=" + highestProfit +
      ", tpOnProfitReducePerc=" + tpOnProfitReducePerc +
      ", createdDate=" + createdDate +
      ", updatedDate=" + updatedDate +
      '}';
  }
}
