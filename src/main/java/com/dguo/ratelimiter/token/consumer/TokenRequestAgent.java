package com.dguo.ratelimiter.token.consumer;

import com.dguo.ratelimiter.token.model.Token;
import com.dguo.ratelimiter.token.model.TokenUnavailableException;

public interface TokenRequestAgent {
  /**
   * To request more token from token-producer (in distributed server)
   *
   * @param clientId
   * @param requestId
   * @return
   * @throws TokenUnavailableException
   */
  Token requestMoreToken(String clientId, String requestId) throws TokenUnavailableException;
}
