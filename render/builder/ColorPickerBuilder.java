package dev.mark.system.render.builder;

import dev.mark.system.gui.enums.IndexedTriState;
import dev.mark.system.render.BuiltColorPicker;

public final class ColorPickerBuilder extends AbstractBuilder<BuiltColorPicker> {
    private float ni;
    private float zb;
    private IndexedTriState qn = IndexedTriState.azP;
    private float EO = 0.0F;
    private float yy = 1.0F;
    private float BY = 1.0F;
    private float vW = 8.0F;

    public ColorPickerBuilder a(float f, float f1) {
        this.ni = f;
        this.zb = f1;
        return this;
    }

    public ColorPickerBuilder b(IndexedTriState IndexedTriState) {
        this.qn = IndexedTriState;
        return this;
    }

    public ColorPickerBuilder c(float f) {
        this.EO = f;
        return this;
    }

    public ColorPickerBuilder d(float f) {
        this.yy = f;
        return this;
    }

    public ColorPickerBuilder e(float f) {
        this.BY = f;
        return this;
    }

    public ColorPickerBuilder f(float f) {
        this.vW = f;
        return this;
    }

    protected BuiltColorPicker g() {
        return new BuiltColorPicker(this.ni, this.zb, this.qn, this.EO, this.yy, this.BY, this.vW);
    }

    @Override
    protected void b() {
        this.ni = 0.0F;
        this.zb = 0.0F;
        this.qn = IndexedTriState.azP;
        this.EO = 0.0F;
        this.yy = 1.0F;
        this.BY = 1.0F;
        this.vW = 8.0F;
    }

    protected BuiltColorPicker c() {
        return this.g();
    }
}
