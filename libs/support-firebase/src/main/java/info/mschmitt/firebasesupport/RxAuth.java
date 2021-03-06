package info.mschmitt.firebasesupport;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import info.mschmitt.androidsupport.RxOptional;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

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

    public static Completable updateProfile(FirebaseAuth auth, UserProfileChangeRequest request) {
        return Completable.create(e -> auth.getCurrentUser()
                .updateProfile(request)
                .addOnSuccessListener(ignore -> e.onComplete())
                .addOnFailureListener(e::onError));
    }

    public static Completable updateEmail(FirebaseAuth auth, String email) {
        return Completable.create(e -> auth.getCurrentUser()
                .updateEmail(email)
                .addOnSuccessListener(ignore -> e.onComplete()).addOnFailureListener(e::onError));
    }

    public static Single<AuthResult> signInWithEmailAndPassword(FirebaseAuth auth, String email, String password) {
        return Single.create(e -> auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(e::onSuccess)
                .addOnFailureListener(e::onError));
    }
}
