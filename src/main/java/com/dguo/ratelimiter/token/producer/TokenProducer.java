package com.dguo.ratelimiter.token.producer;

import com.dguo.ratelimiter.token.model.Token;
import com.dguo.ratelimiter.token.model.TokenUnavailableException;

/**
 * The token generator which will be running on servers, accept the request from distributed token
 * consumers, and to promise the global consistency.
 *
 * @author dguo
 * @version 1.0
 * @since 2020-08-12
 */
public interface TokenProducer {
  /**
   * Handle the more token request
   *
   * @param clientId
   * @param requestId
   * @return token if resource is available
   * @throws TokenUnavailableException if resource is unavailable
   */
  Token acceptRequestMoreToken(String clientId, String requestId) throws TokenUnavailableException;
}
