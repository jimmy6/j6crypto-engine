package com.j6crypto.logic.entity.state;

import com.j6crypto.engine.TradePlatform;
import com.j6crypto.engine.entity.ClientExchange;
import com.j6crypto.logic.PmTradeLogic;
import com.j6crypto.logic.StopTradeLogic;
import com.j6crypto.logic.TradeLogic;
import com.j6crypto.to.Trade;
import com.j6crypto.to.setup.AutoTradeOrderSetup;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Entity
@javax.persistence.Cacheable
@Table(indexes = {@Index(name = "idx_msid_status", columnList = "msId, status")})
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SelectBeforeUpdate(false)
@DynamicUpdate
public class AutoTradeOrder extends AutoTradeOrderSetup {
  @Column(nullable = false)
  private Integer clientId;
  @ManyToOne(cascade = CascadeType.PERSIST)
  private Trade lastTrade;
  @ManyToOne(cascade = CascadeType.PERSIST)
  private Trade firstTrade;
  @Column(precision = 10, scale = 2, nullable = false)
  private BigDecimal totalCost = BigDecimal.ZERO;
  @Column(precision = 10, scale = 2, nullable = false)
  private BigDecimal positionQty = BigDecimal.ZERO;
  @Column(nullable = false)
  private Integer msId;

  //  @OneToMany(fetch = FetchType.LAZY)
//  private List<Trade> trades = new ArrayList();
  @Transient
  protected List<TradeLogic> triggerLogics = new ArrayList();
  @Transient
  protected List<PmTradeLogic> positionMgmtLogics = new ArrayList();
  @Transient
  protected List<StopTradeLogic> stopLogics = new ArrayList();
  @Transient
  protected TradePlatform tradePlatform;

  public AutoTradeOrder() {
  }

  public TradePlatform getTradePlatform() {
    return tradePlatform;
  }

  public void setTradePlatform(TradePlatform tradePlatform) {
    this.tradePlatform = tradePlatform;
  }

  public AutoTradeOrder(Integer clientId, String symbol) {
    this.symbol = symbol;
    this.clientId = clientId;
  }

  public Integer getMsId() {
    return msId;
  }

  public void setMsId(Integer msId) {
    this.msId = msId;
  }

  public List<TradeLogic> getTriggerLogics() {
    return triggerLogics;
  }

  public void setTriggerLogics(List<TradeLogic> triggerLogics) {
    this.triggerLogics = triggerLogics;
  }

  public List<PmTradeLogic> getPositionMgmtLogics() {
    return positionMgmtLogics;
  }

  public void setPositionMgmtLogics(List<PmTradeLogic> positionMgmtLogics) {
    this.positionMgmtLogics = positionMgmtLogics;
  }

  public List<StopTradeLogic> getStopLogics() {
    return stopLogics;
  }

  public void setStopLogics(List<StopTradeLogic> stopLogics) {
    this.stopLogics = stopLogics;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Trade getLastTrade() {
    return lastTrade;
  }

  public void setLastTrade(Trade lastTrade) {
    this.lastTrade = lastTrade;
  }

  public Trade getFirstTrade() {
    return firstTrade;
  }

  public void setFirstTrade(Trade firstTrade) {
    this.firstTrade = firstTrade;
  }

//  public void setTrades(List<Trade> trades) {
//    this.trades = trades;
//  }

  public BigDecimal getPositionQty() {
    return positionQty;
  }

  public void setPositionQty(BigDecimal positionQty) {
    this.positionQty = positionQty;
  }

//  public List<Trade> getTrades() {
//    return trades;
//  }

  public BigDecimal getTotalCost() {
    return totalCost;
  }

  public void setTotalCost(BigDecimal totalCost) {
    this.totalCost = totalCost;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public Integer getClientId() {
    return clientId;
  }

  public void setClientId(Integer clientId) {
    this.clientId = clientId;
  }

  @Override
  public String toString() {
    return "AutoTradeOrder{" +
      "id=" + id +
      ", clientId=" + clientId +
      ", lastTrade=" + lastTrade +
      ", firstTrade=" + firstTrade +
      ", totalCost=" + totalCost +
      ", positionQty=" + positionQty +
//      ", trades=" + trades +
      ", symbol='" + symbol + '\'' +
      ", period=" + period +
      ", status=" + status +
      ", triggerLogics=" + triggerLogics +
      ", positionMgmtLogics=" + positionMgmtLogics +
      ", stopLogics=" + stopLogics +
      ", stopLogicOperator=" + stopLogicOperator +
      ", createdDate=" + createdDate +
      ", updatedDate=" + updatedDate +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AutoTradeOrder that = (AutoTradeOrder) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
