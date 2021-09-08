package com.j6crypto.to;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class TimeData implements Serializable {
  private BigDecimal last;
  @JsonProperty
  private String code;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime dateTime;
  private BigDecimal vol;
  public TimeData() {
  }

  public TimeData(String code, BigDecimal last, LocalDateTime dateTime) {
    this.last = last;
    this.code = code;
    this.dateTime = dateTime;
  }
  public TimeData(String code, BigDecimal last, LocalDateTime dateTime, BigDecimal vol) {
    this.last = last;
    this.code = code;
    this.dateTime = dateTime;
    this.vol = vol;
  }

  public BigDecimal getVol() {
    return vol;
  }

  public void setVol(BigDecimal vol) {
    this.vol = vol;
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public void setDateTime(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  public BigDecimal getLast() {
    return last;
  }

  public void setLast(BigDecimal last) {
    this.last = last;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TimeData timeData = (TimeData) o;
    return Objects.equals(code, timeData.code) &&
      Objects.equals(dateTime, timeData.dateTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, dateTime);
  }

  @Override
  public String toString() {
    return "TimeData{" +
      "last=" + last +
      ", code='" + code + '\'' +
      ", dateTime=" + dateTime +
      '}';
  }
}
