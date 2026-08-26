package dev.mark.system.util;

import net.minecraft.entity.TntEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class DangerDetector {
    public static boolean a(World world, PlayerEntity playerentity, double d0) {
        if (world != null && playerentity != null) {
            for (EndCrystalEntity endcrystalentity : world.method_8390(
                EndCrystalEntity.class, playerentity.method_5829().method_1014(d0), endcrystalentity1 -> true
            )) {
                if (playerentity.method_5739(endcrystalentity) <= d0) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public static boolean b(World world, PlayerEntity playerentity, double d0, int i) {
        if (world != null && playerentity != null) {
            for (TntEntity tntentity : world.method_8390(
                TntEntity.class, playerentity.method_5829().method_1014(d0), tntentity1 -> tntentity1.method_6969() <= i
            )) {
                if (playerentity.method_5739(tntentity) <= d0) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }
}
