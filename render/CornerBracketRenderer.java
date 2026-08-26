package dev.mark.system.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.joml.Matrix4f;

public class CornerBracketRenderer {
    private static final ShaderProgramKey ao = ShaderUtil.a("corner_bracket", VertexFormats.field_1576, Defines.field_53930);
    private static final float Yy = 2.0F;
    private static final float lO = 0.22352941F;
    private static final float pN = 0.23921569F;
    private static final float dL = 0.25490198F;
    private final float aqR;
    private final float acM;
    private final float ahs;
    private final float z;

    public CornerBracketRenderer(float f, float f1, float f2, float f3) {
        this.aqR = f;
        this.acM = f1;
        this.ahs = f2;
        this.z = f3;
    }

    public void a(Matrix4f matrix4f, float f, float f1, float f2) {
        if (!(f2 < 0.01F)) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            ShaderProgram shaderprogram = RenderSystem.setShader(ao);
            shaderprogram.method_34582("Size").method_1255(this.aqR, this.acM);
            shaderprogram.method_34582("Radius").method_1251(this.ahs);
            shaderprogram.method_34582("Thickness").method_1251(this.z);
            shaderprogram.method_34582("StrokeColor").method_1249(0.22352941F, 0.23921569F, 0.25490198F);
            shaderprogram.method_34582("Alpha").method_1251(f2);
            shaderprogram.method_34582("Offset").method_1251(2.0F);
            BufferBuilder bufferbuilder = Tessellator.method_1348().method_60827(DrawMode.field_27382, VertexFormats.field_1576);
            float f3 = f - 2.0F;
            float f4 = f1 - 2.0F;
            float f5 = this.aqR + 4.0F;
            float f6 = this.acM + 4.0F;
            int i = (int)(f2 * 255.0F) << 24 | 16777215;
            bufferbuilder.method_22918(matrix4f, f3, f4, 0.0F).method_39415(i);
            bufferbuilder.method_22918(matrix4f, f3, f4 + f6, 0.0F).method_39415(i);
            bufferbuilder.method_22918(matrix4f, f3 + f5, f4 + f6, 0.0F).method_39415(i);
            bufferbuilder.method_22918(matrix4f, f3 + f5, f4, 0.0F).method_39415(i);
            BufferRenderer.method_43433(bufferbuilder.method_60800());
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }
    }
}
