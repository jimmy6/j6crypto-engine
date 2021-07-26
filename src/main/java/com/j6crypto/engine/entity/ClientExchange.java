package com.j6crypto.engine.entity;

import javax.persistence.*;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Entity
@Table(
  uniqueConstraints = {
    @UniqueConstraint(columnNames = {"client_id", "exchange"}, name = "uk_clientId_exchange")
  })
public class ClientExchange extends EntityBase {
  public enum Exchange {BINANCE, DUMMY}

  private Exchange exchange;
  private String apiKey;
  private String secretKey;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Client client;

  public ClientExchange() {
  }

  public Exchange getExchange() {
    return exchange;
  }

  public void setExchange(Exchange exchange) {
    this.exchange = exchange;
  }

  public Client getClient() {
    return client;
  }

  public void setClient(Client client) {
    this.client = client;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getSecretKey() {
    return secretKey;
  }

  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  @Override
  public String toString() {
    return "ExchangeApiKey{" +
      "id=" + id +
      ", apiKey='" + apiKey + '\'' +
      ", secretKey='" + secretKey + '\'' +
      ", id=" + id +
      ", createdDate=" + createdDate +
      ", updatedDate=" + updatedDate +
      '}';
  }
}
