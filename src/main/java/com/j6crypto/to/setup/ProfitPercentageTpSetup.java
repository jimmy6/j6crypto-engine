package com.j6crypto.to.setup;

import com.j6crypto.engine.CryptoEngine;
import com.j6crypto.engine.EngineConstant;
import com.j6crypto.logic.entity.state.CommonState;
import com.j6crypto.logic.entity.state.State;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Entity(name = "ProfitPercentageTp")
@javax.persistence.Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE) //Provide cache strategy.
public class ProfitPercentageTpSetup extends SetupBase implements State {

  private BigDecimal profitPercentageTp;
  @Embedded
  private CommonState commonState = new CommonState();

  public ProfitPercentageTpSetup(BigDecimal profitPercentageTp) {
    this.profitPercentageTp = profitPercentageTp;
    setLogicCode(EngineConstant.StopLogicCodes.ProfitPercentageTp.name());
  }

  public BigDecimal getProfitPercentageTp() {
    return profitPercentageTp;
  }

  public ProfitPercentageTpSetup() {
    setLogicCode(EngineConstant.StopLogicCodes.ProfitPercentageTp.name());
  }

  public void setCommonState(CommonState commonState) {
    this.commonState = commonState;
  }

  public void setProfitPercentageTp(BigDecimal profitPercentageTp) {
    this.profitPercentageTp = profitPercentageTp;
  }

  @Override
  public String toString() {
    return "ProfitPercentageTpSetup{" +
      "id=" + id +
      ", profitPercentageTp=" + profitPercentageTp +
      ", commonState=" + commonState +
      ", createdDate=" + createdDate +
      ", updatedDate=" + updatedDate +
      '}';
  }

  @Override
  public CommonState getCommonState() {
    return commonState;
  }
}
