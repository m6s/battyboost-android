package info.mschmitt.firebasesupport;

import com.google.firebase.database.DatabaseReference;
import io.reactivex.Completable;

import java.util.Map;

/**
 * @author Matthias Schmitt
 */
public class RxDatabaseReference {
    public static Completable removeValue(DatabaseReference reference) {
        return Completable.create(e -> reference.removeValue((error, ignore) -> {
            if (error != null) {
                e.onError(new RxDatabaseError(error));
                return;
            }
            e.onComplete();
        }));
    }

    public static Completable setValue(DatabaseReference reference, Object o) {
        return Completable.create(e -> reference.setValue(o, (databaseError, ignore) -> {
            if (databaseError != null) {
                e.onError(new RxDatabaseError(databaseError));
                return;
            }
            e.onComplete();
        }));
    }

    public static Completable updateChildren(DatabaseReference reference, Map<String, Object> updateMap) {
        return Completable.create(e -> reference.updateChildren(updateMap, (databaseError, ignore) -> {
            if (databaseError != null) {
                e.onError(new RxDatabaseError(databaseError));
                return;
            }
            e.onComplete();
        }));
    }
}
