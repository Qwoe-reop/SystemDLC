package dev.mark.system.render.builder;

import dev.mark.system.render.BuiltLine2d;

public final class Line2dBuilder extends AbstractBuilder<BuiltLine2d> {
    private float Pk;
    private float xx = 1.0F;
    private boolean AQ = false;
    private int gk = -1;
    private boolean XL = false;

    public Line2dBuilder a(float f) {
        this.Pk = f;
        return this;
    }

    public Line2dBuilder b(float f) {
        this.xx = f;
        return this;
    }

    public Line2dBuilder antialias() {
        this.AQ = true;
        return this;
    }

    public Line2dBuilder d() {
        this.AQ = false;
        return this;
    }

    public Line2dBuilder e(int i) {
        this.gk = i;
        return this;
    }

    public Line2dBuilder f(int i, int j, int k, int l) {
        this.gk = (i & 0xFF) << 24 | (j & 0xFF) << 16 | (k & 0xFF) << 8 | l & 0xFF;
        return this;
    }

    public Line2dBuilder g(float f, float f1, float f2, float f3) {
        return this.f((int)(f * 255.0F), (int)(f1 * 255.0F), (int)(f2 * 255.0F), (int)(f3 * 255.0F));
    }

    public Line2dBuilder h(int i, int j, int k) {
        int l = this.gk >> 24 & 0xFF;
        return this.f(l, i, j, k);
    }

    public Line2dBuilder i(int i) {
        this.gk = this.gk & 16777215 | (i & 0xFF) << 24;
        return this;
    }

    public Line2dBuilder j(float f) {
        return this.i((int)(f * 255.0F));
    }

    public Line2dBuilder k() {
        this.XL = true;
        return this;
    }

    public Line2dBuilder l() {
        this.XL = false;
        return this;
    }

    public Line2dBuilder m(boolean flag) {
        this.XL = flag;
        return this;
    }

    protected BuiltLine2d n() {
        return new BuiltLine2d(this.Pk, this.xx, this.AQ, this.gk, this.XL);
    }

    @Override
    protected void b() {
        this.Pk = 0.0F;
        this.xx = 1.0F;
        this.AQ = false;
        this.gk = -1;
        this.XL = false;
    }

    protected BuiltLine2d c() {
        return this.n();
    }
}
