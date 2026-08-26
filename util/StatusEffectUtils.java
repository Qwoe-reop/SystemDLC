package dev.mark.system.util;

import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class StatusEffectUtils {
    private static final Map<String, String> amU = Map.ofEntries(
        Map.entry("speed", "Speed"),
        Map.entry("slowness", "Slowness"),
        Map.entry("haste", "Haste"),
        Map.entry("mining_fatigue", "Mining Fatigue"),
        Map.entry("strength", "Strength"),
        Map.entry("regeneration", "Regeneration"),
        Map.entry("fire_resistance", "Fire Resistance"),
        Map.entry("water_breathing", "Water Breathing"),
        Map.entry("night_vision", "Night Vision"),
        Map.entry("invisibility", "Invisibility"),
        Map.entry("poison", "Poison"),
        Map.entry("wither", "Wither"),
        Map.entry("absorption", "Absorption"),
        Map.entry("jump_boost", "Jump Boost"),
        Map.entry("resistance", "Resistance")
    );
    private static final Map<String, String> xS = Map.ofEntries(
        Map.entry("speed", "Speed"),
        Map.entry("slowness", "Slowness"),
        Map.entry("haste", "Haste"),
        Map.entry("strength", "Strength"),
        Map.entry("regeneration", "Regen"),
        Map.entry("fire_resistance", "Fire Res"),
        Map.entry("water_breathing", "Water Br."),
        Map.entry("night_vision", "Night Vis."),
        Map.entry("invisibility", "Invis."),
        Map.entry("poison", "Poison"),
        Map.entry("wither", "Wither"),
        Map.entry("absorption", "Absorption"),
        Map.entry("jump_boost", "Jump Boost"),
        Map.entry("resistance", "Resistance")
    );

    private StatusEffectUtils() {
    }

    public static String a(int i) {
        switch (i) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VIII";
            case 9:
                return "IX";
            case 10:
                return "X";
            default:
                return String.valueOf(i);
        }
    }

    public static String b(int i) {
        int j = i / 20;
        int k = j / 60;
        int l = j % 60;
        return String.format("%d:%02d", k, l);
    }

    public static String c(StatusEffect statuseffect) {
        MinecraftClient minecraftclient = MinecraftClient.method_1551();
        if (minecraftclient.field_1687 == null) {
            return "";
        }

        Registry registry = minecraftclient.field_1687.method_30349().method_30530(RegistryKeys.field_41208);
        Identifier identifier = registry.method_10221(statuseffect);
        return identifier != null ? identifier.method_12832() : "";
    }

    public static String d(StatusEffect statuseffect) {
        String s = c(statuseffect);
        return amU.getOrDefault(s, statuseffect.method_5560().getString());
    }

    public static String e(StatusEffect statuseffect) {
        String s = c(statuseffect);
        return xS.getOrDefault(s, statuseffect.method_5560().getString());
    }

    public static String f(StatusEffect statuseffect, int i) {
        String s = d(statuseffect);
        if (s.length() > i) {
            String s1 = s.substring(0, i - 2);
            return s1 + "..";
        } else {
            return s;
        }
    }
}
