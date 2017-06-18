package info.mschmitt.battyboost.core.utils.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import info.mschmitt.battyboost.core.utils.RxOptional;
import io.reactivex.Observable;

/**
 * @author Matthias Schmitt
 */
public class RxAuth {
    public final FirebaseAuth auth;

    public RxAuth(FirebaseAuth auth) {
        this.auth = auth;
    }

    public Observable<RxOptional<FirebaseUser>> userChanges() {
        return Observable.create(e -> {
            FirebaseAuth.AuthStateListener listener = firebaseAuth -> e.onNext(new RxOptional<>(auth.getCurrentUser()));
            auth.addAuthStateListener(listener);
            e.setCancellable(() -> auth.removeAuthStateListener(listener));
        });
    }
}
