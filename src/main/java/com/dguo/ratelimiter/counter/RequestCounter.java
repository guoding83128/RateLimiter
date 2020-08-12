package com.dguo.ratelimiter.counter;

import com.dguo.ratelimiter.counter.model.LimitExceededException;

public interface RequestCounter {
  /**
   * Count one request
   *
   * @param clientId
   * @param requestId
   * @throws LimitExceededException if rate limit is hit
   */
  void countRequest(String clientId, String requestId) throws LimitExceededException;
}
