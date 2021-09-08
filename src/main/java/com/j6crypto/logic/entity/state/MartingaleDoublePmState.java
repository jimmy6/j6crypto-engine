package com.j6crypto.logic.entity.state;

import com.j6crypto.to.setup.MartingaleDoublePmSetup;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Entity(name = "MartingaleDoublePm")
@javax.persistence.Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE) //Provide cache strategy.
public class MartingaleDoublePmState extends MartingaleDoublePmSetup {
  private int noOfTradeCount = 0;

  public int getNoOfTradeCount() {
    return noOfTradeCount;
  }

  public void setNoOfTradeCount(int noOfTradeCount) {
    this.noOfTradeCount = noOfTradeCount;
  }
}
