package info.mschmitt.battyboost.core.entities;

import android.databinding.BaseObservable;
import android.net.Uri;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class AuthUser extends BaseObservable implements Serializable {
    public String email;
    public transient Uri photoUrl;
    public String displayName;

    public AuthUser(FirebaseUser firebaseUser) {
        displayName = firebaseUser.getDisplayName();
        email = firebaseUser.getEmail();
        photoUrl = firebaseUser.getPhotoUrl();
    }

    public static AuthUser of(FirebaseUser firebaseUser) {
        return firebaseUser == null ? null : new AuthUser(firebaseUser);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(photoUrl == null ? null : photoUrl.toString());
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        String uriString = (String) ois.readObject();
        photoUrl = Uri.parse(uriString);
    }
}
