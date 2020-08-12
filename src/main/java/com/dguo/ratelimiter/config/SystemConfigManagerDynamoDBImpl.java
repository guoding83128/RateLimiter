package com.dguo.ratelimiter.config;

import com.dguo.ratelimiter.config.model.RequestRateLimit;

/**
 * The system config fetched from Amazon DynamoDB
 *
 * @author dguo
 * @version 1.0
 * @since 2020-08-12
 */
public class SystemConfigManagerDynamoDBImpl implements SystemConfigManager {
  @Override
  public RequestRateLimit getRequestRateLimit(String clientId, String requestId) {
    return null;
  }
}
