package dev.mark.system.render;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Matrix4f;

public final class WorldRenderUtils {
    public static boolean a(CameraSnapshot CameraSnapshot, World world, PlayerEntity playerentity, double d0, double d1, double d2) {
        double d3 = d0 - CameraSnapshot.VS.field_1352;
        double d4 = d1 - CameraSnapshot.VS.field_1351;
        double d5 = d2 - CameraSnapshot.VS.field_1350;
        double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
        if (d6 > 0.0) {
            d3 /= d6;
            d4 /= d6;
            d5 /= d6;
        }

        if (d3 * CameraSnapshot.avF + d4 * CameraSnapshot.ajW + d5 * CameraSnapshot.auw < CameraSnapshot.ayT) {
            return false;
        } else {
            Vec3d vec3d = new Vec3d(d0, d1, d2);
            BlockHitResult blockhitresult = world.method_17742(
                new RaycastContext(CameraSnapshot.VS, vec3d, ShapeType.field_17558, FluidHandling.field_1348, playerentity)
            );
            if (blockhitresult.method_17783() == Type.field_1332) {
                double d7 = CameraSnapshot.VS.method_1025(blockhitresult.method_17784());
                double d8 = (d0 - CameraSnapshot.VS.field_1352) * (d0 - CameraSnapshot.VS.field_1352)
                    + (d1 - CameraSnapshot.VS.field_1351) * (d1 - CameraSnapshot.VS.field_1351)
                    + (d2 - CameraSnapshot.VS.field_1350) * (d2 - CameraSnapshot.VS.field_1350);
                return d7 >= d8;
            } else {
                return true;
            }
        }
    }

    public static void b(MatrixStack matrixstack, CameraSnapshot CameraSnapshot, double d0, double d1, double d2) {
        double d3 = d0 - CameraSnapshot.VS.field_1352;
        double d4 = d1 - CameraSnapshot.VS.field_1351;
        double d5 = d2 - CameraSnapshot.VS.field_1350;
        matrixstack.method_22903();
        matrixstack.method_22907(RotationAxis.field_40714.rotationDegrees(CameraSnapshot.AZ));
        matrixstack.method_22907(RotationAxis.field_40716.rotationDegrees(CameraSnapshot.Sd + 180.0F));
        matrixstack.method_22904(d3, d4, d5);
        matrixstack.method_22907(RotationAxis.field_40716.rotationDegrees(-CameraSnapshot.Sd));
        matrixstack.method_22907(RotationAxis.field_40714.rotationDegrees(CameraSnapshot.AZ));
    }

    public static void c(MatrixStack matrixstack, CameraSnapshot CameraSnapshot, double d0, double d1, double d2, double d3, double d4, double d5, float f) {
        b(matrixstack, CameraSnapshot, d0, d1, d2);
        matrixstack.method_22907(RotationAxis.field_40714.rotation((float)d3));
        matrixstack.method_22907(RotationAxis.field_40716.rotation((float)d4));
        matrixstack.method_22907(RotationAxis.field_40718.rotation((float)d5));
        matrixstack.method_22905(f, f, f);
    }

    public static void d(Identifier identifier, MinecraftClient minecraftclient) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        AbstractTexture abstracttexture = minecraftclient.method_1531().method_4619(identifier);
        RenderSystem.setShaderTexture(0, abstracttexture.method_4624());
        RenderSystem.setShader(ShaderProgramKeys.field_53880);
    }

    public static void e() {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(ShaderProgramKeys.field_53876);
    }

    public static void f() {
        RenderSystem.depthMask(true);
        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static void g(BufferBuilder bufferbuilder, MatrixStack matrixstack, float f, float f1, float f2, float f3, float f4) {
        Matrix4f matrix4f = matrixstack.method_23760().method_23761();
        float f5 = f / 2.0F;
        bufferbuilder.method_22918(matrix4f, -f5, f5, 0.0F).method_22913(0.0F, 1.0F).method_22915(f1, f2, f3, f4);
        bufferbuilder.method_22918(matrix4f, f5, f5, 0.0F).method_22913(1.0F, 1.0F).method_22915(f1, f2, f3, f4);
        bufferbuilder.method_22918(matrix4f, f5, -f5, 0.0F).method_22913(1.0F, 0.0F).method_22915(f1, f2, f3, f4);
        bufferbuilder.method_22918(matrix4f, -f5, -f5, 0.0F).method_22913(0.0F, 0.0F).method_22915(f1, f2, f3, f4);
    }

    public static int h(Color color, float f) {
        return WorldRenderHelper.x(color, f);
    }

    public static float[] i(int i) {
        return new float[]{(i >> 16 & 0xFF) / 255.0F, (i >> 8 & 0xFF) / 255.0F, (i & 0xFF) / 255.0F, (i >> 24 & 0xFF) / 255.0F};
    }

    public static float[] j(Color color, float f) {
        return new float[]{color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, Math.max(0.0F, Math.min(1.0F, f))};
    }

    public static int k(int i, float f) {
        int j = i >> 24 & 0xFF;
        return i & 16777215 | Math.round(j * f) << 24;
    }

    public static int l(float f, long i) {
        float f1 = ((float)(System.currentTimeMillis() % i) / (float)i + f) % 1.0F;
        return Color.HSBtoRGB(f1, 0.8F, 1.0F) | 0xFF000000;
    }

    public static float m(float f, float f1, float f2) {
        if (f < f1) {
            return f / f1;
        } else {
            return f > f2 ? 1.0F - (f - f2) / (1.0F - f2) : 1.0F;
        }
    }

    public static float n(float f) {
        float f1 = f - 1.0F;
        return f1 * f1 * f1 + 1.0F;
    }

    public static float o(float f) {
        return f < 0.5F ? 4.0F * f * f * f : 1.0F - (float)Math.pow(-2.0F * f + 2.0F, 3.0) / 2.0F;
    }
}
