package com.j6crypto.to.setup;

import com.j6crypto.engine.EngineConstant;
import com.j6crypto.logic.entity.state.CommonState;
import com.j6crypto.logic.entity.state.State;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@MappedSuperclass
public class ProfitReduceFromHighestSetup extends SetupBase {
  protected BigDecimal tpOnProfitReducePerc;

  public ProfitReduceFromHighestSetup() {
    setLogicCode(EngineConstant.StopLogicCodes.ProfitReduceFromHighest.name());
  }

  public ProfitReduceFromHighestSetup(ProfitReduceFromHighestSetup profitReduceFromHighestSetup) {
    setLogicCode(EngineConstant.StopLogicCodes.ProfitReduceFromHighest.name());
    this.tpOnProfitReducePerc = profitReduceFromHighestSetup.getTpOnProfitReducePerc();
  }
  public ProfitReduceFromHighestSetup(BigDecimal tpOnProfitReducePerc) {
    setLogicCode(EngineConstant.StopLogicCodes.ProfitReduceFromHighest.name());
    this.tpOnProfitReducePerc = tpOnProfitReducePerc;
  }
  public BigDecimal getTpOnProfitReducePerc() {
    return tpOnProfitReducePerc;
  }

  public void setTpOnProfitReducePerc(BigDecimal tpOnProfitReducePerc) {
    this.tpOnProfitReducePerc = tpOnProfitReducePerc;
  }

}
