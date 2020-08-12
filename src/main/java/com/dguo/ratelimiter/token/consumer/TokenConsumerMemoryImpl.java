package com.dguo.ratelimiter.token.consumer;

import com.dguo.ratelimiter.config.BeanConfig;
import com.dguo.ratelimiter.config.SystemConfigManager;
import com.dguo.ratelimiter.config.model.RequestRateLimit;
import com.dguo.ratelimiter.token.model.TokenCapacity;
import com.dguo.ratelimiter.token.model.TokenUnavailableException;

import java.util.HashMap;
import java.util.Map;

/** The token generator in-memory implementation, without persistence. */
/**
 * This is the token consumer in-memory implementation without persistence which can be used on
 * single machine only. It's promise to be local consistency.
 *
 * @author dguo
 * @version 1.0
 * @since 2020-08-12
 */
public class TokenConsumerMemoryImpl implements TokenConsumer {
  private Map<String, TokenCapacity> tokenCapacityMap;
  private Map<String, RequestRateLimit> requestRateLimitMap;
  private SystemConfigManager systemConfigManager;

  public TokenConsumerMemoryImpl() {
    try {
      systemConfigManager =
          (SystemConfigManager)
              Class.forName(BeanConfig.getProperty("TokenConsumerMemoryImpl.SystemConfigManager"))
                  .newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
    this.initResources();
  }

  public TokenConsumerMemoryImpl(final SystemConfigManager systemConfigManager) {
    this.systemConfigManager = systemConfigManager;
    this.initResources();
  }

  private void initResources() {
    this.tokenCapacityMap = new HashMap<>();
    this.requestRateLimitMap = new HashMap<>();
  }

  @Override
  public void consumeToken(final String clientId, final String requestId, final int requestToken)
      throws TokenUnavailableException {
    if (requestToken <= 0) {
      throw new IllegalArgumentException("Request token value is non-positive");
    }

    final String uuid = generateKey(clientId, requestId);
    final RequestRateLimit requestRateLimit = getRequestRateLimit(uuid, clientId, requestId);
    final long now = System.currentTimeMillis();

    synchronized (this) {
      final TokenCapacity tokenCapacity =
          this.tokenCapacityMap.getOrDefault(
              uuid,
              TokenCapacity.builder()
                  .requestRateLimit(requestRateLimit)
                  .currToken(requestRateLimit.getMaxRequest())
                  .lastRefillTime(now)
                  .build());

      // refilling
      final double refilledToken =
          1.0
              * requestRateLimit.getMaxRequest()
              * (now - tokenCapacity.getLastRefillTime())
              / (requestRateLimit.getSeconds() * 1000);
      tokenCapacity.setCurrToken(
          Math.min(requestRateLimit.getMaxRequest(), tokenCapacity.getCurrToken() + refilledToken));
      tokenCapacity.setLastRefillTime(now);

      this.tokenCapacityMap.put(uuid, tokenCapacity);

      // not enough
      if (tokenCapacity.getCurrToken() < requestToken) {
        throw new TokenUnavailableException();
      }

      // ok to serve
      tokenCapacity.setCurrToken(tokenCapacity.getCurrToken() - requestToken);
    }
  }

  private String generateKey(final String clientId, final String requestId) {
    return clientId + "&nbsp" + requestId;
  }

  private RequestRateLimit getRequestRateLimit(
      final String uuid, final String clientId, final String requestId) {
    RequestRateLimit requestRateLimit = this.requestRateLimitMap.get(uuid);
    if (requestRateLimit == null) {
      requestRateLimit = this.systemConfigManager.getRequestRateLimit(clientId, requestId);
      this.requestRateLimitMap.put(uuid, requestRateLimit);
    }
    return requestRateLimit;
  }
}
