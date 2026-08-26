package dev.mark.system.util;

public class DefaultValueProvider {
    private final int QQ;
    private final Object aii;

    public DefaultValueProvider(int i, Object object) {
        this.QQ = i;
        this.aii = object;
    }

    public static DefaultValueProvider a(Object object) {
        return new DefaultValueProvider(0, object);
    }

    public static DefaultValueProvider b() {
        return new DefaultValueProvider(1, null);
    }

    public static DefaultValueProvider c() {
        return new DefaultValueProvider(2, null);
    }

    public static DefaultValueProvider d() {
        return new DefaultValueProvider(3, null);
    }

    public Object e(Object object) {
        if (this.QQ == 1) {
            return object;
        } else if (this.QQ == 2) {
            return false;
        } else {
            return this.QQ == 3 ? 0 : this.aii;
        }
    }
}
