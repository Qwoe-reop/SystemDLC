package dev.mark.system.util;

import dev.mark.system.core.Loader;
import java.nio.charset.StandardCharsets;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesCipherUtil {
    private static String ALGORITHM = AesCipherUtil.eUrDut4TMnaMUyye[3];
    private static String KEY = AesCipherUtil.eUrDut4TMnaMUyye[0];
    private static String IV = AesCipherUtil.eUrDut4TMnaMUyye[2];
    private static final SecretKeySpec KEY_SPEC = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), AesCipherUtil.eUrDut4TMnaMUyye[1]);
    private static final IvParameterSpec IV_SPEC = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
    private static String[] eUrDut4TMnaMUyye = new String[4];

    public static String a(String s) {
        return null;
    }

    public static String b(String s) {
        return null;
    }

    private static String c(char[] achar, long i, int j) {
        return null;
    }

    private static void d() {
    }

    public static void guard() {
    }

    static {
        Loader.init(AesCipherUtil.class);
        d();
    }
}
