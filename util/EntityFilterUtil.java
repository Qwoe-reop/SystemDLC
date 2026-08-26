package dev.mark.system.util;

import dev.mark.system.module.Module;
import dev.mark.system.render.WorldRenderHelper;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class EntityFilterUtil {
    public static boolean a(Vec3d vec3d, Camera camera, float f) {
        Vec3d vec3d1 = camera.method_19326();
        Vec3d vec3d2 = vec3d.method_1020(vec3d1).method_1029();
        Vec3d vec3d3 = Vec3d.method_1030(camera.method_19329(), camera.method_19330());
        double d0 = vec3d2.method_1026(vec3d3);
        MinecraftClient minecraftclient = MinecraftClient.method_1551();
        int i = minecraftclient.method_22683().method_4489();
        int j = minecraftclient.method_22683().method_4506();
        double d1 = j > 0 ? (double)i / j : 1.7777777777777777;
        double d2 = Math.toRadians(f);
        double d3 = 2.0 * Math.atan(Math.tan(d2 / 2.0) * d1);
        double d4 = 2.0 * Math.atan(Math.sqrt(Math.pow(Math.tan(d3 / 2.0), 2.0) + Math.pow(Math.tan(d2 / 2.0), 2.0)));
        double d5 = d4 / 2.0 - Math.toRadians(5.0);
        return d0 >= Math.cos(d5);
    }

    public static boolean b(PlayerEntity playerentity, PlayerEntity playerentity1, Vec3d vec3d, double d0, boolean flag, boolean flag1, Module module) {
        if (playerentity == playerentity1) {
            if (!flag) {
                return false;
            }

            MinecraftClient minecraftclient = MinecraftClient.method_1551();
            if (minecraftclient.field_1690.method_31044() == Perspective.field_26664) {
                return false;
            }
        }

        if (!playerentity.method_5805()) {
            return false;
        } else {
            return WorldRenderHelper.p(playerentity.method_19538(), vec3d, d0) ? false : flag1 || !module.isFriendPlayer(playerentity);
        }
    }

    public static boolean c(
        PlayerEntity playerentity, PlayerEntity playerentity1, Vec3d vec3d, double d0, boolean flag, boolean flag1, Module module, Camera camera, float f
    ) {
        if (!b(playerentity, playerentity1, vec3d, d0, flag, flag1, module)) {
            return false;
        }

        if (playerentity == playerentity1) {
            return true;
        }

        Vec3d vec3d1 = playerentity.method_19538().method_1031(0.0, playerentity.method_17682() + 0.5, 0.0);
        Vec3d vec3d2 = playerentity.method_19538().method_1031(0.0, playerentity.method_17682() / 2.0, 0.0);
        return a(vec3d1, camera, f) || a(vec3d2, camera, f);
    }

    public static boolean d(ItemEntity itementity, Vec3d vec3d, double d0, boolean flag, double d1) {
        if (itementity != null && itementity.method_5805()) {
            if (WorldRenderHelper.p(itementity.method_19538(), vec3d, d0)) {
                return false;
            } else {
                return itementity.method_6983().method_7947() < d1 ? false : !flag || WorldRenderHelper.m(itementity.method_6983().method_7909());
            }
        } else {
            return false;
        }
    }

    public static int e(List<? extends PlayerEntity> list, PlayerEntity playerentity, Vec3d vec3d, double d0, boolean flag, boolean flag1, Module module) {
        int i = 0;

        for (PlayerEntity playerentity1 : list) {
            if (b(playerentity1, playerentity, vec3d, d0, flag, flag1, module)) {
                i++;
            }
        }

        return i;
    }
}
