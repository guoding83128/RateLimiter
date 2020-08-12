package com.dguo.ratelimiter.integration;

import com.dguo.ratelimiter.RateLimiter;
import com.dguo.ratelimiter.counter.RequestCounterSlideWindowImpl;
import com.dguo.ratelimiter.strategy.CounterBasedRateLimiterImpl;
import com.dguo.ratelimiter.strategy.RateLimiterStrategy;
import com.dguo.ratelimiter.strategy.TokenBasedRateLimiterImpl;
import com.dguo.ratelimiter.token.consumer.TokenConsumerMemoryImpl;
import com.dguo.ratelimiter.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Category(IntegrationTest.class)
public class RateLimiterIT {
  private static final int THREAD_COUNT = 10;
  private static final int TEST_RUNNING_SECOND = 120;

  private RateLimiter testRateLimiter;
  private ExecutorService executorService;
  private RateLimiterStrategy tokenMemoryBasedStrategy;
  private RateLimiterStrategy slideWindowCounterStrategy;

  @Before
  public void setUp() {
    this.tokenMemoryBasedStrategy = new TokenBasedRateLimiterImpl(new TokenConsumerMemoryImpl());
    this.slideWindowCounterStrategy =
        new CounterBasedRateLimiterImpl(new RequestCounterSlideWindowImpl());
    this.executorService = Executors.newFixedThreadPool(THREAD_COUNT);
  }

  @After
  public void tearDown() {}

  @Test
  public void test_tokenMemoryBasedStrategy() {
    this.testRateLimiter = new RateLimiter(this.tokenMemoryBasedStrategy);
    System.out.println(
        "RateLimiter with TokenMemoryBasedStrategy started at "
            + new Timestamp(System.currentTimeMillis()).toLocalDateTime()
            + "...");
    launchUpTest();
    System.out.println(
        "RateLimiter with TokenMemoryBasedStrategy completed at "
            + new Timestamp(System.currentTimeMillis()).toLocalDateTime()
            + ".");
  }

  @Test
  public void test_slideWindowCounterStrategy() {
    this.testRateLimiter = new RateLimiter(this.slideWindowCounterStrategy);
    System.out.println(
        "RateLimiter with SlideWindowCounterStrategy started at "
            + new Timestamp(System.currentTimeMillis()).toLocalDateTime()
            + "...");
    launchUpTest();
    System.out.println(
        "RateLimiter with SlideWindowCounterStrategy completed at "
            + new Timestamp(System.currentTimeMillis()).toLocalDateTime()
            + ".");
  }

  private void launchUpTest() {
    final AtomicInteger successCounter = new AtomicInteger(0);
    final AtomicInteger throttleCounter = new AtomicInteger(0);

    for (int i = 0; i < THREAD_COUNT; i++) {
      this.executorService.execute(
          () -> {
            final String clientId = Thread.currentThread().getName();
            final String[] requests = new String[] {"Request-1", "Request-2", "Request3"};
            final long testStartTime = System.currentTimeMillis();

            while (System.currentTimeMillis() - testStartTime < TEST_RUNNING_SECOND * 1000L) {
              final String currRequest = requests[TestUtils.getRandomInt(0, 2)];
              final long now = System.currentTimeMillis();
              final String response =
                  RateLimiterIT.this.testRateLimiter.acceptRequest(clientId, currRequest);

              // sample printing
              if (TestUtils.getRandomInt(0, 60) == 0) {
                System.out.format(
                    "\r\nClient:%s made request: %s at %s, got %s\r\n",
                    clientId, currRequest, new Timestamp(now).toLocalDateTime(), response);
              } else {
                System.out.print(".");
              }

              long waitMilli = TestUtils.getRandomInt(50, 300);
              if (response.equals(RateLimiter.RESPONSE_OK)) {
                successCounter.incrementAndGet();
              } else {
                throttleCounter.incrementAndGet();
                waitMilli += 1000L;
              }

              TestUtils.testWait(waitMilli);
            }
          });
    }

    this.executorService.shutdown();
    try {
      while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {}
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    this.executorService.shutdownNow();

    System.out.format(
        "\r\nTotal successful requests: %d; throttled: %d. \r\n",
        successCounter.get(), throttleCounter.get());
  }
}
