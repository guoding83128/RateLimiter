package com.dguo.ratelimiter.counter;

import com.dguo.ratelimiter.config.BeanConfig;
import com.dguo.ratelimiter.config.SystemConfigManager;
import com.dguo.ratelimiter.config.model.RequestRateLimit;
import com.dguo.ratelimiter.counter.model.LimitExceededException;
import com.dguo.ratelimiter.counter.model.SlideWindowCounter;

import java.util.HashMap;
import java.util.Map;

public class RequestCounterSlideWindowImpl implements RequestCounter {
  private SystemConfigManager systemConfigManager;
  private Map<String, RequestRateLimit> requestRateLimitMap;
  private Map<String, SlideWindowCounter> slideWindowCounterMap;

  public RequestCounterSlideWindowImpl() {
    try {
      systemConfigManager =
          (SystemConfigManager)
              Class.forName(
                      BeanConfig.getProperty("RequestCounterSlideWindowImpl.SystemConfigManager"))
                  .newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
    this.initResources();
  }

  public RequestCounterSlideWindowImpl(final SystemConfigManager systemConfigManager) {
    this.systemConfigManager = systemConfigManager;
    this.initResources();
  }

  private void initResources() {
    this.requestRateLimitMap = new HashMap<>();
    this.slideWindowCounterMap = new HashMap<>();
  }

  @Override
  public void countRequest(String clientId, String requestId) throws LimitExceededException {
    final String uuid = generateKey(clientId, requestId);
    final RequestRateLimit requestRateLimit = getRequestRateLimit(uuid, clientId, requestId);

    synchronized (this) {
      SlideWindowCounter slideWindowCounter = this.slideWindowCounterMap.get(uuid);

      if (slideWindowCounter == null) {
        slideWindowCounter = new SlideWindowCounter(requestRateLimit);
        this.slideWindowCounterMap.put(uuid, slideWindowCounter);
      }
      if (!slideWindowCounter.count()) {
        throw new LimitExceededException();
      }
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
