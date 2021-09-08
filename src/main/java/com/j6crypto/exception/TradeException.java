package com.j6crypto.exception;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class TradeException extends Exception {
  /**
   * Null = no retry. Thus, set ato.status to ERR
   */
  private RetryMode retryMode;

  public enum RetryMode {
    NO_LIMIT
// ,   LIMIT
  }

  public TradeException(RetryMode retryMode) {
    this.retryMode = retryMode;
  }

  public RetryMode getRetryMode() {
    return retryMode;
  }

  public void setRetryMode(RetryMode retryMode) {
    this.retryMode = retryMode;
  }
}
