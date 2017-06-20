package info.mschmitt.battyboost.core.utils.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import info.mschmitt.battyboost.core.utils.RxOptional;
import io.reactivex.Observable;

/**
 * @author Matthias Schmitt
 */
public class RxAuth {
    public static Observable<RxOptional<FirebaseUser>> userChanges(FirebaseAuth auth) {
        return Observable.create(e -> {
            FirebaseAuth.AuthStateListener listener = firebaseAuth -> e.onNext(new RxOptional<>(auth.getCurrentUser()));
            auth.addAuthStateListener(listener);
            e.setCancellable(() -> auth.removeAuthStateListener(listener));
        });
    }
}
