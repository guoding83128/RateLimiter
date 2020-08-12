package com.dguo.ratelimiter.token.consumer;

import com.dguo.ratelimiter.token.model.Token;
import com.dguo.ratelimiter.token.model.TokenUnavailableException;

/**
 * The token fetched via RPC call from remote token generator (or IPC in local machine)
 *
 * @author dguo
 * @version 1.0
 * @since 2020-08-12
 */
public class TokenRequestAgentRPCImpl implements TokenRequestAgent {
  @Override
  public Token requestMoreToken(String clientId, String requestId)
      throws TokenUnavailableException {
    return null;
  }
}
