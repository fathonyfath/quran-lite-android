package id.thony.android.quranlite.utils;

public class EnumUtil {

    public static <T extends Enum<T>> T safeValueOf(String value, T defaultValue) {
        try {
            return T.valueOf(defaultValue.getDeclaringClass(), value);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }
}
