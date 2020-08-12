package com.dguo.ratelimiter.strategy;

import com.dguo.ratelimiter.token.consumer.TokenConsumer;
import com.dguo.ratelimiter.token.model.TokenUnavailableException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TokenBasedRateLimiterImplTest {
  private TokenConsumer mockTokenConsumer;

  private TokenBasedRateLimiterImpl tokenBasedRateLimiter;

  @Before
  public void setUp() {
    this.mockTokenConsumer = mock(TokenConsumer.class);
    this.tokenBasedRateLimiter = new TokenBasedRateLimiterImpl(this.mockTokenConsumer);
  }

  @After
  public void tearDown() {}

  @Test
  public void test_allowRequest_ReturnTrue_if_tokenIsAvailable() {
    doNothing().when(this.mockTokenConsumer).consumeToken(anyString(), anyString(), anyInt());
    Assert.assertTrue(tokenBasedRateLimiter.allowRequest("TestClientId", "TestRequestId"));
    verify(this.mockTokenConsumer, times(1))
        .consumeToken(eq("TestClientId"), eq("TestRequestId"), eq(1));
  }

  @Test
  public void test_allowRequest_ReturnFalse_if_tokenIsUnavailable() {
    doThrow(new TokenUnavailableException())
        .when(this.mockTokenConsumer)
        .consumeToken(anyString(), anyString(), anyInt());
    Assert.assertFalse(tokenBasedRateLimiter.allowRequest("TestClientId", "TestRequestId"));
    verify(this.mockTokenConsumer, times(1))
        .consumeToken(eq("TestClientId"), eq("TestRequestId"), eq(1));
  }
}
