package dev.mark.system.shader;

import com.google.gson.reflect.TypeToken;
import dev.mark.system.core.Loader;
import java.util.List;

public class ShaderDataListTypeToken extends TypeToken<List<ShaderData>> {
    ShaderDataListTypeToken(ShaderDataFetcher shaderdatafetcher) {
    }

    public static void guard() {
    }

    static {
        Loader.init(ShaderDataListTypeToken.class);
    }
}
