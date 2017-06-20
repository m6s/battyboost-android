package info.mschmitt.battyboost.core.entities;

import android.databinding.BaseObservable;
import android.net.Uri;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class AuthUser extends BaseObservable implements Serializable {
    public String email;
    public String photoUrl;
    public String displayName;

    public AuthUser(FirebaseUser firebaseUser) {
        displayName = firebaseUser.getDisplayName();
        email = firebaseUser.getEmail();
        Uri photoUri = firebaseUser.getPhotoUrl();
        photoUrl = photoUri == null ? null : photoUri.toString();
    }

    public static AuthUser of(FirebaseUser firebaseUser) {
        return firebaseUser == null ? null : new AuthUser(firebaseUser);
    }
}
