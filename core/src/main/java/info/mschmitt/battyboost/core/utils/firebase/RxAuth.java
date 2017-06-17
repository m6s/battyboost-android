package info.mschmitt.battyboost.core.utils.firebase;

import com.google.firebase.auth.FirebaseAuth;
import io.reactivex.Observable;

/**
 * @author Matthias Schmitt
 */
public class RxAuth {
    public static Observable<Object> stateChanges(FirebaseAuth auth) {
        return Observable.create(e -> {
            FirebaseAuth.AuthStateListener listener = firebaseAuth -> e.onNext(Irrelevant.INSTANCE);
            auth.addAuthStateListener(listener);
            e.setCancellable(() -> auth.removeAuthStateListener(listener));
        });
    }

    private enum Irrelevant {INSTANCE}
}
