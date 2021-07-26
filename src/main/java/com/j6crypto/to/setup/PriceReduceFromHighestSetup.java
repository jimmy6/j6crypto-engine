package com.j6crypto.to.setup;

import com.j6crypto.logic.entity.state.CommonState;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;
/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@MappedSuperclass
public class PriceReduceFromHighestSetup extends SetupBase {
  protected BigDecimal tpOnPriceReducePerc;

  public PriceReduceFromHighestSetup() {
  }

  public PriceReduceFromHighestSetup(BigDecimal tpOnPriceReducePerc) {
    this.tpOnPriceReducePerc = tpOnPriceReducePerc;
  }

  public BigDecimal getTpOnPriceReducePerc() {
    return tpOnPriceReducePerc;
  }

  public void setTpOnPriceReducePerc(BigDecimal tpOnPriceReducePerc) {
    this.tpOnPriceReducePerc = tpOnPriceReducePerc;
  }
}
