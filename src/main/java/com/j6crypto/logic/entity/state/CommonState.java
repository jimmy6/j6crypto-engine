package com.j6crypto.logic.entity.state;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.j6crypto.to.Trade;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Embeddable
public class CommonState {
//  @JsonIgnore
//  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//  @JsonSerialize(using = LocalDateTimeSerializer.class)
//  private LocalDateTime cacheSignalExpiredAt;
//
//  @JsonIgnore
//  @ManyToOne(optional = false, fetch = FetchType.LAZY)
//  private AutoTradeOrder autoTradeOrder;
//
//  @JsonIgnore
//  private BigDecimal value;

  public CommonState() {
  }

//  public BigDecimal getValue() {
//    return value;
//  }
//
//  public void setValue(BigDecimal value) {
//    this.value = value;
//  }
//
//  public CommonState(LocalDateTime cacheSignalExpiredAt) {
//    this.cacheSignalExpiredAt = cacheSignalExpiredAt;
//  }
//
//  public LocalDateTime getCacheSignalExpiredAt() {
//    return cacheSignalExpiredAt;
//  }
//
//  public AutoTradeOrder getAutoTradeOrder() {
//    return autoTradeOrder;
//  }
//
//  public void setAutoTradeOrder(AutoTradeOrder autoTradeOrder) {
//    this.autoTradeOrder = autoTradeOrder;
//  }
//
//  public void setCacheSignalExpiredAt(LocalDateTime cacheSignalExpiredAt) {
//    this.cacheSignalExpiredAt = cacheSignalExpiredAt;
//  }
//
//  @Override
//  public String toString() {
//    return "CommonState{" +
//      "cacheSignalExpiredAt=" + cacheSignalExpiredAt +
//      ", autoTradeOrder=" + autoTradeOrder +
//      ", value=" + value +
//      '}';
//  }
}
