package com.j6crypto.to.setup;

import com.j6crypto.engine.EngineConstant;

import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@MappedSuperclass
public class ReboundSetup extends SetupBase {
  protected BigDecimal reboundEnterPerc;

  public ReboundSetup(BigDecimal reboundEnterPerc) {
    setLogicCode(EngineConstant.PmLogicCodes.Rebound.name());
    this.reboundEnterPerc = reboundEnterPerc;
  }

  public ReboundSetup() {
    setLogicCode(EngineConstant.PmLogicCodes.Rebound.name());
  }

  public ReboundSetup(ReboundSetup martingaleSetup) {
    this.reboundEnterPerc = martingaleSetup.reboundEnterPerc;
  }

  public void setReboundEnterPerc(BigDecimal reboundEnterPerc) {
    this.reboundEnterPerc = reboundEnterPerc;
  }

  public BigDecimal getReboundEnterPerc() {
    return reboundEnterPerc;
  }

  @Override
  public String toString() {
    return "ReboundSetup{" +
      ", reboundEnterPerc=" + reboundEnterPerc +
      '}';
  }
}
