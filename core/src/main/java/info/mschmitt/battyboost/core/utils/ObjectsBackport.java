package info.mschmitt.battyboost.core.utils;

/**
 * @author Matthias Schmitt
 */
public class ObjectsBackport {
    public static boolean nonNull(Object obj) {
        return obj != null;
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }
}
