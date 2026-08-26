package dev.mark.system.render;

import dev.mark.system.font.MSDFFont;
import dev.mark.system.render.builder.TextBuilder;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class TextCache {
    private static final Map<TextCacheKey, BuiltText> gb = new HashMap<>();
    private static final int di = 1000;
    private static final TextCacheKey as = new TextCacheKey();

    public static BuiltText a(MSDFFont msdffont, String s, float f, Color color) {
        as.a(msdffont, s, f, color.getRGB());
        BuiltText builttext = gb.get(as);
        if (builttext != null) {
            return builttext;
        }

        if (gb.size() > 1000) {
            gb.clear();
        }

        TextCacheKey TextCacheKey = new TextCacheKey(msdffont, s, f, color.getRGB());
        BuiltText builttext1 = new TextBuilder().a(msdffont).b(s).c(f).d(0.0F).e(color).g(0.4F).h(-0.3F).a();
        gb.put(TextCacheKey, builttext1);
        return builttext1;
    }
}
