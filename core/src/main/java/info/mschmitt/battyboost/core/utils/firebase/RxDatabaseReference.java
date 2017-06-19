package info.mschmitt.battyboost.core.utils.firebase;

import com.google.firebase.database.*;
import io.reactivex.Completable;
import io.reactivex.Emitter;
import io.reactivex.Observable;

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

    public static Observable<DataSnapshot> valueEvents(DatabaseReference reference) {
        return Observable.create(e -> {
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    e.onNext(snapshot);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    e.onError(new RxDatabaseError(error));
                }
            };
            reference.addValueEventListener(listener);
            e.setCancellable(() -> reference.removeEventListener(listener));
        });
    }

    public static Observable<DataSnapshot> childAddedEvents(DatabaseReference reference) {
        return Observable.create(e -> {
            ChildEventListener listener = new BaseChildEventListener(e) {
                @Override
                public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                    e.onNext(snapshot);
                }
            };
            reference.addChildEventListener(listener);
            e.setCancellable(() -> reference.removeEventListener(listener));
        });
    }

    public static Observable<DataSnapshot> childChangedEvents(DatabaseReference reference) {
        return Observable.create(e -> {
            ChildEventListener listener = new BaseChildEventListener(e) {
                @Override
                public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                    e.onNext(snapshot);
                }
            };
            reference.addChildEventListener(listener);
            e.setCancellable(() -> reference.removeEventListener(listener));
        });
    }

    public static Observable<DataSnapshot> childRemovedEvents(DatabaseReference reference) {
        return Observable.create(e -> {
            ChildEventListener listener = new BaseChildEventListener(e) {
                @Override
                public void onChildRemoved(DataSnapshot snapshot) {
                    e.onNext(snapshot);
                }
            };
            reference.addChildEventListener(listener);
            e.setCancellable(() -> reference.removeEventListener(listener));
        });
    }

    public static Observable<DataSnapshot> childMovedEvents(DatabaseReference reference) {
        return Observable.create(e -> {
            ChildEventListener listener = new BaseChildEventListener(e) {
                @Override
                public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
                    e.onNext(snapshot);
                }
            };
            reference.addChildEventListener(listener);
            e.setCancellable(() -> reference.removeEventListener(listener));
        });
    }

    private static class BaseChildEventListener implements ChildEventListener {
        private final Emitter e;

        BaseChildEventListener(Emitter e) {
            this.e = e;
        }

        @Override
        public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
        }

        @Override
        public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
        }

        @Override
        public void onChildRemoved(DataSnapshot snapshot) {
        }

        @Override
        public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            e.onError(new RxDatabaseError(databaseError));
        }
    }
}
