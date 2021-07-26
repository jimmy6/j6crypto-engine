package com.j6crypto.to.setup;

import com.j6crypto.logic.entity.state.CommonState;
import com.j6crypto.logic.entity.state.State;
import com.j6crypto.to.Trade;
import com.j6crypto.to.setup.SetupBase;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Entity(name = "DummyTrigger")
@javax.persistence.Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE) //Provide cache strategy.
public class DummyTriggerSetup extends SetupBase implements State {
  private Trade.LongShort runLogic;

  @Embedded
  private CommonState commonState = new CommonState();

  public DummyTriggerSetup(Trade.LongShort runLogic) {
    this.runLogic = runLogic;
  }

  @Override
  public CommonState getCommonState() {
    return commonState;
  }

  public void setCommonState(CommonState commonState) {
    this.commonState = commonState;
  }

  public Trade.LongShort getRunLogic() {
    return runLogic;
  }

  public void setRunLogic(Trade.LongShort runLogic) {
    this.runLogic = runLogic;
  }

  @Override
  public String toString() {
    return "DummyTradeLogicSetup{" +
      "runLogic=" + runLogic +
      '}';
  }
}
