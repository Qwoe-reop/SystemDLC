package dev.mark.system.util;

import dev.mark.system.entity.FreecamEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class MouseLookHandler {
    private double ahw = 0.0;
    private double ali = 0.0;
    private boolean akC = true;
    MinecraftClient hD = MinecraftClient.method_1551();

    public void a(FreecamEntity FreecamEntity, float f) {
        if (this.hD.field_1729 != null && FreecamEntity != null && this.hD.field_1755 == null) {
            long i = this.hD.method_22683().method_4490();
            double[] adouble = new double[1];
            double[] adouble1 = new double[1];
            GLFW.glfwGetCursorPos(i, adouble, adouble1);
            if (this.akC) {
                this.ahw = adouble[0];
                this.ali = adouble1[0];
                this.akC = false;
            }

            double d0 = adouble[0] - this.ahw;
            double d1 = adouble1[0] - this.ali;
            this.ahw = adouble[0];
            this.ali = adouble1[0];
            double d2 = (Double)this.hD.field_1690.method_42495().method_41753() * 0.6 + 0.2;
            double d3 = d2 * d2 * d2 * 8.0 * f;
            d0 *= d3;
            d1 *= d3;
            float f1 = FreecamEntity.method_36454() + (float)d0 * 0.15F;
            float f2 = FreecamEntity.method_36455() + (float)d1 * 0.15F;
            f2 = MathHelper.method_15363(f2, -90.0F, 90.0F);
            FreecamEntity.method_36456(f1);
            FreecamEntity.method_36457(f2);
        }
    }

    public void b() {
        this.akC = true;
    }
}
