package com.j6crypto.to.setup;

import com.j6crypto.logic.entity.state.CommonState;
import com.j6crypto.logic.entity.state.State;
import com.j6crypto.service.CandlestickManager;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Entity(name = "BreakSupportResistanceTrigger")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BreakSupportResistanceTriggerSetup extends SetupBase implements State {
  private int breakPeriod;
  //support resistance range
  private BigDecimal priceRange = BigDecimal.valueOf(5);
  private BigDecimal firstBreakVol;
  private BigDecimal breakWithGapBelow = BigDecimal.valueOf(1);

  @Embedded
  private CommonState commonState = new CommonState();

  public BreakSupportResistanceTriggerSetup(int breakPeriod, BigDecimal priceRange,
                                            BigDecimal firstBreakVol, BigDecimal breakWithGapBelow) {
    this.breakPeriod = breakPeriod;
    this.priceRange = priceRange;
    this.firstBreakVol = firstBreakVol;
    this.breakWithGapBelow = breakWithGapBelow;
  }

  public BreakSupportResistanceTriggerSetup() {
  }

  public int getBreakPeriod() {
    return breakPeriod;
  }

  public void setBreakPeriod(int breakPeriod) {
    this.breakPeriod = breakPeriod;
  }

  public BigDecimal getPriceRange() {
    return priceRange;
  }

  public void setPriceRange(BigDecimal priceRange) {
    this.priceRange = priceRange;
  }

  public BigDecimal getFirstBreakVol() {
    return firstBreakVol;
  }

  public void setFirstBreakVol(BigDecimal firstBreakVol) {
    this.firstBreakVol = firstBreakVol;
  }

  public BigDecimal getBreakWithGapBelow() {
    return breakWithGapBelow;
  }

  public void setBreakWithGapBelow(BigDecimal breakWithGapBelow) {
    this.breakWithGapBelow = breakWithGapBelow;
  }

  @Override
  public CommonState getCommonState() {
    return commonState;
  }

  public void setCommonState(CommonState commonState) {
    this.commonState = commonState;
  }

}
