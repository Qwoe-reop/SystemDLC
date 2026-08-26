package dev.mark.system.util;

public class TickHelper {
    private boolean tickMode = false;
    private long tickDelay = 0L;
    private long msDelay = 0L;
    private long lastTickTime = 0L;
    private int tickCount = 0;
    private boolean initialized = false;

    public void reset() {
        this.tickCount = 0;
        this.lastTickTime = System.currentTimeMillis();
        this.initialized = true;
    }

    public void setTickMode(boolean msMode) {
        this.tickMode = msMode;
    }

    public void setTickDelay(long ticks) {
        this.tickDelay = ticks;
    }

    public void setMsDelay(long ms) {
        this.msDelay = ms;
    }

    public void tick() {
        if (!this.initialized) {
            this.lastTickTime = System.currentTimeMillis();
            this.initialized = true;
        }

        this.tickCount++;
    }

    public boolean isReady() {
        if (!this.initialized) {
            this.lastTickTime = System.currentTimeMillis();
            this.initialized = true;
            return false;
        } else {
            return this.tickMode ? System.currentTimeMillis() - this.lastTickTime >= this.msDelay : this.tickCount >= this.tickDelay;
        }
    }
}
