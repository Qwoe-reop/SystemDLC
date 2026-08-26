package dev.mark.system.render.builder;

import dev.mark.system.render.BuiltBlur;
import dev.mark.system.render.ColorPair;
import dev.mark.system.render.RadiusConfig;
import dev.mark.system.render.Size;

public final class BlurBuilder extends AbstractBuilder<BuiltBlur> {
    private Size cr;
    private RadiusConfig amJ;
    private ColorPair HX;
    private float Ha;
    private float OO;

    public BlurBuilder a(Size size) {
        this.cr = size;
        return this;
    }

    public BlurBuilder b(RadiusConfig radiusconfig) {
        this.amJ = radiusconfig;
        return this;
    }

    public BlurBuilder c(ColorPair colorpair) {
        this.HX = colorpair;
        return this;
    }

    public BlurBuilder d(float f) {
        this.Ha = f;
        return this;
    }

    public BlurBuilder e(float f) {
        this.OO = f;
        return this;
    }

    protected BuiltBlur f() {
        return new BuiltBlur(this.cr, this.amJ, this.HX, this.Ha, this.OO);
    }

    @Override
    protected void b() {
        this.cr = Size.kb;
        this.amJ = RadiusConfig.azt;
        this.HX = ColorPair.gH;
        this.Ha = 1.0F;
        this.OO = 0.0F;
    }

    protected BuiltBlur c() {
        return this.f();
    }
}
