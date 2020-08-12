package com.dguo.ratelimiter;

import com.dguo.ratelimiter.strategy.RateLimiterStrategy;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RateLimiterTest {
  private RateLimiterStrategy mockRateLimiterStrategy;

  private RateLimiter testRateLimiter;

  @Before
  public void setUp() {
    this.mockRateLimiterStrategy = mock(RateLimiterStrategy.class);
    this.testRateLimiter = new RateLimiter(this.mockRateLimiterStrategy);
  }

  @After
  public void tearDown() {}

  @Test
  public void test_allowRequest() {
    when(this.mockRateLimiterStrategy.allowRequest(anyString(), anyString())).thenReturn(true);
    Assert.assertEquals(
        testRateLimiter.acceptRequest("TestClientId", "TestRequestId"), RateLimiter.RESPONSE_OK);
    verify(this.mockRateLimiterStrategy, times(1))
        .allowRequest(eq("TestClientId"), eq("TestRequestId"));
  }

  @Test
  public void test_rejectRequest() {
    when(this.mockRateLimiterStrategy.allowRequest(anyString(), anyString())).thenReturn(false);
    Assert.assertEquals(
        testRateLimiter.acceptRequest("TestClientId", "TestRequestId"), RateLimiter.RESPONSE_ERROR);
    verify(this.mockRateLimiterStrategy, times(1))
        .allowRequest(eq("TestClientId"), eq("TestRequestId"));
  }
}
