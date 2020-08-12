package com.dguo.ratelimiter.config;

import com.dguo.ratelimiter.config.model.RequestRateLimit;

public class SystemConfigManagerStaticImpl implements SystemConfigManager {
  @Override
  public RequestRateLimit getRequestRateLimit(final String clientId, final String requestId) {
    return new RequestRateLimit(30, 60);
  }
}
