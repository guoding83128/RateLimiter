package com.dguo.ratelimiter.strategy;

public interface RateLimiterStrategy {
  /**
   * Check whether the request is allowed to be served
   *
   * @param clientId: Client Identity
   * @param requestId: Request Identity (different API request type)
   * @return true if allowed, false otherwise
   */
  boolean allowRequest(String clientId, String requestId);
}
