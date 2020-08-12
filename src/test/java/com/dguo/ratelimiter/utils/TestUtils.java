package com.dguo.ratelimiter.utils;

import java.util.Random;

public class TestUtils {
  private static final Random rand = new Random(System.currentTimeMillis());

  private TestUtils() {}

  public static void testWait(final long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static int getRandomInt(final int from, final int to) {
    return rand.nextInt(to - from + 1) + from;
  }
}
