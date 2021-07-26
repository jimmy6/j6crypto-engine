package com.j6crypto.to.setup;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.j6crypto.engine.entity.EntityBase;
import com.j6crypto.logic.entity.state.PriceReduceFromHighestState;
import com.j6crypto.logic.entity.state.ProfitReduceFromHighestState;
import com.j6crypto.logic.entity.state.ReboundMartingaleState;
import com.j6crypto.logic.entity.state.State;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@MappedSuperclass
public class AutoTradeOrderSetup extends EntityBase {
  protected String symbol;

  @Column(nullable = false)
  private Integer clientExchangeId;

  public enum Period {MIN1, DAY}

  protected Period period = Period.MIN1;

  public enum ProductType {SPOT, FUTURE, MARGIN}

  protected ProductType productType = ProductType.SPOT;

  public enum Status {TRIGGER, PM, STOP, FINISH, TERMINATED, ERR}

  protected Status status = Status.TRIGGER;

  public enum LogicOperator {OR, AND,}

  protected LogicOperator stopLogicOperator = LogicOperator.AND;
  //https://www.baeldung.com/jackson-annotations
//  @JsonDeserialize(using = StateDeserializer.class)
  @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  @OneToMany(targetEntity = SetupBase.class, cascade = CascadeType.ALL)
  @OrderBy("id")
  @JsonTypeInfo(use = NAME, include = EXISTING_PROPERTY, property = "logicCode", visible = true)
  @JsonSubTypes({@JsonSubTypes.Type(value = DummyTriggerSetup.class, name = "DummyTrigger")})
  protected List<State> triggerStates = new ArrayList<>();

  @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  @OneToMany(targetEntity = SetupBase.class, cascade = CascadeType.ALL)
  @OrderBy("id")
  @JsonTypeInfo(use = NAME, include = EXISTING_PROPERTY, property = "logicCode", visible = true)
  @JsonSubTypes({
    @JsonSubTypes.Type(value = ReboundMartingaleState.class, name = "ReboundMartingale"),
    @JsonSubTypes.Type(value = OpenMarketPriceSetup.class, name = "OpenMarketPrice")})
  protected List<State> pmStates = new ArrayList<>();

  @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  @OneToMany(targetEntity = SetupBase.class, cascade = CascadeType.ALL)
  @OrderBy("id")
  @JsonTypeInfo(use = NAME, include = EXISTING_PROPERTY, property = "logicCode", visible = true)
  @JsonSubTypes({
    @JsonSubTypes.Type(value = PriceReduceFromHighestState.class, name = "PriceReduceFromHighest"),
    @JsonSubTypes.Type(value = ProfitReduceFromHighestState.class, name = "ProfitReduceFromHighest"),
    @JsonSubTypes.Type(value = ProfitPercentageTpSetup.class, name = "ProfitPercentageTp")})
  protected List<State> stopStates = new ArrayList<>();

  public AutoTradeOrderSetup() {
  }

  public ProductType getProductType() {
    return productType;
  }

  public void setProductType(ProductType productType) {
    this.productType = productType;
  }

  public Integer getClientExchangeId() {
    return clientExchangeId;
  }

  public void setClientExchangeId(Integer clientExchangeId) {
    this.clientExchangeId = clientExchangeId;
  }

  public List<State> getTriggerStates() {
    return triggerStates;
  }

  public void setTriggerStates(List<State> triggerStates) {
    this.triggerStates = triggerStates;
  }

  public List<State> getStopStates() {
    return stopStates;
  }

  public void setStopStates(List<State> stopStates) {
    this.stopStates = stopStates;
  }

  public List<State> getPmStates() {
    return pmStates;
  }

  public void setPmStates(List<State> pmStates) {
    this.pmStates = pmStates;
  }

  public AutoTradeOrderSetup(LogicOperator stopLogicOperator) {
    this.stopLogicOperator = stopLogicOperator;
  }

  public LogicOperator getStopLogicOperator() {
    return stopLogicOperator;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public Period getPeriod() {
    return period;
  }

  public void setPeriod(Period period) {
    this.period = period;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }


  public void setStopLogicOperator(LogicOperator stopLogicOperator) {
    this.stopLogicOperator = stopLogicOperator;
  }

  @Override
  public String toString() {
    return "AutoTradeOrderSetup{" +
      "symbol='" + symbol + '\'' +
      ", period=" + period +
      ", status=" + status +
      ", stopLogicOperator=" + stopLogicOperator +
      ", triggerStates=" + triggerStates +
      ", pmStates=" + pmStates +
      ", stopStates=" + stopStates +
      ", id=" + id +
      ", createdDate=" + createdDate +
      ", updatedDate=" + updatedDate +
      '}';
  }
}
