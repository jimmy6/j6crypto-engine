package com.j6crypto.controller.to;

import com.j6crypto.engine.entity.ClientExchange;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class ClientExchangeTo {
  private Integer id;
  private ClientExchange.Exchange exchange;
  private String apiKey;
  private String secretKey;

  public ClientExchangeTo() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public ClientExchange.Exchange getExchange() {
    return exchange;
  }

  public void setExchange(ClientExchange.Exchange exchange) {
    this.exchange = exchange;
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

}
