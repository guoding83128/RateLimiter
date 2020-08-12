package com.dguo.ratelimiter.counter;

import com.dguo.ratelimiter.config.SystemConfigManager;
import com.dguo.ratelimiter.config.model.RequestRateLimit;
import com.dguo.ratelimiter.counter.model.LimitExceededException;
import com.dguo.ratelimiter.utils.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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

public class RequestCounterSlideWindowImplTest {
  private static final int MAX_REQUEST = 10;
  private static final int SECONDS = 1;

  private SystemConfigManager mockSystemConfigManager;

  private RequestCounterSlideWindowImpl requestCounterSlideWindow;

  @Before
  public void setUp() {
    this.mockSystemConfigManager = mock(SystemConfigManager.class);
    this.requestCounterSlideWindow =
        new RequestCounterSlideWindowImpl(this.mockSystemConfigManager);

    when(this.mockSystemConfigManager.getRequestRateLimit(anyString(), anyString()))
        .thenReturn(new RequestRateLimit(MAX_REQUEST, SECONDS));
  }

  @Test
  public void test_requestToken_withFirstTime() {
    this.requestCounterSlideWindow.countRequest("Client-1", "Request-1");
    verify(this.mockSystemConfigManager, times(1))
        .getRequestRateLimit(eq("Client-1"), eq("Request-1"));
  }

  @Test
  @Ignore
  public void test_requestToken_withBurst() {
    for (int i = 0; i < MAX_REQUEST; i++) {
      this.requestCounterSlideWindow.countRequest("Client-1", "Request-1");
    }

    Assert.assertThrows(
        LimitExceededException.class,
        () -> {
          this.requestCounterSlideWindow.countRequest("Client-1", "Request-1");
        });

    TestUtils.testWait(SECONDS * 1000L);

    this.requestCounterSlideWindow.countRequest("Client-1", "Request-1");
  }

  @Test
  public void test_requestToken_with_multiple_threads() {
    final int numberOfThreads = MAX_REQUEST;
    final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    final AtomicInteger exceptionCounter = new AtomicInteger(0);
    for (int i = 0; i < numberOfThreads; i++) {
      executorService.execute(
          () -> {
            try {
              this.requestCounterSlideWindow.countRequest("Client-1", "Request-1");
            } catch (final LimitExceededException e) {
              exceptionCounter.incrementAndGet();
            }
          });
    }

    TestUtils.testWait(2000);

    Assert.assertEquals(0, exceptionCounter.get());
  }

  @Test
  public void test_requestToken_with_multiple_threads_burst() {
    final int numberOfThreads = 2 * MAX_REQUEST;
    final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    final AtomicInteger exceptionCounter = new AtomicInteger(0);
    for (int i = 0; i < numberOfThreads; i++) {
      executorService.execute(
          () -> {
            try {
              this.requestCounterSlideWindow.countRequest("Client-1", "Request-1");
            } catch (final LimitExceededException e) {
              exceptionCounter.incrementAndGet();
            }
          });
    }

    TestUtils.testWait(2000);

    Assert.assertEquals(MAX_REQUEST, exceptionCounter.get());
  }
}
