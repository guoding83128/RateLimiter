package com.dguo.ratelimiter.token.model;

public class TokenUnavailableException extends RuntimeException {
  public TokenUnavailableException() {
    super();
  }

  public TokenUnavailableException(final String message) {
    super(message);
  }

  public TokenUnavailableException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public TokenUnavailableException(final Throwable cause) {
    super(cause);
  }
}
