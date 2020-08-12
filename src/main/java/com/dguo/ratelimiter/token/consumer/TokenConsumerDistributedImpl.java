package com.dguo.ratelimiter.token.consumer;

import com.dguo.ratelimiter.config.BeanConfig;
import com.dguo.ratelimiter.token.model.Token;
import com.dguo.ratelimiter.token.model.TokenUnavailableException;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the token consumer implementation based on distributed platform. It's still based
 * in-memory local consistency, but the global token consistency is persisted on producer side; the
 * token request will be handled by tokenRequestAgent which is responsible to communicate with token
 * producer.
 *
 * @author dguo
 * @version 1.0
 * @since 2020-08-12
 */
public class TokenConsumerDistributedImpl implements TokenConsumer {
  private TokenRequestAgent tokenRequestAgent;
  private Map<String, Token> tokenMap;

  public TokenConsumerDistributedImpl() {
    try {
      this.tokenRequestAgent =
          (TokenRequestAgent)
              Class.forName(
                      BeanConfig.getProperty("TokenConsumerDistributedImpl.TokenRequestAgent"))
                  .newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
    initResources();
  }

  public TokenConsumerDistributedImpl(final TokenRequestAgent tokenRequestAgent) {
    this.tokenRequestAgent = tokenRequestAgent;
    initResources();
  }

  private void initResources() {
    this.tokenMap = new HashMap<>();
  }

  public void consumeToken(final String clientId, final String requestId, final int token)
      throws TokenUnavailableException {
    if (token <= 0) {
      throw new IllegalArgumentException("Request token value is non-positive");
    }
    final String uuid = generateKey(clientId, requestId);
    final int requestAmount = attemptToConsume(uuid, token);
    if (requestAmount > 0) {
      try {
        final Token newAcquiredToken = this.tokenRequestAgent.requestMoreToken(clientId, requestId);
        consumeWithNewAcquiredToken(uuid, token, newAcquiredToken);
      } catch (final TokenUnavailableException e) {
        // do a retry here, in case new token acquired by other thread
        if (attemptToConsume(uuid, token) > 0) {
          throw e;
        }
      }
    }
  }

  private int attemptToConsume(final String uuid, final int token) {
    synchronized (this) {
      final Token currentToken = this.tokenMap.get(uuid);
      if (currentToken == null || currentToken.getToken() < token) {
        return currentToken == null ? token : token - currentToken.getToken();
      }
      this.tokenMap.put(uuid, new Token(currentToken.getToken() - token));
      return 0;
    }
  }

  private void consumeWithNewAcquiredToken(
      final String uuid, final int token, final Token newAcquiredToken) {
    synchronized (this) {
      final Token currentToken = this.tokenMap.get(uuid);
      final int newTokenCount =
          currentToken == null
              ? newAcquiredToken.getToken() - token
              : currentToken.getToken() + newAcquiredToken.getToken() - token;
      this.tokenMap.put(uuid, new Token(newTokenCount));
    }
  }

  private String generateKey(final String clientId, final String requestId) {
    return clientId + "&nbsp" + requestId;
  }
}
