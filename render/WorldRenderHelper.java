package dev.mark.system.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.Set;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class WorldRenderHelper {
    private static final Set<Item> agX = Set.of(
        Items.field_8477,
        Items.field_8687,
        Items.field_8695,
        Items.field_8620,
        Items.field_22020,
        Items.field_8802,
        Items.field_8377,
        Items.field_8556,
        Items.field_8250,
        Items.field_8527,
        Items.field_8805,
        Items.field_8058,
        Items.field_8348,
        Items.field_8285,
        Items.field_22022,
        Items.field_22024,
        Items.field_22025,
        Items.field_22023,
        Items.field_22026,
        Items.field_22027,
        Items.field_22028,
        Items.field_22029,
        Items.field_22030,
        Items.field_8367,
        Items.field_8463,
        Items.field_8288,
        Items.field_8833,
        Items.field_8545,
        Items.field_8722,
        Items.field_8380,
        Items.field_8050,
        Items.field_8829,
        Items.field_8271,
        Items.field_8548,
        Items.field_8520,
        Items.field_8627,
        Items.field_8451,
        Items.field_8213,
        Items.field_8816,
        Items.field_8350,
        Items.field_8584,
        Items.field_8461,
        Items.field_8676,
        Items.field_8268
    );
    private static final Set<Item> zu = Set.of(
        Items.field_8229,
        Items.field_8176,
        Items.field_8261,
        Items.field_8544,
        Items.field_8347,
        Items.field_8509,
        Items.field_8373,
        Items.field_8512,
        Items.field_8179,
        Items.field_8567,
        Items.field_8186,
        Items.field_8279
    );

    public static void a(boolean flag) {
        if (!flag) {
            RenderSystem.disableDepthTest();
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(ShaderProgramKeys.field_53876);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void b() {
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }

    public static Vec3d c(Entity entity, float f, Vec3d vec3d) {
        double d0 = MathHelper.method_16436(f, entity.field_6014, entity.method_23317()) - vec3d.field_1352;
        double d1 = MathHelper.method_16436(f, entity.field_6036, entity.method_23318()) - vec3d.field_1351;
        double d2 = MathHelper.method_16436(f, entity.field_5969, entity.method_23321()) - vec3d.field_1350;
        return new Vec3d(d0, d1, d2);
    }

    public static void d(BufferBuilder bufferbuilder, Matrix4f matrix4f, Vec3d vec3d, float f, float f1, float f2, float f3) {
        bufferbuilder.method_22918(matrix4f, (float)vec3d.field_1352, (float)vec3d.field_1351, (float)vec3d.field_1350).method_22915(f, f1, f2, f3);
    }

    public static void d(
        BufferBuilder bufferbuilder, Matrix4f matrix4f, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a
    ) {
        bufferbuilder.method_22918(matrix4f, x1, y1, z1).method_22915(r, g, b, a);
        bufferbuilder.method_22918(matrix4f, x2, y1, z2).method_22915(r, g, b, a);
        bufferbuilder.method_22918(matrix4f, x2, y2, z2).method_22915(r, g, b, a);
        bufferbuilder.method_22918(matrix4f, x1, y2, z1).method_22915(r, g, b, a);
    }

    public static void e(BufferBuilder bufferbuilder, Matrix4f matrix4f, float f, float f1, float f2, float f3, float f4, float f5, float[] afloat, float f6) {
        bufferbuilder.method_22918(matrix4f, f, f1, f2).method_22915(afloat[0], afloat[1], afloat[2], f6);
        bufferbuilder.method_22918(matrix4f, f3, f4, f5).method_22915(afloat[0], afloat[1], afloat[2], f6);
    }

    public static void f(BufferBuilder bufferbuilder, Matrix4f matrix4f, float f, float f1, float[] afloat, float f2) {
        float f3 = f / 2.0F;
        e(bufferbuilder, matrix4f, -f3, 0.0F, -f3, -f3, 0.0F, f3, afloat, f2);
        e(bufferbuilder, matrix4f, -f3, 0.0F, f3, f3, 0.0F, f3, afloat, f2);
        e(bufferbuilder, matrix4f, f3, 0.0F, f3, f3, 0.0F, -f3, afloat, f2);
        e(bufferbuilder, matrix4f, f3, 0.0F, -f3, -f3, 0.0F, -f3, afloat, f2);
        e(bufferbuilder, matrix4f, -f3, f1, -f3, -f3, f1, f3, afloat, f2);
        e(bufferbuilder, matrix4f, -f3, f1, f3, f3, f1, f3, afloat, f2);
        e(bufferbuilder, matrix4f, f3, f1, f3, f3, f1, -f3, afloat, f2);
        e(bufferbuilder, matrix4f, f3, f1, -f3, -f3, f1, -f3, afloat, f2);
        e(bufferbuilder, matrix4f, -f3, 0.0F, -f3, -f3, f1, -f3, afloat, f2);
        e(bufferbuilder, matrix4f, -f3, 0.0F, f3, -f3, f1, f3, afloat, f2);
        e(bufferbuilder, matrix4f, f3, 0.0F, f3, f3, f1, f3, afloat, f2);
        e(bufferbuilder, matrix4f, f3, 0.0F, -f3, f3, f1, -f3, afloat, f2);
    }

    public static void g(BufferBuilder bufferbuilder, Matrix4f matrix4f, float f, float f1, float f2, float[] afloat, float f3) {
        float f4 = f / 2.0F;
        float f5 = f / 2.0F;
        float f6 = Math.min(f2, Math.min(f, f1) * 0.4F);
        float[][] afloat1 = new float[][]{
            {-f4, 0.0F, -f5}, {f4, 0.0F, -f5}, {-f4, 0.0F, f5}, {f4, 0.0F, f5}, {-f4, f1, -f5}, {f4, f1, -f5}, {-f4, f1, f5}, {f4, f1, f5}
        };
        float[][][] afloat2 = new float[][][]{
            {{f6, 0.0F, 0.0F}, {0.0F, f6, 0.0F}, {0.0F, 0.0F, f6}},
            {{-f6, 0.0F, 0.0F}, {0.0F, f6, 0.0F}, {0.0F, 0.0F, f6}},
            {{f6, 0.0F, 0.0F}, {0.0F, f6, 0.0F}, {0.0F, 0.0F, -f6}},
            {{-f6, 0.0F, 0.0F}, {0.0F, f6, 0.0F}, {0.0F, 0.0F, -f6}},
            {{f6, 0.0F, 0.0F}, {0.0F, -f6, 0.0F}, {0.0F, 0.0F, f6}},
            {{-f6, 0.0F, 0.0F}, {0.0F, -f6, 0.0F}, {0.0F, 0.0F, f6}},
            {{f6, 0.0F, 0.0F}, {0.0F, -f6, 0.0F}, {0.0F, 0.0F, -f6}},
            {{-f6, 0.0F, 0.0F}, {0.0F, -f6, 0.0F}, {0.0F, 0.0F, -f6}}
        };

        for (int i = 0; i < afloat1.length; i++) {
            float[] afloat3 = afloat1[i];
            float[][] afloat4 = afloat2[i];

            for (float[] afloat5 : afloat4) {
                e(
                    bufferbuilder,
                    matrix4f,
                    afloat3[0],
                    afloat3[1],
                    afloat3[2],
                    afloat3[0] + afloat5[0],
                    afloat3[1] + afloat5[1],
                    afloat3[2] + afloat5[2],
                    afloat,
                    f3
                );
            }
        }
    }

    public static void h(MatrixStack matrixstack, int i, int j, int k, int l, float f, int i1) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(ShaderProgramKeys.field_53876);
        Tessellator tessellator = Tessellator.method_1348();
        BufferBuilder bufferbuilder = tessellator.method_60827(DrawMode.field_27382, VertexFormats.field_1576);
        Matrix4f matrix4f = matrixstack.method_23760().method_23761();
        bufferbuilder.method_22918(matrix4f, i - i1, j - i1, 0.0F).method_22915(0.0F, 0.0F, 0.0F, f);
        bufferbuilder.method_22918(matrix4f, i - i1, j + l + i1, 0.0F).method_22915(0.0F, 0.0F, 0.0F, f);
        bufferbuilder.method_22918(matrix4f, i + k + i1, j + l + i1, 0.0F).method_22915(0.0F, 0.0F, 0.0F, f);
        bufferbuilder.method_22918(matrix4f, i + k + i1, j - i1, 0.0F).method_22915(0.0F, 0.0F, 0.0F, f);
        BufferRenderer.method_43433(bufferbuilder.method_60800());
    }

    public static void i(MatrixStack matrixstack, float f, float f1, float f2, float f3, int i) {
        if (!(f2 <= 0.0F)) {
            float[] afloat = y(i);
            int j = MathHelper.method_15340((int)(f2 * 1.8F), 48, 220);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest();
            RenderSystem.disableCull();
            RenderSystem.setShader(ShaderProgramKeys.field_53876);
            RenderSystem.lineWidth(Math.max(1.0F, f3));
            Matrix4f matrix4f = matrixstack.method_23760().method_23761();
            BufferBuilder bufferbuilder = Tessellator.method_1348().method_60827(DrawMode.field_29345, VertexFormats.field_1576);

            for (int k = 0; k <= j; k++) {
                float f4 = (float)((Math.PI * 2) * k / j);
                float f5 = f + MathHelper.method_15362(f4) * f2;
                float f6 = f1 + MathHelper.method_15374(f4) * f2;
                bufferbuilder.method_22918(matrix4f, f5, f6, 0.0F).method_22915(afloat[0], afloat[1], afloat[2], afloat[3]);
            }

            BufferRenderer.method_43433(bufferbuilder.method_60800());
            RenderSystem.lineWidth(1.0F);
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }
    }

    public static float[] j(Formatting formatting) {
        switch (formatting) {
            case field_1074:
                return new float[]{0.0F, 0.0F, 0.0F};
            case field_1058:
                return new float[]{0.0F, 0.0F, 0.5F};
            case field_1077:
                return new float[]{0.0F, 0.5F, 0.0F};
            case field_1062:
                return new float[]{0.0F, 0.5F, 0.5F};
            case field_1079:
                return new float[]{0.5F, 0.0F, 0.0F};
            case field_1064:
                return new float[]{0.5F, 0.0F, 0.5F};
            case field_1065:
                return new float[]{1.0F, 0.5F, 0.0F};
            case field_1080:
                return new float[]{0.5F, 0.5F, 0.5F};
            case field_1063:
                return new float[]{0.25F, 0.25F, 0.25F};
            case field_1078:
                return new float[]{0.3F, 0.3F, 1.0F};
            case field_1060:
                return new float[]{0.3F, 1.0F, 0.3F};
            case field_1075:
                return new float[]{0.3F, 1.0F, 1.0F};
            case field_1061:
                return new float[]{1.0F, 0.3F, 0.3F};
            case field_1076:
                return new float[]{1.0F, 0.3F, 1.0F};
            case field_1054:
                return new float[]{1.0F, 1.0F, 0.3F};
            default:
                return new float[]{1.0F, 1.0F, 1.0F};
        }
    }

    public static float[] k(String s) {
        for (Formatting formatting : Formatting.values()) {
            if (s.contains(formatting.toString())) {
                return j(formatting);
            }
        }

        return new float[]{1.0F, 1.0F, 1.0F};
    }

    public static float[] l(Item item) {
        String s = item.toString();
        if (item == Items.field_8477 || s.contains("diamond")) {
            return new float[]{0.3F, 1.0F, 1.0F};
        } else if (item == Items.field_8687 || s.contains("emerald")) {
            return new float[]{0.3F, 1.0F, 0.3F};
        } else if (item == Items.field_8695 || s.contains("gold")) {
            return new float[]{1.0F, 1.0F, 0.3F};
        } else if (item == Items.field_8620 || s.contains("iron")) {
            return new float[]{0.8F, 0.8F, 0.8F};
        } else if (item == Items.field_22020 || s.contains("netherite")) {
            return new float[]{0.3F, 0.1F, 0.3F};
        } else if (item == Items.field_8367) {
            return new float[]{1.0F, 0.3F, 1.0F};
        } else if (item == Items.field_8463) {
            return new float[]{1.0F, 0.8F, 0.3F};
        } else if (item == Items.field_8288) {
            return new float[]{1.0F, 1.0F, 0.0F};
        } else if (item == Items.field_8833) {
            return new float[]{0.5F, 0.3F, 0.8F};
        } else if (s.contains("shulker")) {
            return new float[]{0.8F, 0.3F, 0.8F};
        } else if (s.contains("sword")) {
            return new float[]{1.0F, 0.3F, 0.3F};
        } else if (s.contains("pickaxe") || s.contains("axe") || s.contains("shovel") || s.contains("hoe")) {
            return new float[]{0.3F, 0.8F, 1.0F};
        } else if (!s.contains("helmet") && !s.contains("chestplate") && !s.contains("leggings") && !s.contains("boots")) {
            return zu.contains(item) ? new float[]{1.0F, 0.6F, 0.3F} : new float[]{1.0F, 1.0F, 1.0F};
        } else {
            return new float[]{0.6F, 0.6F, 1.0F};
        }
    }

    public static boolean m(Item item) {
        return agX.contains(item);
    }

    public static int n(PlayerEntity playerentity) {
        float f = playerentity.method_6032();
        float f1 = playerentity.method_6063();
        float f2 = f / f1;
        if (f2 > 0.6F) {
            return 5635925;
        } else {
            return f2 > 0.3F ? 16777045 : 16733525;
        }
    }

    public static Vec3d o(float f, float f1) {
        float f2 = f * (float) (Math.PI / 180.0);
        float f3 = -f1 * (float) (Math.PI / 180.0);
        float f4 = (float)Math.cos(f3);
        float f5 = (float)Math.sin(f3);
        float f6 = (float)Math.cos(f2);
        float f7 = (float)Math.sin(f2);
        return new Vec3d(f5 * f6, -f7, f4 * f6);
    }

    public static boolean p(Vec3d vec3d, Vec3d vec3d1, double d0) {
        double d1 = d0 * d0;
        return !(vec3d.method_1025(vec3d1) <= d1);
    }

    public static void q(MatrixStack matrixstack, Vec3d vec3d, Vec3d vec3d1, float f, float f1, float f2, float f3, boolean flag) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(ShaderProgramKeys.field_53876);
        if (flag) {
            RenderSystem.disableDepthTest();
        }

        Tessellator tessellator = Tessellator.method_1348();
        BufferBuilder bufferbuilder = tessellator.method_60827(DrawMode.field_27382, VertexFormats.field_1576);
        Matrix4f matrix4f = matrixstack.method_23760().method_23761();
        d(
            bufferbuilder,
            matrix4f,
            (float)vec3d.field_1352,
            (float)vec3d.field_1351,
            (float)vec3d.field_1350,
            (float)vec3d1.field_1352,
            (float)vec3d1.field_1351,
            (float)vec3d1.field_1350,
            f,
            f1,
            f2,
            f3
        );
        BufferRenderer.method_43433(bufferbuilder.method_60800());
        RenderSystem.enableDepthTest();
    }

    public static void r(
        BufferBuilder bufferbuilder, Matrix4f matrix4f, float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9
    ) {
        bufferbuilder.method_22918(matrix4f, f, f1, f2).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f3, f4, f5).method_22915(f6, f7, f8, f9);
    }

    public static void s(
        BufferBuilder bufferbuilder, Matrix4f matrix4f, float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9
    ) {
        r(bufferbuilder, matrix4f, f, f1, f2, f3, f1, f2, f6, f7, f8, f9);
        r(bufferbuilder, matrix4f, f3, f1, f2, f3, f1, f5, f6, f7, f8, f9);
        r(bufferbuilder, matrix4f, f3, f1, f5, f, f1, f5, f6, f7, f8, f9);
        r(bufferbuilder, matrix4f, f, f1, f5, f, f1, f2, f6, f7, f8, f9);
        r(bufferbuilder, matrix4f, f, f4, f2, f3, f4, f2, f6, f7, f8, f9);
        r(bufferbuilder, matrix4f, f3, f4, f2, f3, f4, f5, f6, f7, f8, f9);
        r(bufferbuilder, matrix4f, f3, f4, f5, f, f4, f5, f6, f7, f8, f9);
        r(bufferbuilder, matrix4f, f, f4, f5, f, f4, f2, f6, f7, f8, f9);
        r(bufferbuilder, matrix4f, f, f1, f2, f, f4, f2, f6, f7, f8, f9);
        r(bufferbuilder, matrix4f, f3, f1, f2, f3, f4, f2, f6, f7, f8, f9);
        r(bufferbuilder, matrix4f, f3, f1, f5, f3, f4, f5, f6, f7, f8, f9);
        r(bufferbuilder, matrix4f, f, f1, f5, f, f4, f5, f6, f7, f8, f9);
    }

    public static void t(
        BufferBuilder bufferbuilder, Matrix4f matrix4f, float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9
    ) {
        r(bufferbuilder, matrix4f, f, f1, f2, f3, f4, f5, f6, f7, f8, f9);
        r(bufferbuilder, matrix4f, f3, f1, f2, f, f4, f5, f6, f7, f8, f9);
        r(bufferbuilder, matrix4f, f, f1, f5, f3, f4, f2, f6, f7, f8, f9);
        r(bufferbuilder, matrix4f, f3, f1, f5, f, f4, f2, f6, f7, f8, f9);
    }

    public static void u(
        BufferBuilder bufferbuilder, Matrix4f matrix4f, float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9
    ) {
        bufferbuilder.method_22918(matrix4f, f, f1, f2).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f3, f1, f2).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f3, f1, f5).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f, f1, f5).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f, f4, f2).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f, f4, f5).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f3, f4, f5).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f3, f4, f2).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f, f1, f2).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f, f4, f2).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f3, f4, f2).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f3, f1, f2).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f, f1, f5).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f3, f1, f5).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f3, f4, f5).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f, f4, f5).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f, f1, f2).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f, f1, f5).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f, f4, f5).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f, f4, f2).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f3, f1, f2).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f3, f4, f2).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f3, f4, f5).method_22915(f6, f7, f8, f9);
        bufferbuilder.method_22918(matrix4f, f3, f1, f5).method_22915(f6, f7, f8, f9);
    }

    public static void v(BufferBuilder bufferbuilder, Matrix4f matrix4f, float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7) {
        bufferbuilder.method_22918(matrix4f, f, f1 + f3, 0.0F).method_22913(0.0F, 1.0F).method_22915(f4, f5, f6, f7);
        bufferbuilder.method_22918(matrix4f, f + f2, f1 + f3, 0.0F).method_22913(1.0F, 1.0F).method_22915(f4, f5, f6, f7);
        bufferbuilder.method_22918(matrix4f, f + f2, f1, 0.0F).method_22913(1.0F, 0.0F).method_22915(f4, f5, f6, f7);
        bufferbuilder.method_22918(matrix4f, f, f1, 0.0F).method_22913(0.0F, 0.0F).method_22915(f4, f5, f6, f7);
    }

    public static void w(BufferBuilder bufferbuilder, Matrix4f matrix4f, float f, float f1, float f2, float f3) {
        float f4 = 0.5F;
        float f5 = 0.7F;
        float f6 = 0.0F;
        float f7 = -f5 / 2.0F;
        float f8 = -f4 * 0.866F;
        float f9 = -f5 / 2.0F;
        float f10 = -f4 / 2.0F;
        float f11 = f4 * 0.866F;
        float f12 = -f5 / 2.0F;
        float f13 = -f4 / 2.0F;
        float f14 = 0.0F;
        float f15 = f5 / 2.0F;
        float f16 = 0.0F;
        r(bufferbuilder, matrix4f, f6, f7, f4, f8, f9, f10, f, f1, f2, f3);
        r(bufferbuilder, matrix4f, f8, f9, f10, f11, f12, f13, f, f1, f2, f3);
        r(bufferbuilder, matrix4f, f11, f12, f13, f6, f7, f4, f, f1, f2, f3);
        r(bufferbuilder, matrix4f, f6, f7, f4, f14, f15, f16, f, f1, f2, f3);
        r(bufferbuilder, matrix4f, f8, f9, f10, f14, f15, f16, f, f1, f2, f3);
        r(bufferbuilder, matrix4f, f11, f12, f13, f14, f15, f16, f, f1, f2, f3);
    }

    public static int x(Color color, float f) {
        int i = Math.max(0, Math.min(255, (int)(f * 255.0F)));
        return i << 24 | color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
    }

    public static float[] y(int i) {
        return new float[]{(i >> 16 & 0xFF) / 255.0F, (i >> 8 & 0xFF) / 255.0F, (i & 0xFF) / 255.0F, (i >> 24 & 0xFF) / 255.0F};
    }
}
