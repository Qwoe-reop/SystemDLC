package dev.mark.system.render.builder;

import dev.mark.system.render.BuiltWhiteRectangle;

public final class WhiteRectangleBuilder extends AbstractBuilder<BuiltWhiteRectangle> {
    private static final float[] wL = new float[]{0.12156863F, 0.12156863F, 0.12156863F, 1.0F};
    private static final float[] qm = new float[]{0.09411765F, 0.09411765F, 0.09411765F, 1.0F};
    private static final float[] YY = new float[]{0.12156863F, 0.12156863F, 0.12156863F, 1.0F};
    private static final float[] aqi = new float[]{0.10980392F, 0.10980392F, 0.10980392F, 1.0F};
    private float yr;
    private float PY;
    private float tG = 12.0F;
    private float tP = 0.0F;
    private float ak = 0.0F;
    private float pE = 0.0F;
    private float ik = 1.0F;
    private float aur = 0.008F;
    private float CP = 0.0F;
    private float[] Do = (float[])wL.clone();
    private float[] aqE = (float[])qm.clone();
    private float[] aln = (float[])YY.clone();
    private float[] aaq = (float[])aqi.clone();

    public WhiteRectangleBuilder a(float f, float f1) {
        this.yr = f;
        this.PY = f1;
        return this;
    }

    public WhiteRectangleBuilder b(float f) {
        this.tG = f;
        return this;
    }

    public WhiteRectangleBuilder c(float f) {
        this.tP = f;
        return this;
    }

    public WhiteRectangleBuilder d(float f) {
        this.ak = f;
        return this;
    }

    public WhiteRectangleBuilder e(float f) {
        this.pE = f;
        return this;
    }

    public WhiteRectangleBuilder f(float f) {
        this.ik = f;
        return this;
    }

    public WhiteRectangleBuilder g(float f) {
        this.aur = f;
        return this;
    }

    public WhiteRectangleBuilder h(float f) {
        this.CP = f;
        return this;
    }

    public WhiteRectangleBuilder i(float f, float f1, float f2, float f3) {
        this.Do = new float[]{f, f1, f2, f3};
        return this;
    }

    public WhiteRectangleBuilder j(float f, float f1, float f2, float f3) {
        this.aqE = new float[]{f, f1, f2, f3};
        return this;
    }

    public WhiteRectangleBuilder k(float f, float f1, float f2, float f3) {
        this.aln = new float[]{f, f1, f2, f3};
        return this;
    }

    public WhiteRectangleBuilder l(float f, float f1, float f2, float f3) {
        this.aaq = new float[]{f, f1, f2, f3};
        return this;
    }

    protected BuiltWhiteRectangle m() {
        return new BuiltWhiteRectangle(this.yr, this.PY, this.tG, this.tP, this.ak, this.pE, this.ik, this.aur, this.CP, this.Do, this.aqE, this.aln, this.aaq);
    }

    @Override
    protected void b() {
        this.yr = 0.0F;
        this.PY = 0.0F;
        this.tG = 12.0F;
        this.tP = 6.0F;
        this.ak = 5.0F;
        this.pE = 0.3F;
        this.ik = 1.0F;
        this.aur = 0.008F;
        this.CP = 0.0F;
        this.Do = (float[])wL.clone();
        this.aqE = (float[])qm.clone();
        this.aln = (float[])YY.clone();
        this.aaq = (float[])aqi.clone();
    }

    protected BuiltWhiteRectangle c() {
        return this.m();
    }
}
