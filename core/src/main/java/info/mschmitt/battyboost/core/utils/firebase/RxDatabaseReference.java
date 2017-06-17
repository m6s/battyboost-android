package info.mschmitt.battyboost.core.utils.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * @author Matthias Schmitt
 */
public class RxDatabaseReference {
    public static Completable removeValue(DatabaseReference reference) {
        return Completable.create(e -> reference.removeValue((databaseError, databaseReference) -> {
            if (databaseError != null) {
                e.onError(new RxDatabaseError(databaseError));
                return;
            }
            e.onComplete();
        }));
    }

    public static Completable setValue(DatabaseReference reference, Object o) {
        return Completable.create(e -> reference.setValue(o, (databaseError, databaseReference) -> {
            if (databaseError != null) {
                e.onError(new RxDatabaseError(databaseError));
                return;
            }
            e.onComplete();
        }));
    }

    public static Observable<DataSnapshot> valueEvents(DatabaseReference reference) {
        return Observable.create(e -> {
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    e.onNext(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    e.onError(new RxDatabaseError(databaseError));
                }
            };
            reference.addValueEventListener(listener);
            e.setCancellable(() -> reference.removeEventListener(listener));
        });
    }
}
