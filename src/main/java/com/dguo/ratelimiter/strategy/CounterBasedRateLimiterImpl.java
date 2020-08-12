package com.dguo.ratelimiter.strategy;

import com.dguo.ratelimiter.config.BeanConfig;
import com.dguo.ratelimiter.counter.RequestCounter;
import com.dguo.ratelimiter.counter.model.LimitExceededException;

public class CounterBasedRateLimiterImpl implements RateLimiterStrategy {
  private RequestCounter requestCounter;

  public CounterBasedRateLimiterImpl() {
    try {
      this.requestCounter =
          (RequestCounter)
              Class.forName(BeanConfig.getProperty("CounterBasedRateLimiterImpl.RequestCounter"))
                  .newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  public CounterBasedRateLimiterImpl(final RequestCounter requestCounter) {
    this.requestCounter = requestCounter;
  }

  @Override
  public boolean allowRequest(final String clientId, final String requestId) {
    try {
      this.requestCounter.countRequest(clientId, requestId);
      return true;
    } catch (final LimitExceededException e) {
      return false;
    }
  }
}
