package dev.mark.system.util;

import com.google.gson.reflect.TypeToken;
import dev.mark.system.core.Loader;
import java.util.Map;

public class BooleanMapTypeToken extends TypeToken<Map<String, Boolean>> {
    public static void guard() {
    }

    static {
        Loader.init(BooleanMapTypeToken.class);
    }
}
