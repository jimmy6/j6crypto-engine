package com.j6crypto.to;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class LoginRes {
  private String authToken;

  public LoginRes() {
  }

  public LoginRes(String authToken) {
    this.authToken = authToken;
  }

  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }
}
