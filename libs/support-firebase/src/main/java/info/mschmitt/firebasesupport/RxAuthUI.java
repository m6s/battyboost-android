package info.mschmitt.firebasesupport;

import android.support.v4.app.FragmentActivity;
import com.firebase.ui.auth.AuthUI;
import io.reactivex.Completable;

/**
 * @author Matthias Schmitt
 */
public class RxAuthUI {
    public static Completable signOut(AuthUI authUI, FragmentActivity activity) {
        return Completable.create(e -> authUI.signOut(activity)
                .addOnSuccessListener(ignore -> e.onComplete())
                .addOnFailureListener(e::onError));
    }
}
