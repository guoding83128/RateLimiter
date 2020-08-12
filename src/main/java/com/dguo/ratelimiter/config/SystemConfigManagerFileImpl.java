package com.dguo.ratelimiter.config;

import com.dguo.ratelimiter.config.model.RequestRateLimit;

/**
 * The system config fetched from local config files
 *
 * @author dguo
 * @version 1.0
 * @since 2020-08-12
 */
public class SystemConfigManagerFileImpl implements SystemConfigManager {
  @Override
  public RequestRateLimit getRequestRateLimit(String clientId, String requestId) {
    return null;
  }
}
