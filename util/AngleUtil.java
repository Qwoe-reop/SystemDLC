package dev.mark.system.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

public class AngleUtil {
    public static double normalizeAngle(double angle) {
        double d = angle % 360.0;
        if (d >= 180.0) {
            d -= 360.0;
        }

        if (d < -180.0) {
            d += 360.0;
        }

        return d;
    }

    public static double getGCD() {
        double f = (Double)AngleUtil.MinecraftClientAccessor.getOptions().method_42495().method_41753() * 0.6 + 0.2;
        double g = f * f * f * 8.0;
        return g * 0.15;
    }

    public static double roundToGCD(double value, double gcd) {
        return Math.round(value / gcd) * gcd;
    }

    private static class MinecraftClientAccessor {
        static GameOptions getOptions() {
            return MinecraftClient.method_1551().field_1690;
        }
    }
}
