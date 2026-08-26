package dev.mark.system.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

public class LruStringListCache extends LinkedHashMap<String, List<String>> {
    public LruStringListCache(int i, float f, boolean flag) {
        super(i, f, flag);
    }

    @Override
    protected boolean removeEldestEntry(Entry<String, List<String>> entry) {
        return this.size() > 128;
    }
}
