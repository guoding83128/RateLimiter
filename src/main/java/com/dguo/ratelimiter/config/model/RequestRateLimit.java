package com.dguo.ratelimiter.config.model;

import lombok.Value;

/** The limitation of request rate: maxRequest / seconds */
@Value
public class RequestRateLimit {
  private int maxRequest;
  private int seconds;
}
