package com.dguo.ratelimiter.strategy;

import com.dguo.ratelimiter.config.BeanConfig;
import com.dguo.ratelimiter.token.consumer.TokenConsumer;
import com.dguo.ratelimiter.token.model.TokenUnavailableException;

public class TokenBasedRateLimiterImpl implements RateLimiterStrategy {
  private TokenConsumer tokenConsumer;

  public TokenBasedRateLimiterImpl() {
    try {
      this.tokenConsumer =
          (TokenConsumer)
              Class.forName(BeanConfig.getProperty("TokenBasedRateLimiterImpl.TokenConsumer"))
                  .newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  public TokenBasedRateLimiterImpl(final TokenConsumer tokenConsumer) {
    this.tokenConsumer = tokenConsumer;
  }

  public boolean allowRequest(final String clientId, final String requestId) {
    try {
      this.tokenConsumer.consumeToken(clientId, requestId, 1);
    } catch (final TokenUnavailableException e) {
      return false;
    }
    return true;
  }
}
