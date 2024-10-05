package com.xcue.lib;

public class TickTimer {
    private Runnable cb;
    private int ticks;

    public void startWithTicks(int ticks, Runnable cb) {
        this.ticks = ticks;
        this.cb = cb;
    }

     public void startWithSeconds(int seconds, Runnable cb) {
        this.ticks = seconds * 20;
        this.cb = cb;
     }

     public void stop() {
        this.ticks = -1;
     }

    public void tick() {
        if (ticks >= 0) {
            // Run at 0 ticks left
            if (ticks == 0 && cb != null) {
                cb.run();
            }

            // Advance ticks to -1 at the lowest
            ticks--;
        }
    }
}
