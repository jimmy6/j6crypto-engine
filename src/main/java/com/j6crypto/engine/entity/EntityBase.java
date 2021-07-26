package com.j6crypto.engine.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.TemporalType.TIMESTAMP;
@MappedSuperclass
public class EntityBase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  protected Integer id;
  @JsonIgnore
  @CreationTimestamp
  protected LocalDateTime createdDate;
  @org.springframework.data.annotation.Version
  @JsonIgnore
  @UpdateTimestamp
  protected LocalDateTime updatedDate;

  public LocalDateTime getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(LocalDateTime createdDate) {
    this.createdDate = createdDate;
  }

  public LocalDateTime getUpdatedDate() {
    return updatedDate;
  }

  public void setUpdatedDate(LocalDateTime updatedDate) {
    this.updatedDate = updatedDate;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "EntityBase{" +
      "id=" + id +
      ", createdDate=" + createdDate +
      ", updatedDate=" + updatedDate +
      '}';
  }
}
