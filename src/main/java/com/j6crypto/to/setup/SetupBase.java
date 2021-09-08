package com.j6crypto.to.setup;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.j6crypto.engine.entity.EntityBase;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Entity
@javax.persistence.Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "logic_code", discriminatorType = DiscriminatorType.STRING)
public class SetupBase extends EntityBase {
  @Transient // keep this transient because duplicate column with DiscriminatorColumn. But this need to for json api
  private String logicCode;
  private int cacheSignalForPeriod = 0;
  private String valueFrom;
  @JsonIgnore
  private LocalDateTime cacheSignalExpiredAt;

  @JsonIgnore
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private AutoTradeOrder autoTradeOrder;

  @JsonIgnore
  private BigDecimal value;

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public LocalDateTime getCacheSignalExpiredAt() {
    return cacheSignalExpiredAt;
  }

  public AutoTradeOrder getAutoTradeOrder() {
    return autoTradeOrder;
  }

  public void setAutoTradeOrder(AutoTradeOrder autoTradeOrder) {
    this.autoTradeOrder = autoTradeOrder;
  }

  public void setCacheSignalExpiredAt(LocalDateTime cacheSignalExpiredAt) {
    this.cacheSignalExpiredAt = cacheSignalExpiredAt;
  }

  public String getValueFrom() {
    return valueFrom;
  }

  public void setValueFrom(String valueFrom) {
    this.valueFrom = valueFrom;
  }

  public String getLogicCode() {
    return logicCode;
  }

  public void setLogicCode(String logicCode) {
    this.logicCode = logicCode;
  }

  public int getCacheSignalForPeriod() {
    return cacheSignalForPeriod;
  }

  public void setCacheSignalForPeriod(int cacheSignalForPeriod) {
    this.cacheSignalForPeriod = cacheSignalForPeriod;
  }

}
