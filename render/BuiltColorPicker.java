package dev.mark.system.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.mark.system.gui.enums.IndexedTriState;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.joml.Matrix4f;

public final class BuiltColorPicker implements Renderable {
    private static final ShaderProgramKey Dt = ShaderUtil.a("colorpicker", VertexFormats.field_1576, Defines.field_53930);
    private final float yL;
    private final float eJ;
    private final IndexedTriState BV;
    private final float asJ;
    private final float mx;
    private final float up;
    private final float EW;

    public BuiltColorPicker(float f, float f1, IndexedTriState IndexedTriState, float f2, float f3, float f4, float f5) {
        this.yL = f;
        this.eJ = f1;
        this.BV = IndexedTriState;
        this.asJ = f2;
        this.mx = f3;
        this.up = f4;
        this.EW = f5;
    }

    @Override
    public void render(Matrix4f matrix4f, float f, float f1, float f2) {
        this.a(matrix4f, f, f1, 1.0F);
    }

    public void a(Matrix4f matrix4f, float f, float f1, float f2) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        ShaderProgram shaderprogram = RenderSystem.setShader(Dt);
        shaderprogram.method_34582("Size").method_1255(this.yL, this.eJ);
        shaderprogram.method_34582("PickerType").method_35649(this.BV.b());
        shaderprogram.method_34582("Hue").method_1251(this.asJ);
        shaderprogram.method_34582("Saturation").method_1251(this.mx);
        shaderprogram.method_34582("Brightness").method_1251(this.up);
        shaderprogram.method_34582("Alpha").method_1251(f2);
        shaderprogram.method_34582("CornerRadius").method_1251(this.EW);
        BufferBuilder bufferbuilder = Tessellator.method_1348().method_60827(DrawMode.field_27382, VertexFormats.field_1576);
        int i = (int)(f2 * 255.0F) << 24 | 16777215;
        bufferbuilder.method_22918(matrix4f, f, f1, 0.0F).method_39415(i);
        bufferbuilder.method_22918(matrix4f, f, f1 + this.eJ, 0.0F).method_39415(i);
        bufferbuilder.method_22918(matrix4f, f + this.yL, f1 + this.eJ, 0.0F).method_39415(i);
        bufferbuilder.method_22918(matrix4f, f + this.yL, f1, 0.0F).method_39415(i);
        BufferRenderer.method_43433(bufferbuilder.method_60800());
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public float b() {
        return this.yL;
    }

    public float c() {
        return this.eJ;
    }
}
