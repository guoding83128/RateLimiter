package com.dguo.ratelimiter.strategy;

import com.dguo.ratelimiter.counter.RequestCounter;
import com.dguo.ratelimiter.counter.model.LimitExceededException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CounterBasedRateLimiterImplTest {
  private RequestCounter mockRequestCounter;

  private CounterBasedRateLimiterImpl counterBasedRateLimiter;

  @Before
  public void setUp() {
    this.mockRequestCounter = mock(RequestCounter.class);
    this.counterBasedRateLimiter = new CounterBasedRateLimiterImpl(this.mockRequestCounter);
  }

  @After
  public void tearDown() {}

  @Test
  public void test_allowRequest_ReturnTrue_if_no_limit_error() {
    doNothing().when(this.mockRequestCounter).countRequest(anyString(), anyString());
    Assert.assertTrue(counterBasedRateLimiter.allowRequest("TestClientId", "TestRequestId"));
    verify(this.mockRequestCounter, times(1)).countRequest(eq("TestClientId"), eq("TestRequestId"));
  }

  @Test
  public void test_allowRequest_ReturnFalse_if_over_limit() {
    doThrow(new LimitExceededException())
        .when(this.mockRequestCounter)
        .countRequest(anyString(), anyString());
    Assert.assertFalse(counterBasedRateLimiter.allowRequest("TestClientId", "TestRequestId"));
    verify(this.mockRequestCounter, times(1)).countRequest(eq("TestClientId"), eq("TestRequestId"));
  }
}
