package info.mschmitt.battyboost.core.utils;

import android.support.annotation.NonNull;

import java.util.function.Function;

/**
 * @author Matthias Schmitt
 */
public class RxOptional<T> {
    private static final RxOptional<?> EMPTY = new RxOptional<>(null);
    public final T value;

    public RxOptional(T value) {
        this.value = value;
    }

    public <U> RxOptional<U> map(Function<? super T, ? extends U> mapper) {
        if (value == null) {
            return empty();
        } else {
            return new RxOptional<>(mapper.apply(value));
        }
    }

    @NonNull
    public static <T> RxOptional<T> empty() {
        //noinspection unchecked
        return (RxOptional<T>) EMPTY;
    }
}
