package com.j6crypto.engine.entity;

import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Objects;

import static javax.persistence.EnumType.STRING;

public class MasterDataKey implements Serializable {
  private String id;

  public enum Category {
    COIN
  }

  public enum IdCoin {
    BNBBUSD, BTCUSDT, DUMMY
  }

  public MasterDataKey() {
  }

  public MasterDataKey(Category category, String id) {
    this.category = category;
    this.id = id;
  }

  @Enumerated(STRING)
  private Category category;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MasterDataKey that = (MasterDataKey) o;
    return Objects.equals(id, that.id) && category == that.category;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, category);
  }
}
