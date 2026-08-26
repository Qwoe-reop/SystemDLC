package dev.mark.system.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.joml.Matrix4f;

public final class BuiltBorderOutline implements Renderable {
    private static final ShaderProgramKey zA = ShaderUtil.a("outline", VertexFormats.field_1576, Defines.field_53930);
    private final float un;
    private final float aaB;
    private final float Rs;
    private final float bM;
    private final float KI;
    private final float hC;
    private final float[] td;

    private static void safeSetUniform(ShaderProgram shader, String name, float... values) {
        GlUniform uniform = shader.method_34582(name);
        if (uniform != null) {
            if (values.length == 1) {
                uniform.method_1251(values[0]);
            } else if (values.length == 2) {
                uniform.method_1255(values[0], values[1]);
            } else if (values.length == 4) {
                uniform.method_35657(values[0], values[1], values[2], values[3]);
            }
        }
    }

    public BuiltBorderOutline(float f, float f1, float f2, float f3, float f4, float f5, float[] afloat) {
        this.un = f;
        this.aaB = f1;
        this.Rs = f2;
        this.bM = f3;
        this.KI = f4;
        this.hC = f5;
        this.td = afloat;
    }

    @Override
    public void render(Matrix4f matrix4f, float f, float f1, float f2) {
        this.b(matrix4f, f, f1, f2, 1.0F);
    }

    public void a(Matrix4f matrix4f, float f, float f1, float f2) {
        this.b(matrix4f, f, f1, 0.0F, f2);
    }

    private void b(Matrix4f matrix4f, float f, float f1, float f2, float f3) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        ShaderProgram shaderprogram = RenderSystem.setShader(zA);
        float f4 = this.bM + this.KI + 2.0F;
        safeSetUniform(shaderprogram, "Size", this.un, this.aaB);
        safeSetUniform(shaderprogram, "Offset", f4, f4);
        safeSetUniform(shaderprogram, "CornerRadius", this.Rs);
        safeSetUniform(shaderprogram, "BorderThickness", this.bM);
        safeSetUniform(shaderprogram, "BorderSoftness", this.KI);
        safeSetUniform(shaderprogram, "Margin", this.hC);
        safeSetUniform(shaderprogram, "GlobalAlpha", f3);
        safeSetUniform(shaderprogram, "BorderColor", this.td[0], this.td[1], this.td[2], this.td[3] * f3);
        BufferBuilder bufferbuilder = Tessellator.method_1348().method_60827(DrawMode.field_27382, VertexFormats.field_1576);
        float f5 = f - f4;
        float f6 = f1 - f4;
        float f7 = this.un + f4 * 2.0F;
        float f8 = this.aaB + f4 * 2.0F;
        int i = (int)(f3 * 255.0F) << 24 | 16777215;
        bufferbuilder.method_22918(matrix4f, f5, f6, f2).method_39415(i);
        bufferbuilder.method_22918(matrix4f, f5, f6 + f8, f2).method_39415(i);
        bufferbuilder.method_22918(matrix4f, f5 + f7, f6 + f8, f2).method_39415(i);
        bufferbuilder.method_22918(matrix4f, f5 + f7, f6, f2).method_39415(i);
        BufferRenderer.method_43433(bufferbuilder.method_60800());
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public float c() {
        return this.un;
    }

    public float d() {
        return this.aaB;
    }

    public float e() {
        return this.Rs;
    }

    public float f() {
        return this.bM;
    }

    public float g() {
        return this.KI;
    }

    public float h() {
        return this.hC;
    }

    public float[] i() {
        return this.td;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (object != null && object.getClass() == this.getClass()) {
            BuiltBorderOutline builtborderoutline1 = (BuiltBorderOutline)object;
            return Float.floatToIntBits(this.un) == Float.floatToIntBits(builtborderoutline1.un)
                && Float.floatToIntBits(this.aaB) == Float.floatToIntBits(builtborderoutline1.aaB)
                && Float.floatToIntBits(this.Rs) == Float.floatToIntBits(builtborderoutline1.Rs)
                && Float.floatToIntBits(this.bM) == Float.floatToIntBits(builtborderoutline1.bM)
                && Float.floatToIntBits(this.KI) == Float.floatToIntBits(builtborderoutline1.KI)
                && Float.floatToIntBits(this.hC) == Float.floatToIntBits(builtborderoutline1.hC)
                && Objects.equals(this.td, builtborderoutline1.td);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.un, this.aaB, this.Rs, this.bM, this.KI, this.hC, this.td);
    }

    @Override
    public String toString() {
        float f6 = this.un;
        float f7 = this.aaB;
        float f8 = this.Rs;
        float f9 = this.bM;
        float f10 = this.KI;
        float f11 = this.hC;
        String s = String.valueOf(this.td);
        float f = f11;
        float f1 = f10;
        float f2 = f9;
        float f3 = f8;
        float f4 = f7;
        float f5 = f6;
        return "BuiltBorderOutline[width="
            + f5
            + ", height="
            + f4
            + ", cornerRadius="
            + f3
            + ", borderThickness="
            + f2
            + ", borderSoftness="
            + f1
            + ", margin="
            + f
            + ", borderColor="
            + s
            + "]";
    }
}
