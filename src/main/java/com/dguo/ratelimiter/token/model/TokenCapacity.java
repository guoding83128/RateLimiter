package com.dguo.ratelimiter.token.model;

import com.dguo.ratelimiter.config.model.RequestRateLimit;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenCapacity {
  private long lastRefillTime;
  private double currToken;
  private RequestRateLimit requestRateLimit;
}
