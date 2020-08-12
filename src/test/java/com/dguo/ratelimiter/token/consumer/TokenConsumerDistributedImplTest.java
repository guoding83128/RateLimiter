package com.dguo.ratelimiter.token.consumer;

import com.dguo.ratelimiter.token.model.Token;
import com.dguo.ratelimiter.token.model.TokenUnavailableException;
import com.dguo.ratelimiter.utils.TestUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TokenConsumerDistributedImplTest {
  private TokenRequestAgent mockTokenRequestAgent;

  private TokenConsumerDistributedImpl tokenConsumerDistributed;

  @Before
  public void setUp() {
    this.mockTokenRequestAgent = mock(TokenRequestAgent.class);
    this.tokenConsumerDistributed = new TokenConsumerDistributedImpl(this.mockTokenRequestAgent);
  }

  @After
  public void tearDown() {}

  @Test
  public void test_consumeToken_with_single_token_request() {
    when(this.mockTokenRequestAgent.requestMoreToken(anyString(), anyString()))
        .thenReturn(new Token(5));
    this.tokenConsumerDistributed.consumeToken("ClientId-1", "Request-1", 1);
    this.tokenConsumerDistributed.consumeToken("ClientId-1", "Request-1", 1);
    this.tokenConsumerDistributed.consumeToken("ClientId-1", "Request-1", 1);

    verify(this.mockTokenRequestAgent, times(1))
        .requestMoreToken(eq("ClientId-1"), eq("Request-1"));
  }

  @Test
  public void test_consumeToken_with_multiple_token_requests() {
    when(this.mockTokenRequestAgent.requestMoreToken(anyString(), anyString()))
        .thenReturn(new Token(2));
    this.tokenConsumerDistributed.consumeToken("ClientId-1", "Request-1", 1);
    this.tokenConsumerDistributed.consumeToken("ClientId-1", "Request-1", 1);
    this.tokenConsumerDistributed.consumeToken("ClientId-1", "Request-1", 1);
    this.tokenConsumerDistributed.consumeToken("ClientId-1", "Request-1", 1);
    this.tokenConsumerDistributed.consumeToken("ClientId-1", "Request-1", 1);

    verify(this.mockTokenRequestAgent, times(3))
        .requestMoreToken(eq("ClientId-1"), eq("Request-1"));
  }

  @Test
  public void test_consumeToken_with_half_enough_token() {
    when(this.mockTokenRequestAgent.requestMoreToken(anyString(), anyString()))
        .thenReturn(new Token(2))
        .thenThrow(new TokenUnavailableException());
    this.tokenConsumerDistributed.consumeToken("ClientId-1", "Request-1", 1);
    this.tokenConsumerDistributed.consumeToken("ClientId-1", "Request-1", 1);
    verify(this.mockTokenRequestAgent, times(1))
        .requestMoreToken(eq("ClientId-1"), eq("Request-1"));

    Assert.assertThrows(
        TokenUnavailableException.class,
        () -> {
          this.tokenConsumerDistributed.consumeToken("ClientId-1", "Request-1", 1);
        });
    verify(this.mockTokenRequestAgent, times(2))
        .requestMoreToken(eq("ClientId-1"), eq("Request-1"));
  }

  @Test
  public void test_consumeToken_with_multiple_threads() {
    when(this.mockTokenRequestAgent.requestMoreToken(anyString(), anyString()))
        .thenReturn(new Token(2));
    final int numberOfThreads = 10;
    final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    for (int i = 0; i < numberOfThreads; i++) {
      executorService.execute(
          () -> {
            TestUtils.testWait(TestUtils.getRandomInt(0, 2) * 1000);
            TokenConsumerDistributedImplTest.this.tokenConsumerDistributed.consumeToken(
                "ClientId-1", "Request-1", 1);
          });
    }

    TestUtils.testWait(3000);
    verify(this.mockTokenRequestAgent, atLeast(5))
        .requestMoreToken(eq("ClientId-1"), eq("Request-1"));
  }
}
