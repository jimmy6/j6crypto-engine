package com.j6crypto.service;

import org.springframework.stereotype.Service;

import javax.persistence.Entity;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Service
public class SecurityService {
  private Integer clientId;
  public int getClientId() {
    return clientId;//TODO for testing only
  }

  public void setClientId(Integer clientId) {
    this.clientId = clientId;
  }
}
