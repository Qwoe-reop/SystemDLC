package dev.mark.system.render;

import dev.mark.system.render.builder.TextureBuilder;

public final class TextureBuilderSingleton {
    private static final TextureBuilder NV = new TextureBuilder();

    public static TextureBuilder a() {
        return NV;
    }
}
