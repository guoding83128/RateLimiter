package com.dguo.ratelimiter.counter;

import com.dguo.ratelimiter.config.model.RequestRateLimit;
import com.dguo.ratelimiter.counter.model.SlideWindowCounter;
import com.dguo.ratelimiter.utils.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SlideWindowCounterTest {
  private static final int MAX_REQUEST = 100;
  private static final int SECONDS = 1;

  private RequestRateLimit requestRateLimit;
  private SlideWindowCounter slideWindowCounter;

  @Before
  public void setUp() {
    this.requestRateLimit = new RequestRateLimit(MAX_REQUEST, SECONDS);
    this.slideWindowCounter = new SlideWindowCounter(this.requestRateLimit);
  }

  @Test
  public void test_count_under_limit_with_burst() {
    for (int i = 0; i < MAX_REQUEST; i++) {
      Assert.assertTrue(this.slideWindowCounter.count());
    }
  }

  @Test
  public void test_count_over_limit_with_burst() {
    for (int i = 0; i < MAX_REQUEST; i++) {
      Assert.assertTrue(this.slideWindowCounter.count());
    }
    Assert.assertFalse(this.slideWindowCounter.count());
  }

  @Test
  public void test_count_under_limit_continues() {
    for (int i = 0; i < MAX_REQUEST * 2; i++) {
      Assert.assertTrue(this.slideWindowCounter.count());
      TestUtils.testWait(SECONDS * 1000L / SlideWindowCounter.GRANULARITY);
    }
  }

  @Test
  public void test_count_over_limit_continues() {
    int reject_count = 0;
    for (int i = 0; i < MAX_REQUEST * 2; i++) {
      if (!this.slideWindowCounter.count()) {
        reject_count++;
      }
      TestUtils.testWait(SECONDS * 500L / SlideWindowCounter.GRANULARITY);
    }
    Assert.assertTrue(reject_count > 0);
  }
}
