package com.dguo.ratelimiter.token.consumer;

import com.dguo.ratelimiter.token.model.TokenUnavailableException;

public interface TokenConsumer {
  /**
   * Client to consume request token
   *
   * @param clientId
   * @param requestId
   * @param requestToken
   * @param timestamp: Request timestamp in milliseconds
   * @throws TokenUnavailableException
   */
  void consumeToken(String clientId, String requestId, int requestToken)
      throws TokenUnavailableException;
}
