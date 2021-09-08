package com.j6crypto.web;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.persistence.Entity;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Component
@RequestScope
public class ClientContext {
  private Integer clientId;
  public ClientContext(int clientId) {
    this.clientId = clientId;
  }
  public ClientContext() {
  }

  public ClientContext(Integer clientId) {
    this.clientId = clientId;
  }

  public int getClientId() {
    return clientId;
  }

  public void setClientId(Integer clientId) {
    this.clientId = clientId;
  }
}
