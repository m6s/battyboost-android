package info.mschmitt.firebasesupport;

import com.google.firebase.database.*;
import info.mschmitt.androidsupport.RxOptional;
import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * @author Matthias Schmitt
 */
public class RxQuery {
    public static final Function<DataSnapshot, RxOptional<DataSnapshot>> FIRST_CHILD_MAPPER =
            dataSnapshot -> dataSnapshot.hasChildren() ? new RxOptional<>(dataSnapshot.getChildren().iterator().next())
                    : RxOptional.empty();

    public static Observable<DataSnapshot> valueEvents(Query query) {
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
            query.addValueEventListener(listener);
            e.setCancellable(() -> query.removeEventListener(listener));
        });
    }

    public static Observable<DataSnapshot> childAddedEvents(Query query) {
        return Observable.create(e -> {
            ChildEventListener listener = new BaseChildEventListener(e) {
                @Override
                public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                    e.onNext(snapshot);
                }
            };
            query.addChildEventListener(listener);
            e.setCancellable(() -> query.removeEventListener(listener));
        });
    }

    public static Observable<DataSnapshot> childChangedEvents(Query query) {
        return Observable.create(e -> {
            ChildEventListener listener = new BaseChildEventListener(e) {
                @Override
                public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                    e.onNext(snapshot);
                }
            };
            query.addChildEventListener(listener);
            e.setCancellable(() -> query.removeEventListener(listener));
        });
    }

    public static Observable<DataSnapshot> childRemovedEvents(Query query) {
        return Observable.create(e -> {
            ChildEventListener listener = new BaseChildEventListener(e) {
                @Override
                public void onChildRemoved(DataSnapshot snapshot) {
                    e.onNext(snapshot);
                }
            };
            query.addChildEventListener(listener);
            e.setCancellable(() -> query.removeEventListener(listener));
        });
    }

    public static Observable<DataSnapshot> childMovedEvents(Query query) {
        return Observable.create(e -> {
            ChildEventListener listener = new BaseChildEventListener(e) {
                @Override
                public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
                    e.onNext(snapshot);
                }
            };
            query.addChildEventListener(listener);
            e.setCancellable(() -> query.removeEventListener(listener));
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
