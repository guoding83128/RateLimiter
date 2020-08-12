package com.dguo.ratelimiter.token.consumer;

import com.dguo.ratelimiter.config.SystemConfigManager;
import com.dguo.ratelimiter.config.model.RequestRateLimit;
import com.dguo.ratelimiter.token.model.TokenUnavailableException;
import com.dguo.ratelimiter.utils.TestUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TokenConsumerMemoryImplTest {
  private static final int MAX_REQUEST = 10;
  private static final int SECONDS = 1;
  private SystemConfigManager mockSystemConfigManager;

  private TokenConsumerMemoryImpl tokenConsumerMemory;

  @Before
  public void setUp() {
    this.mockSystemConfigManager = mock(SystemConfigManager.class);
    this.tokenConsumerMemory = new TokenConsumerMemoryImpl(this.mockSystemConfigManager);

    when(this.mockSystemConfigManager.getRequestRateLimit(anyString(), anyString()))
        .thenReturn(new RequestRateLimit(MAX_REQUEST, SECONDS));
  }

  @After
  public void tearDown() {}

  @Test
  public void test_requestToken_withFirstTime() {
    this.tokenConsumerMemory.consumeToken("Client-1", "Request-1", 3);
    verify(this.mockSystemConfigManager, times(1))
        .getRequestRateLimit(eq("Client-1"), eq("Request-1"));
  }

  @Test
  public void test_requestToken_withBurst() {
    this.tokenConsumerMemory.consumeToken("Client-1", "Request-1", 4);
    this.tokenConsumerMemory.consumeToken("Client-1", "Request-1", 4);
    Assert.assertThrows(
        TokenUnavailableException.class,
        () -> {
          this.tokenConsumerMemory.consumeToken("Client-1", "Request-1", 4);
        });

    TestUtils.testWait(1000);

    this.tokenConsumerMemory.consumeToken("Client-1", "Request-1", 4);
  }

  @Test
  public void test_requestToken_with_multiple_threads() {
    final int numberOfThreads = 10;
    final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    final AtomicInteger exceptionCounter = new AtomicInteger(0);
    for (int i = 0; i < numberOfThreads; i++) {
      executorService.execute(
          () -> {
            try {
              this.tokenConsumerMemory.consumeToken("Client-1", "Request-1", 1);
            } catch (final TokenUnavailableException e) {
              exceptionCounter.incrementAndGet();
            }
          });
    }

    TestUtils.testWait(2000);

    Assert.assertEquals(0, exceptionCounter.get());
  }

  @Test
  public void test_requestToken_with_multiple_threads_burst() {
    final int numberOfThreads = 20;
    final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    final AtomicInteger exceptionCounter = new AtomicInteger(0);
    for (int i = 0; i < numberOfThreads; i++) {
      executorService.execute(
          () -> {
            try {
              this.tokenConsumerMemory.consumeToken("Client-1", "Request-1", 1);
            } catch (final TokenUnavailableException e) {
              exceptionCounter.incrementAndGet();
            }
          });
    }

    TestUtils.testWait(2000);

    Assert.assertEquals(10, exceptionCounter.get());
  }
}
