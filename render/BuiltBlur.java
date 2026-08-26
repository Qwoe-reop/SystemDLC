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

public final class BuiltBlur implements Renderable {
    private static final ShaderProgramKey NY = ShaderUtil.a("blur", VertexFormats.field_1576, Defines.field_53930);
    private static final Supplier<SimpleFramebuffer> abD = Suppliers.memoize(() -> new SimpleFramebuffer(1920, 1080, false));
    private static final MinecraftClient mc = MinecraftClient.method_1551();
    private final Size amp;
    private final RadiusConfig vj;
    private final ColorPair Jg;
    private final float blurRadius;
    private final float smoothness;

    public BuiltBlur(Size size, RadiusConfig radiusconfig, ColorPair colorpair, float f, float f1) {
        this.amp = size;
        this.vj = radiusconfig;
        this.Jg = colorpair;
        this.blurRadius = f;
        this.smoothness = f1;
    }

    @Override
    public void render(Matrix4f matrix4f, float f, float f1, float f2) {
        this.a(matrix4f, f, f1, f2, this.smoothness);
    }

    public void a(Matrix4f matrix4f, float f, float f1, float f2, float f3) {
        Framebuffer framebuffer = mc.method_1522();
        SimpleFramebuffer simpleframebuffer = (SimpleFramebuffer)abD.get();
        if (simpleframebuffer.field_1482 != framebuffer.field_1482 || simpleframebuffer.field_1481 != framebuffer.field_1481) {
            simpleframebuffer.method_1234(framebuffer.field_1482, framebuffer.field_1481);
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        simpleframebuffer.method_1235(false);
        framebuffer.method_1237(simpleframebuffer.field_1482, simpleframebuffer.field_1481);
        framebuffer.method_1235(false);
        RenderSystem.setShaderTexture(0, simpleframebuffer.method_30277());
        float f4 = this.amp.a();
        float f5 = this.amp.b();
        ShaderProgram shaderprogram = RenderSystem.setShader(NY);
        shaderprogram.method_34582("Size").method_1255(f4, f5);
        shaderprogram.method_34582("Radius").method_35657(this.vj.a(), this.vj.b(), this.vj.c(), this.vj.d());
        shaderprogram.method_34582("Smoothness").method_1251(this.blurRadius);
        shaderprogram.method_34582("BlurRadius").method_1251(f3);
        BufferBuilder bufferbuilder = Tessellator.method_1348().method_60827(DrawMode.field_27382, VertexFormats.field_1576);
        bufferbuilder.method_22918(matrix4f, f, f1, f2).method_39415(this.Jg.a());
        bufferbuilder.method_22918(matrix4f, f, f1 + f5, f2).method_39415(this.Jg.b());
        bufferbuilder.method_22918(matrix4f, f + f4, f1 + f5, f2).method_39415(this.Jg.c());
        bufferbuilder.method_22918(matrix4f, f + f4, f1, f2).method_39415(this.Jg.d());
        BufferRenderer.method_43433(bufferbuilder.method_60800());
        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public static void b() {
        SimpleFramebuffer simpleframebuffer = (SimpleFramebuffer)abD.get();
        if (simpleframebuffer != null) {
            simpleframebuffer.method_1238();
        }
    }

    public Size c() {
        return this.amp;
    }

    public RadiusConfig d() {
        return this.vj;
    }

    public ColorPair e() {
        return this.Jg;
    }

    public float f() {
        return this.blurRadius;
    }

    public float g() {
        return this.smoothness;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (object != null && object.getClass() == this.getClass()) {
            BuiltBlur builtblur1 = (BuiltBlur)object;
            return Objects.equals(this.amp, builtblur1.amp)
                && Objects.equals(this.vj, builtblur1.vj)
                && Objects.equals(this.Jg, builtblur1.Jg)
                && Float.floatToIntBits(this.blurRadius) == Float.floatToIntBits(builtblur1.blurRadius)
                && Float.floatToIntBits(this.smoothness) == Float.floatToIntBits(builtblur1.smoothness);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.amp, this.vj, this.Jg, this.blurRadius, this.smoothness);
    }

    @Override
    public String toString() {
        String s3 = String.valueOf(this.amp);
        String s4 = String.valueOf(this.vj);
        String s5 = String.valueOf(this.Jg);
        float f = this.smoothness;
        float f1 = this.blurRadius;
        String s = s5;
        String s1 = s4;
        String s2 = s3;
        return "BuiltBlur[size=" + s2 + ", radius=" + s1 + ", color=" + s + ", smoothness=" + f1 + ", blurRadius=" + f + "]";
    }
}
