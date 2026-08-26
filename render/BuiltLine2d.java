package dev.mark.system.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.joml.Matrix4f;

public final class BuiltLine2d implements Renderable {
    private static final ShaderProgramKey Bn = ShaderUtil.a("line", VertexFormats.field_1576, Defines.field_53930);
    private final float length;
    private final float thickness;
    private final boolean vertical;
    private final int color;
    private final boolean fadeEnabled;

    public BuiltLine2d(float f, float f1, boolean flag, int i, boolean flag1) {
        this.length = f;
        this.thickness = f1;
        this.vertical = flag;
        this.color = i;
        this.fadeEnabled = flag1;
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
        ShaderProgram shaderprogram = RenderSystem.setShader(Bn);
        float f4;
        float f5;
        if (this.vertical) {
            f4 = this.thickness;
            f5 = this.length;
        } else {
            f4 = this.length;
            f5 = this.thickness;
        }

        shaderprogram.method_34582("Size").method_1255(f4, f5);
        float f6 = (this.color >> 24 & 0xFF) / 255.0F;
        float f7 = (this.color >> 16 & 0xFF) / 255.0F;
        float f8 = (this.color >> 8 & 0xFF) / 255.0F;
        float f9 = (this.color & 0xFF) / 255.0F;
        float f10 = f6 * f3;
        shaderprogram.method_34582("Color").method_35657(f7, f8, f9, f10);
        shaderprogram.method_34582("FadeEnabled").method_35649(this.fadeEnabled ? 1 : 0);
        int i = (int)(f10 * 255.0F) << 24 | this.color & 16777215;
        BufferBuilder bufferbuilder = Tessellator.method_1348().method_60827(DrawMode.field_27382, VertexFormats.field_1576);
        bufferbuilder.method_22918(matrix4f, f, f1, f2).method_39415(i);
        bufferbuilder.method_22918(matrix4f, f, f1 + f5, f2).method_39415(i);
        bufferbuilder.method_22918(matrix4f, f + f4, f1 + f5, f2).method_39415(i);
        bufferbuilder.method_22918(matrix4f, f + f4, f1, f2).method_39415(i);
        BufferRenderer.method_43433(bufferbuilder.method_60800());
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public float c() {
        return this.length;
    }

    public float d() {
        return this.thickness;
    }

    public boolean e() {
        return this.vertical;
    }

    public int f() {
        return this.color;
    }

    public boolean g() {
        return this.fadeEnabled;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (object != null && object.getClass() == this.getClass()) {
            BuiltLine2d builtline2d1 = (BuiltLine2d)object;
            return Float.floatToIntBits(this.length) == Float.floatToIntBits(builtline2d1.length)
                && Float.floatToIntBits(this.thickness) == Float.floatToIntBits(builtline2d1.thickness)
                && this.vertical == builtline2d1.vertical
                && this.color == builtline2d1.color
                && this.fadeEnabled == builtline2d1.fadeEnabled;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.length, this.thickness, this.vertical, this.color, this.fadeEnabled);
    }

    @Override
    public String toString() {
        boolean flag = this.fadeEnabled;
        int i = this.color;
        boolean flag1 = this.vertical;
        float f = this.thickness;
        float f1 = this.length;
        return "BuiltLine2d[length=" + f1 + ", thickness=" + f + ", vertical=" + flag1 + ", color=" + i + ", fadeEnabled=" + flag + "]";
    }
}
