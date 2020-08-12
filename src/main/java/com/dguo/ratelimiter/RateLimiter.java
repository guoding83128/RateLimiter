package com.dguo.ratelimiter;

import com.dguo.ratelimiter.config.BeanConfig;
import com.dguo.ratelimiter.strategy.RateLimiterStrategy;

public class RateLimiter {
  public static final String RESPONSE_OK = "200 (OK)";
  public static final String RESPONSE_ERROR =
      "429 (Rate limit exceeded. Try again in #{n} seconds)";

  private RateLimiterStrategy rateLimiterStrategy;

  public RateLimiter() {
    try {
      this.rateLimiterStrategy =
          (RateLimiterStrategy)
              Class.forName(BeanConfig.getProperty("RateLimiter.RateLimiterStrategy"))
                  .newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  public RateLimiter(final RateLimiterStrategy rateLimiterStrategy) {
    this.rateLimiterStrategy = rateLimiterStrategy;
  }

  public String acceptRequest(final String clientId, final String requestId) {
    return this.rateLimiterStrategy.allowRequest(clientId, requestId)
        ? RESPONSE_OK
        : RESPONSE_ERROR;
  }
}
