package com.dguo.ratelimiter.counter.model;

public class LimitExceededException extends RuntimeException {
  public LimitExceededException() {
    super();
  }

  public LimitExceededException(final String message) {
    super(message);
  }

  public LimitExceededException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public LimitExceededException(final Throwable cause) {
    super(cause);
  }
}
