package com.j6crypto.to.setup;

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
  }

  public ProfitReduceFromHighestSetup(ProfitReduceFromHighestSetup profitReduceFromHighestSetup) {
    this.tpOnProfitReducePerc = profitReduceFromHighestSetup.getTpOnProfitReducePerc();
  }
  public ProfitReduceFromHighestSetup(BigDecimal tpOnProfitReducePerc) {
    this.tpOnProfitReducePerc = tpOnProfitReducePerc;
  }
  public BigDecimal getTpOnProfitReducePerc() {
    return tpOnProfitReducePerc;
  }

  public void setTpOnProfitReducePerc(BigDecimal tpOnProfitReducePerc) {
    this.tpOnProfitReducePerc = tpOnProfitReducePerc;
  }

}
