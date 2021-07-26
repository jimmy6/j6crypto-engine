package com.j6crypto.engine.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class MasterData {

  @EmbeddedId
  private MasterDataKey masterDataKey;

  private String value;

  private Integer sequence;

  private Boolean active;

  public MasterData() {
  }

  public MasterData(MasterDataKey masterDataKey, String value, Integer sequence, Boolean active) {
    this.masterDataKey = masterDataKey;
    this.value = value;
    this.sequence = sequence;
    this.active = active;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public MasterDataKey getMasterDataKey() {
    return masterDataKey;
  }

  public void setMasterDataKey(MasterDataKey masterDataKey) {
    this.masterDataKey = masterDataKey;
  }

  public void setSequence(Integer sequence) {
    this.sequence = sequence;
  }

  public Integer getSequence() {
    return sequence;
  }
}
