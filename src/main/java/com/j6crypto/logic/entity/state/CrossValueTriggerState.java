package com.j6crypto.logic.entity.state;

import com.j6crypto.engine.EngineConstant;
import com.j6crypto.to.setup.CrossValueTriggerSetup;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Embedded;
import javax.persistence.Entity;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Entity(name = "CrossValueTrigger")
@javax.persistence.Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE) //Provide cache strategy.
public class CrossValueTriggerState extends CrossValueTriggerSetup {
  private int currentCrossNo;

  public CrossValueTriggerState() {
  }

  public int getCurrentCrossNo() {
    return currentCrossNo;
  }

  public void setCurrentCrossNo(int currentCrossNo) {
    this.currentCrossNo = currentCrossNo;
  }
}