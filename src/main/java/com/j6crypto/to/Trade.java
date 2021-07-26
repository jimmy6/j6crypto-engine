package com.j6crypto.to;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j6crypto.engine.entity.EntityBase;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Entity
@SelectBeforeUpdate(false)
public class Trade extends EntityBase {

  @Column(precision = 28, scale = 18, nullable = false)
  private BigDecimal price;
  @Column(precision = 10, scale = 4, nullable = false)
  private BigDecimal qty;
  @Column(precision = 28, scale = 18)
  private BigDecimal transactedPrice;
  @Column(precision = 10, scale = 4)
  private BigDecimal transactedQty;
  @Column(nullable = false)
  private LongShort longShort;
  @JsonIgnore
  @ManyToOne(optional = false)
  private AutoTradeOrder autoTradeOrder;

  public enum OrderType {
    LIMIT, MARKET, STOP_LOSS, STOP_LOSS_LIMIT, TAKE_PROFIT, TAKE_PROFIT_LIMIT, LIMIT_MAKER
  }
  @Column(nullable = false)
  private OrderType orderType;

  /**
   * For stoplogic, Long is used for positive
   */
  public enum LongShort {
    SHORT, LONG
  }

  public AutoTradeOrder getAutoTradeOrder() {
    return autoTradeOrder;
  }

  public void setAutoTradeOrder(AutoTradeOrder autoTradeOrder) {
    this.autoTradeOrder = autoTradeOrder;
  }

  public BigDecimal getQty() {
    return qty;
  }

  public void setQty(BigDecimal qty) {
    this.qty = qty;
  }

  public BigDecimal getTransactedPrice() {
    return transactedPrice;
  }

  public void setTransactedPrice(BigDecimal transactedPrice) {
    this.transactedPrice = transactedPrice;
  }

  public BigDecimal getTransactedQty() {
    return transactedQty;
  }

  public void setTransactedQty(BigDecimal transactedQty) {
    this.transactedQty = transactedQty;
  }

  public OrderType getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderType orderType) {
    this.orderType = orderType;
  }


  public Trade() {
  }

  public Trade(AutoTradeOrder autoTradeOrder, BigDecimal price, BigDecimal qty, LongShort longShort, OrderType orderType) {
    this.autoTradeOrder = autoTradeOrder;
    this.price = price;
    this.qty = qty;
    this.longShort = longShort;
    this.orderType = orderType;
  }

  public LongShort getLongShort() {
    return longShort;
  }

  public void setLongShort(LongShort longShort) {
    this.longShort = longShort;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  @Override
  public String toString() {
    return "Trade{" +
      "price=" + price +
      ", qty=" + qty +
      ", transactedPrice=" + transactedPrice +
      ", transactedQty=" + transactedQty +
      ", longShort=" + longShort +
      ", orderType=" + orderType +
      '}';
  }
}
