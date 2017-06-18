package info.mschmitt.battyboost.core.entities;

import com.google.firebase.auth.FirebaseUser;

/**
 * @author Matthias Schmitt
 */
public class ObservableFirebaseUser {
    public String displayName;

    public ObservableFirebaseUser(FirebaseUser firebaseUser) {
        displayName = firebaseUser.getDisplayName();
    }
}
