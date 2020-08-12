package com.dguo.ratelimiter.config;

import com.dguo.ratelimiter.config.model.RequestRateLimit;

public interface SystemConfigManager {
  RequestRateLimit getRequestRateLimit(String clientId, String requestId);
}
