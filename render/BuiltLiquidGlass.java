package dev.mark.system.render;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.joml.Matrix4f;

public final class BuiltLiquidGlass implements Renderable {
    private static final ShaderProgramKey QE = ShaderUtil.a("liquidglass", VertexFormats.field_1576, Defines.field_53930);
    private static final Supplier<SimpleFramebuffer> alA = Suppliers.memoize(() -> new SimpleFramebuffer(1920, 1080, false));
    private static final MinecraftClient mc = MinecraftClient.method_1551();
    private static boolean oF = false;
    private final Size HB;
    private final RadiusConfig ahA;
    private final ColorPair abJ;
    private final float axu;
    private final float aec;
    private final float awn;
    private final float NN;
    private final float aop;
    private final float afq;
    private final float ahe;
    private final float nZ;
    private final float vw;

    public BuiltLiquidGlass(Size size, RadiusConfig radiusconfig, ColorPair colorpair, float f, float f1, float f2, float f3, float f4) {
        this(size, radiusconfig, colorpair, f, f1, f2, f3, f4, 1.0F, 0.0F, 0.0F, 8.0F);
    }

    public BuiltLiquidGlass(
        Size size, RadiusConfig radiusconfig, ColorPair colorpair, float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8
    ) {
        this.HB = size;
        this.ahA = radiusconfig;
        this.abJ = colorpair;
        this.axu = f;
        this.aec = f1;
        this.awn = f2;
        this.NN = f3;
        this.aop = f4;
        this.afq = f5;
        this.ahe = f6;
        this.nZ = f7;
        this.vw = f8;
    }

    @Override
    public void render(Matrix4f matrix4f, float f, float f1, float f2) {
        this.b(matrix4f, f, f1, f2, 1.0F);
    }

    public void a(Matrix4f matrix4f, float f, float f1, float f2) {
        this.b(matrix4f, f, f1, 0.0F, f2);
    }

    private void b(Matrix4f matrix4f, float f, float f1, float f2, float f3) {
        Framebuffer framebuffer = mc.method_1522();
        SimpleFramebuffer simpleframebuffer = (SimpleFramebuffer)alA.get();
        if (simpleframebuffer.field_1482 != framebuffer.field_1482 || simpleframebuffer.field_1481 != framebuffer.field_1481) {
            simpleframebuffer.method_1234(framebuffer.field_1482, framebuffer.field_1481);
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        if (!oF) {
            simpleframebuffer.method_1235(false);
            framebuffer.method_1237(simpleframebuffer.field_1482, simpleframebuffer.field_1481);
            framebuffer.method_1235(false);
            oF = true;
        }

        RenderSystem.setShaderTexture(0, simpleframebuffer.method_30277());
        float f4 = this.HB.a();
        float f5 = this.HB.b();
        float f6 = this.nZ > 0.001F ? Math.abs(this.ahe) + this.vw + 2.0F : 0.0F;
        ShaderProgram shaderprogram = RenderSystem.setShader(QE);
        shaderprogram.method_34582("Size").method_1255(f4, f5);
        shaderprogram.method_34582("Offset").method_1255(f6, f6);
        shaderprogram.method_34582("Radius").method_35657(this.ahA.a(), this.ahA.b(), this.ahA.c(), this.ahA.d());
        shaderprogram.method_34582("Smoothness").method_1251(this.axu);
        shaderprogram.method_34582("CornerSmoothness").method_1251(this.aec);
        float f7 = (this.abJ.a() >> 24 & 0xFF) / 255.0F * f3;
        shaderprogram.method_34582("GlobalAlpha").method_1251(f7);
        shaderprogram.method_34582("FresnelPower").method_1251(this.awn);
        shaderprogram.method_34582("DistortStrength").method_1251(this.NN);
        shaderprogram.method_34582("BlurRadius").method_1251(this.aop);
        shaderprogram.method_34582("Brightness").method_1251(this.afq);
        shaderprogram.method_34582("ShadowOffset").method_1251(this.ahe);
        shaderprogram.method_34582("ShadowStrength").method_1251(this.nZ);
        shaderprogram.method_34582("ShadowBlurRadius").method_1251(this.vw);
        BufferBuilder bufferbuilder = Tessellator.method_1348().method_60827(DrawMode.field_27382, VertexFormats.field_1576);
        int i = this.abJ.a();
        int j = this.abJ.b();
        int k = this.abJ.c();
        int l = this.abJ.d();
        float f8 = f - f6;
        float f9 = f1 - f6;
        float f10 = f4 + f6 * 2.0F;
        float f11 = f5 + f6 * 2.0F;
        bufferbuilder.method_22918(matrix4f, f8, f9, f2).method_39415(i);
        bufferbuilder.method_22918(matrix4f, f8, f9 + f11, f2).method_39415(j);
        bufferbuilder.method_22918(matrix4f, f8 + f10, f9 + f11, f2).method_39415(k);
        bufferbuilder.method_22918(matrix4f, f8 + f10, f9, f2).method_39415(l);
        BufferRenderer.method_43433(bufferbuilder.method_60800());
        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public static void c() {
        oF = false;
    }

    public static void d() {
        SimpleFramebuffer simpleframebuffer = (SimpleFramebuffer)alA.get();
        if (simpleframebuffer != null) {
            simpleframebuffer.method_1238();
        }
    }

    public Size e() {
        return this.HB;
    }

    public RadiusConfig f() {
        return this.ahA;
    }

    public ColorPair g() {
        return this.abJ;
    }

    public float h() {
        return this.axu;
    }

    public float i() {
        return this.aec;
    }

    public float j() {
        return this.awn;
    }

    public float k() {
        return this.NN;
    }

    public float l() {
        return this.aop;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (object != null && object.getClass() == this.getClass()) {
            BuiltLiquidGlass builtliquidglass1 = (BuiltLiquidGlass)object;
            return Objects.equals(this.HB, builtliquidglass1.HB)
                && Objects.equals(this.ahA, builtliquidglass1.ahA)
                && Objects.equals(this.abJ, builtliquidglass1.abJ)
                && Float.floatToIntBits(this.axu) == Float.floatToIntBits(builtliquidglass1.axu)
                && Float.floatToIntBits(this.aec) == Float.floatToIntBits(builtliquidglass1.aec)
                && Float.floatToIntBits(this.awn) == Float.floatToIntBits(builtliquidglass1.awn)
                && Float.floatToIntBits(this.NN) == Float.floatToIntBits(builtliquidglass1.NN)
                && Float.floatToIntBits(this.aop) == Float.floatToIntBits(builtliquidglass1.aop);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.HB, this.ahA, this.abJ, this.axu, this.aec, this.awn, this.NN, this.aop);
    }

    @Override
    public String toString() {
        String s3 = String.valueOf(this.HB);
        String s4 = String.valueOf(this.ahA);
        String s5 = String.valueOf(this.abJ);
        float f = this.aop;
        float f1 = this.NN;
        float f2 = this.awn;
        float f3 = this.aec;
        float f4 = this.axu;
        String s = s5;
        String s1 = s4;
        String s2 = s3;
        return "BuiltLiquidGlass[size="
            + s2
            + ", radius="
            + s1
            + ", color="
            + s
            + ", smoothness="
            + f4
            + ", cornerSmoothness="
            + f3
            + ", fresnelPower="
            + f2
            + ", distortStrength="
            + f1
            + ", blurRadius="
            + f
            + "]";
    }
}
