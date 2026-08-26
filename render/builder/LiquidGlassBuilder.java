package dev.mark.system.render.builder;

import dev.mark.system.render.BuiltLiquidGlass;
import dev.mark.system.render.ColorPair;
import dev.mark.system.render.RadiusConfig;
import dev.mark.system.render.Size;

public final class LiquidGlassBuilder extends AbstractBuilder<BuiltLiquidGlass> {
    private Size tD;
    private RadiusConfig ZV;
    private ColorPair Ht;
    private float XN;
    private float RZ;
    private float oi;
    private float arv;
    private float AW;
    private float Zo;
    private float Ah;
    private float Wt;
    private float acy;

    public LiquidGlassBuilder a(Size size) {
        this.tD = size;
        return this;
    }

    public LiquidGlassBuilder b(RadiusConfig radiusconfig) {
        this.ZV = radiusconfig;
        return this;
    }

    public LiquidGlassBuilder c(ColorPair colorpair) {
        this.Ht = colorpair;
        return this;
    }

    public LiquidGlassBuilder d(float f) {
        this.XN = f;
        return this;
    }

    public LiquidGlassBuilder e(float f) {
        this.RZ = f;
        return this;
    }

    public LiquidGlassBuilder f(float f) {
        this.oi = f;
        return this;
    }

    public LiquidGlassBuilder g(float f) {
        this.arv = f;
        return this;
    }

    public LiquidGlassBuilder h(float f) {
        this.AW = f;
        return this;
    }

    public LiquidGlassBuilder i(float f) {
        this.Zo = f;
        return this;
    }

    public LiquidGlassBuilder j(float f) {
        this.Ah = f;
        return this;
    }

    public LiquidGlassBuilder k(float f) {
        this.Wt = f;
        return this;
    }

    public LiquidGlassBuilder l(float f) {
        this.acy = f;
        return this;
    }

    protected BuiltLiquidGlass m() {
        return new BuiltLiquidGlass(this.tD, this.ZV, this.Ht, this.XN, this.RZ, this.oi, this.arv, this.AW, this.Zo, this.Ah, this.Wt, this.acy);
    }

    @Override
    protected void b() {
        this.tD = Size.kb;
        this.ZV = RadiusConfig.azt;
        this.Ht = ColorPair.gH;
        this.XN = 0.5F;
        this.RZ = 2.0F;
        this.oi = 50.0F;
        this.arv = 0.08F;
        this.AW = 10.0F;
        this.Zo = 1.0F;
        this.Ah = 0.0F;
        this.Wt = 0.0F;
        this.acy = 8.0F;
    }

    protected BuiltLiquidGlass c() {
        return this.m();
    }
}
