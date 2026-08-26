package dev.mark.system.render.particle;

import dev.mark.system.module.particles.JumpParticlesModule;
import dev.mark.system.render.BoxWireframeRenderer;
import dev.mark.system.render.BuiltLine3d;
import dev.mark.system.render.WorldRenderUtils;
import java.awt.Color;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

public class JumpWaveParticle {
    private static final float asy = 300.0F;
    private final BlockPos bF;
    private final long agz;
    private float KM;
    private float avH;
    private boolean avE;
    private long apb;
    private float axG;
    final JumpParticlesModule Jp;

    public JumpWaveParticle(JumpParticlesModule jumpparticlesmodule, BlockPos blockpos, long i) {
        this.Jp = jumpparticlesmodule;
        this.KM = 0.0F;
        this.avH = -1.0F;
        this.avE = false;
        this.apb = 0L;
        this.axG = 1.0F;
        this.bF = blockpos;
        this.agz = i;
    }

    public void a() {
        if (!this.avE) {
            this.avE = true;
            this.apb = System.currentTimeMillis();
            this.avH = this.KM;
        }
    }

    public boolean b() {
        return this.avE ? this.axG <= 0.01F : System.currentTimeMillis() - this.agz > this.Jp.durationSetting.getValue();
    }

    public void c(WorldRenderContext worldrendercontext) {
        if (this.Jp.getWorld() != null) {
            if (this.avE) {
                this.axG = Math.max(0.0F, 1.0F - (float)(System.currentTimeMillis() - this.apb) / 300.0F);
                if (this.axG <= 0.01F) {
                    return;
                }
            }

            long i = System.currentTimeMillis() - this.agz;
            float f = (float)i / this.Jp.durationSetting.getFloatValue();
            float f1 = WorldRenderUtils.n(Math.min(f, 1.0F));
            int j = (int)this.Jp.radiusSetting.getValue();
            float f2 = f1 * j;
            float f3;
            if (this.avE && this.avH >= 0.0F) {
                f3 = this.avH;
            } else {
                f3 = this.KM + (f2 - this.KM) * 0.3F;
                this.KM = f3;
            }

            float f4 = (float)Math.pow(1.0F - f, 0.6);
            float f5 = this.d(f, f4) * this.axG;
            if (!(f5 < 0.02F)) {
                int k = this.avE ? 100 : 400;
                float f6 = (f3 - 1.0F) * (f3 - 1.0F);
                float f7 = (f3 + 0.5F) * (f3 + 0.5F);
                Color color = this.Jp.waveColorSetting.getColor();
                float f8 = color.getRed() / 255.0F;
                float f9 = color.getGreen() / 255.0F;
                float f10 = color.getBlue() / 255.0F;
                Vec3d vec3d = worldrendercontext.camera().method_19326();
                MatrixStack matrixstack = worldrendercontext.matrixStack();
                BuiltLine3d builtline3d = this.Jp
                    .apz
                    .a(this.Jp.lineWidthSetting.getFloatValue())
                    .b(f8, f9, f10, f5)
                    .l(6)
                    .g(true)
                    .m(true)
                    .n(this.Jp.glowSetting.getValue())
                    .a();
                BoxWireframeRenderer BoxWireframeRenderer = builtline3d.h();
                int l = 0;

                label53:
                for (int i1 = -j; i1 <= j; i1++) {
                    for (int j1 = -j; j1 <= j; j1++) {
                        if (l >= k) {
                            break label53;
                        }

                        float f11 = i1 * i1 + j1 * j1;
                        if (!(f11 < f6) && !(f11 > f7)) {
                            BlockPos blockpos = this.bF.method_10069(i1, 0, j1);
                            BlockPos blockpos1 = this.e(blockpos);
                            if (blockpos1 != null) {
                                BlockState blockstate = this.Jp.getWorld().method_8320(blockpos1);
                                VoxelShape voxelshape = blockstate.method_26218(this.Jp.getWorld(), blockpos1);
                                if (!voxelshape.method_1110()) {
                                    l++;
                                    float f12 = (float)Math.sqrt(f11);
                                    float f13 = WorldRenderUtils.o(Math.max(0.0F, Math.min(1.0F, 1.0F - Math.abs(f12 - f3) / 1.0F)));
                                    if (f13 > 0.02F) {
                                        Box box = voxelshape.method_1107().method_996(blockpos1);
                                        Vec3d vec3d1 = new Vec3d(box.field_1323, box.field_1322, box.field_1321).method_1020(vec3d);
                                        Vec3d vec3d2 = new Vec3d(box.field_1320, box.field_1325, box.field_1324).method_1020(vec3d);
                                        BoxWireframeRenderer.c(vec3d1, vec3d2);
                                    }
                                }
                            }
                        }
                    }
                }

                BoxWireframeRenderer.d(matrixstack, Vec3d.field_1353);
            }
        }
    }

    private float d(float f, float f1) {
        float f2 = this.Jp.appearanceSetting.getFloatValue();
        float f3 = this.Jp.disappearanceSetting.getFloatValue();
        float f4 = this.Jp.durationSetting.getFloatValue();
        float f5 = f * f4;
        float f6 = f1;
        if (f5 < f2) {
            f6 = f1 * WorldRenderUtils.o(f5 / f2);
        }

        float f7 = f4 - f5;
        if (f7 < f3) {
            f6 *= WorldRenderUtils.o(f7 / f3);
        }

        return f6;
    }

    private BlockPos e(BlockPos blockpos) {
        for (int i = 2; i >= -4; i--) {
            BlockPos blockpos1 = blockpos.method_10086(i);
            if (!this.Jp.getWorld().method_8320(blockpos1).method_26215() && this.Jp.getWorld().method_8320(blockpos1.method_10084()).method_26215()) {
                return blockpos1;
            }
        }

        return null;
    }
}
