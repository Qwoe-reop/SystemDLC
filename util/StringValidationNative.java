package dev.mark.system.util;

import dev.mark.system.core.Loader;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;

public class StringValidationNative {
    private static final long PH = 104857600L;
    private static String[] Zz = new String[9];

    public static boolean a(String s) {
        return false;
    }

    public static char b(String s) {
        return '\u0000';
    }

    public static void c(String s) {
    }

    private static CallSite d(Lookup lookup, String s, MethodType methodtype, String s1) {
        return null;
    }

    private static String e(char[] achar, long i, int j) {
        return null;
    }

    private static void f() {
    }

    public static void guard() {
    }

    static {
        Loader.init(StringValidationNative.class);
        f();
    }
}
