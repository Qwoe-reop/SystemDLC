package dev.mark.system.render.builder;

import dev.mark.system.font.MSDFFont;
import dev.mark.system.render.BuiltText;
import java.awt.Color;

public final class TextBuilder extends AbstractBuilder<BuiltText> {
    private MSDFFont Tq;
    private String aiS;
    private float aeL;
    private float RU;
    private int wB;
    private float apT;
    private float ach;
    private int adc;
    private float awt;
    private boolean lY;

    public TextBuilder a(MSDFFont msdffont) {
        this.Tq = msdffont;
        return this;
    }

    public TextBuilder b(String s) {
        this.aiS = s;
        return this;
    }

    public TextBuilder c(float f) {
        this.aeL = f;
        return this;
    }

    public TextBuilder d(float f) {
        this.RU = f;
        return this;
    }

    public TextBuilder e(Color color) {
        return this.f(color.getRGB());
    }

    public TextBuilder f(int i) {
        this.wB = i;
        return this;
    }

    public TextBuilder g(float f) {
        this.apT = f;
        return this;
    }

    public TextBuilder h(float f) {
        this.ach = f;
        return this;
    }

    public TextBuilder i(Color color, float f) {
        return this.j(color.getRGB(), f);
    }

    public TextBuilder j(int i, float f) {
        this.adc = i;
        this.awt = f;
        return this;
    }

    public TextBuilder k(boolean flag) {
        this.lY = flag;
        return this;
    }

    protected BuiltText l() {
        return new BuiltText(this.Tq, this.aiS, this.aeL, this.RU, this.wB, this.apT, this.ach, this.adc, this.awt, this.lY);
    }

    @Override
    protected void b() {
        this.Tq = null;
        this.aiS = "";
        this.aeL = 0.0F;
        this.RU = 0.05F;
        this.wB = -1;
        this.apT = 0.5F;
        this.ach = 0.0F;
        this.adc = 0;
        this.awt = 0.0F;
        this.lY = true;
    }

    protected BuiltText c() {
        return this.l();
    }
}
