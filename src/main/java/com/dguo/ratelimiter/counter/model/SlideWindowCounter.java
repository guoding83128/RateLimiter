package com.dguo.ratelimiter.counter.model;

import com.dguo.ratelimiter.config.model.RequestRateLimit;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

public class SlideWindowCounter {
  public static final int GRANULARITY = 100;

  private final RequestRateLimit requestRateLimit;
  private final ArrayList<CounterSlot> slots;
  private int currTotalCount = 0;

  public SlideWindowCounter(final RequestRateLimit requestRateLimit) {
    this.requestRateLimit = requestRateLimit;
    this.slots = new ArrayList<>();
  }

  public boolean count() {
    final long slotTimeFrame = requestRateLimit.getSeconds() * 1000L / GRANULARITY;
    final CounterSlot lastSlot =
        this.slots.isEmpty() ? null : this.slots.get(this.slots.size() - 1);
    final long now = System.currentTimeMillis();

    if (lastSlot == null) {
      this.slots.add(new CounterSlot(now, 1));
      this.currTotalCount++;
    } else if (now - lastSlot.tick < slotTimeFrame) {
      if (this.currTotalCount >= requestRateLimit.getMaxRequest()) {
        return false;
      }
      this.currTotalCount++;
      lastSlot.count++;
    } else {
      while (this.slots.size() > 0
          && (now - this.slots.get(0).tick) > requestRateLimit.getSeconds() * 1000L) {
        this.currTotalCount -= this.slots.get(0).count;
        this.slots.remove(0);
      }
      if (this.currTotalCount >= requestRateLimit.getMaxRequest()) {
        return false;
      }
      this.slots.add(new CounterSlot(now, 1));
      this.currTotalCount++;
    }
    return true;
  }

  @Data
  @AllArgsConstructor
  private static class CounterSlot {
    private long tick;
    private int count;
  }
}
