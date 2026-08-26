package dev.mark.system.render;

import dev.mark.system.font.MSDFFont;
import java.util.Objects;

public class TextCacheKey {
    private int UI;
    private String C;
    private float RB;
    private int CR;

    TextCacheKey() {
    }

    TextCacheKey(MSDFFont msdffont, String s, float f, int i) {
        this.UI = msdffont.hashCode();
        this.C = s;
        this.RB = f;
        this.CR = i;
    }

    void a(MSDFFont msdffont, String s, float f, int i) {
        this.UI = msdffont.hashCode();
        this.C = s;
        this.RB = f;
        this.CR = i;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else {
            return object instanceof TextCacheKey TextCacheKey
                ? this.UI == TextCacheKey.UI
                    && Float.compare(TextCacheKey.RB, this.RB) == 0
                    && this.CR == TextCacheKey.CR
                    && Objects.equals(this.C, TextCacheKey.C)
                : false;
        }
    }

    @Override
    public int hashCode() {
        int i = this.UI;
        i = 31 * i + (this.C != null ? this.C.hashCode() : 0);
        i = 31 * i + Float.floatToIntBits(this.RB);
        return 31 * i + this.CR;
    }
}
