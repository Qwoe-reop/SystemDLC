package dev.mark.system.util;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;
import net.minecraft.util.math.BlockPos;

public class CombatHelper {
    private static final MinecraftClient KF = MinecraftClient.method_1551();
    private static final float SettingsButton = -0.15F;
    private static final float Cq = -0.01F;
    private static final double CE = 0.4;
    private static final float EV = 0.8F;
    private static final float abp = 0.8F;

    public static boolean a() {
        return KF.field_1724 == null ? false : b(KF.field_1724.method_6047()) || b(KF.field_1724.method_6079());
    }

    public static boolean b(ItemStack itemstack) {
        if (itemstack.method_7960()) {
            return false;
        }

        Item item = itemstack.method_7909();
        return item instanceof SwordItem || item instanceof AxeItem || item instanceof TridentItem || item instanceof BowItem || item instanceof CrossbowItem;
    }

    public static boolean c(String s, boolean flag) {
        return e(s, flag, 0.0F, true);
    }

    public static boolean d(String s, boolean flag, float f) {
        return e(s, flag, f, true);
    }

    public static boolean e(String s, boolean flag, float f, boolean flag1) {
        if (KF.field_1724 != null && flag) {
            float f1 = KF.field_1724.method_7261(f);
            if (f1 < 0.8F) {
                return false;
            } else {
                boolean flag2 = f();
                if ("Criticals Only".equals(s) || "Только криты".equals(s)) {
                    return h() || flag2;
                } else if (!h() && KF.field_1724.method_18798().field_1351 > 0.0) {
                    return false;
                } else {
                    return KF.field_1724.method_24828() ? f1 >= 0.8F && flag1 : h() || flag2;
                }
            }
        } else {
            return false;
        }
    }

    public static boolean f() {
        if (KF.field_1724 == null) {
            return false;
        }

        if (!KF.field_1724.method_24828() && !KF.field_1724.method_5771() && !KF.field_1724.method_5799() && !KF.field_1724.method_5765()) {
            double d0 = KF.field_1724.method_18798().field_1351;
            if (g() && d0 < -0.01F) {
                double d1 = KF.field_1724.method_23318() - (KF.field_1724.method_24515().method_10074().method_10264() + 1.0);
                if (d1 > 0.4) {
                    return true;
                }
            }

            return d0 < -0.15F;
        } else {
            return false;
        }
    }

    public static boolean g() {
        if (KF.field_1724 != null && KF.field_1687 != null) {
            BlockPos blockpos = KF.field_1724.method_24515().method_10086(2);
            BlockState blockstate = KF.field_1687.method_8320(blockpos);
            return !blockstate.method_26215() && blockstate.method_26234(KF.field_1687, blockpos);
        } else {
            return false;
        }
    }

    public static boolean h() {
        return KF.field_1724 == null
            ? false
            : KF.field_1724.method_5799()
                || KF.field_1724.method_5681()
                || KF.field_1724.method_6128()
                || KF.field_1724.method_6101()
                || KF.field_1724.method_5757()
                || KF.field_1724.method_5771()
                || KF.field_1724.method_5765();
    }

    public static boolean i(LivingEntity livingentity) {
        return !livingentity.method_6039() ? false : livingentity.method_6030().method_7909() == Items.field_8255;
    }

    public static int j() {
        if (KF.field_1724 == null) {
            return -1;
        }

        for (int i = 0; i < 9; i++) {
            ItemStack itemstack = KF.field_1724.method_31548().method_5438(i);
            if (!itemstack.method_7960() && itemstack.method_7909() instanceof AxeItem) {
                return i;
            }
        }

        return -1;
    }
}
