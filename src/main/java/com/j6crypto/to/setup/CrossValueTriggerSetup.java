package com.j6crypto.to.setup;

import com.j6crypto.engine.EngineConstant;
import com.j6crypto.logic.entity.state.CommonState;
import com.j6crypto.logic.entity.state.State;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@MappedSuperclass
public class CrossValueTriggerSetup extends SetupBase {

  private int crossNo;

  public CrossValueTriggerSetup(int crossNo) {
    setLogicCode(EngineConstant.TriggerLogicCodes.CrossValueTrigger.name());
    this.crossNo = crossNo;
  }

  public CrossValueTriggerSetup() {
    setLogicCode(EngineConstant.TriggerLogicCodes.CrossValueTrigger.name());
  }

  public int getCrossNo() {
    return crossNo;
  }

  public void setCrossNo(int crossNo) {
    this.crossNo = crossNo;
  }

  @Override
  public String toString() {
    return "CrossValueTriggerSetup{" +
      "crossNo=" + crossNo +
      '}';
  }
}
