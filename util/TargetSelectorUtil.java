package dev.mark.system.util;

import dev.mark.system.core.FriendManager;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public class TargetSelectorUtil {
    private static final MinecraftClient adE = MinecraftClient.method_1551();

    public static Stream<LivingEntity> a(double d0, boolean flag, double d1) {
        return adE.field_1687 != null && adE.field_1724 != null
            ? StreamSupport.<Entity>stream(adE.field_1687.method_18112().spliterator(), false)
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity)entity)
                .filter(livingentity -> b(livingentity, d0, flag, d1))
            : Stream.empty();
    }

    public static boolean b(LivingEntity livingentity, double d0, boolean flag, double d1) {
        if (adE.field_1724 == null) {
            return false;
        }

        if (!flag) {
            return adE.field_1724.method_5739(livingentity) <= d0;
        }

        double d2 = livingentity.method_23317() - adE.field_1724.method_23317();
        double d3 = livingentity.method_23318() - adE.field_1724.method_23318();
        double d4 = livingentity.method_23321() - adE.field_1724.method_23321();
        double d5 = d2 * d2 + d4 * d4;
        double d6 = Math.sqrt(d5);
        return d6 <= d0 && Math.abs(d3) <= d1;
    }

    public static boolean c(LivingEntity livingentity, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        if (adE.field_1724 == null
            || livingentity == null
            || livingentity == adE.field_1724
            || livingentity.method_29504()
            || !livingentity.method_5805()
            || livingentity.method_7325()
            || livingentity.method_6032() <= 0.0F) {
            return false;
        } else if (!flag && livingentity.method_5767()) {
            return false;
        } else if (!flag1 && livingentity.method_6039()) {
            return false;
        } else if (flag2 && !d(livingentity)) {
            return false;
        } else {
            return livingentity instanceof PlayerEntity ? !FriendManager.getInstance().isFriend(livingentity.method_5477().getString()) : !flag3;
        }
    }

    public static boolean d(LivingEntity livingentity) {
        for (ItemStack itemstack : livingentity.method_5661()) {
            if (!itemstack.method_7960()) {
                return true;
            }
        }

        return false;
    }

    public static boolean e(LivingEntity livingentity, float f) {
        if (adE.field_1724 == null) {
            return true;
        }

        if (f >= 180.0F) {
            return true;
        }

        Vec3d vec3d = livingentity.method_19538()
            .method_1031(0.0, livingentity.method_17682() / 2.0F, 0.0)
            .method_1020(adE.field_1724.method_33571())
            .method_1029();
        double d0 = Math.max(-1.0, Math.min(1.0, adE.field_1724.method_5828(1.0F).method_1026(vec3d)));
        return Math.toDegrees(Math.acos(d0)) <= f / 2.0F;
    }

    public static LivingEntity f(List<LivingEntity> list, String s, LivingEntity livingentity, int i, float f) {
        if (list.isEmpty()) {
            return null;
        }

        if (adE.field_1724 == null) {
            return null;
        }

        switch (s) {
            case "Current Target":
            case "Текущая цель":
                if (livingentity != null && i < f && list.contains(livingentity)) {
                    return livingentity;
                }

                return list.stream().min(Comparator.comparingDouble(livingentity1 -> adE.field_1724.method_5739(livingentity1))).orElse(null);
            case "Lowest HP":
            case "Меньше HP":
                return list.stream().min(Comparator.comparingDouble(LivingEntity::method_6032)).orElse(null);
            case "Nearest":
            case "Ближайший":
            default:
                return list.stream().min(Comparator.comparingDouble(livingentity1 -> adE.field_1724.method_5739(livingentity1))).orElse(null);
        }
    }

    public static Vec3d g(LivingEntity livingentity) {
        return new Vec3d(livingentity.method_23317(), livingentity.method_23318() + livingentity.method_17682() * 0.5, livingentity.method_23321());
    }

    public static boolean h(LivingEntity livingentity) {
        if (livingentity != null && adE.field_1724 != null) {
            double d0 = adE.field_1724.method_5739(livingentity);
            if (d0 <= 1.5) {
                return true;
            }

            Vec3d vec3d = livingentity.method_18798();
            if (vec3d.method_1033() < 0.1) {
                return true;
            }

            Vec3d vec3d1 = vec3d.method_1029();
            Vec3d vec3d2 = adE.field_1724.method_19538().method_1020(livingentity.method_19538()).method_1029();
            return vec3d2.method_1026(vec3d1) > -0.3;
        } else {
            return false;
        }
    }
}
