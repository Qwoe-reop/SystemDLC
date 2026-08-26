package dev.mark.system.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class MathUtil {
    public static float a(float f, float f1) {
        return f + (f1 - f) * MinecraftClient.method_1551().method_61966().method_60637(true);
    }

    public static double b(double d0, double d1) {
        return d0 + (d1 - d0) * MinecraftClient.method_1551().method_61966().method_60637(true);
    }

    public static boolean c(int i, int j, int k) {
        return MathHelper.method_15395(Random.method_43047(), i, j + 1) > k;
    }

    public static boolean d(int i, int j, double d0) {
        return MathHelper.method_15366(Random.method_43047(), i, j + 1) > d0;
    }

    public static float e(float f, float f1) {
        return f + (float)Math.random() * (f1 - f);
    }

    public static double f(double d0, double d1) {
        return d0 + Math.random() * (d1 - d0);
    }

    public static int g(int i, int j) {
        return i + (int)(Math.random() * (j - i + 1));
    }

    public static float h(float f, float f1, float f2) {
        return Math.max(f1, Math.min(f2, f));
    }

    public static double i(double d0, double d1, double d2) {
        return Math.max(d1, Math.min(d2, d0));
    }

    public static int j(int i, int j, int k) {
        return Math.max(j, Math.min(k, i));
    }

    public static float k(float f, float f1, float f2) {
        return f + (f1 - f) * f2;
    }

    public static double l(double d0, double d1, double d2) {
        return d0 + (d1 - d0) * d2;
    }

    public static double m() {
        double d0 = (Double)MinecraftClient.method_1551().field_1690.method_42495().method_41753();
        double d1 = d0 * 0.6 + 0.2;
        return d1 * d1 * d1 * 1.2;
    }

    public static double n(double d0, double d1) {
        return d1 <= 1.0E-4 ? d0 : d0 - d0 % d1;
    }

    public static double o(double d0) {
        d0 %= 360.0;
        if (d0 > 180.0) {
            d0 -= 360.0;
        }

        if (d0 < -180.0) {
            d0 += 360.0;
        }

        return d0;
    }
}
