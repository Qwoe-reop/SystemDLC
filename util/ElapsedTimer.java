package dev.mark.system.util;

public class ElapsedTimer {
    public static float art = 0.0F;
    long QK = System.nanoTime();

    public float a() {
        return (float)(System.nanoTime() - this.QK) / 1.0E9F;
    }
}
