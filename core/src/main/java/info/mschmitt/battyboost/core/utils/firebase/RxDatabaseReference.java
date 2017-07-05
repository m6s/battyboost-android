package info.mschmitt.battyboost.core.utils.firebase;

import com.google.firebase.database.DatabaseReference;
import io.reactivex.Completable;

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
}
