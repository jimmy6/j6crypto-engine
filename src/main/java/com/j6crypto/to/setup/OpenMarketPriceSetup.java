package com.j6crypto.to.setup;

import com.j6crypto.engine.entity.EntityBase;
import com.j6crypto.logic.entity.state.CommonState;
import com.j6crypto.logic.entity.state.State;
import com.j6crypto.to.Trade;
import com.j6crypto.to.Trade.LongShort;
import com.j6crypto.to.setup.SetupBase;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Entity(name = "OpenMarketPrice")
@javax.persistence.Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class OpenMarketPriceSetup extends SetupBase implements State {

  private BigDecimal qty;
  private LongShort longShort = LongShort.LONG;
  @Embedded
  private CommonState commonState = new CommonState();

  @Override
  public CommonState getCommonState() {
    return commonState;
  }

  public void setCommonState(CommonState commonState) {
    this.commonState = commonState;
  }


  /**
   * Min Mandatory
   *
   * @param qty
   */
  public OpenMarketPriceSetup(BigDecimal qty) {
    this.qty = qty;
  }

  public OpenMarketPriceSetup() {
  }

  public LongShort getLongShort() {
    return longShort;
  }

  public void setLongShort(LongShort longShort) {
    this.longShort = longShort;
  }

  public BigDecimal getQty() {
    return qty;
  }

  public void setQty(BigDecimal qty) {
    this.qty = qty;
  }
}
