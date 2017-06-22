package info.mschmitt.battyboost.app.photo;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class PhotoComponent {
    private final Router router;
    private final FirebaseDatabase database;
    private final BattyboostClient client;
    private final FirebaseAuth auth;
    private final FirebaseStorage storage;
    private final AuthUI authUI;

    public PhotoComponent(Router router, FirebaseDatabase database, BattyboostClient client, FirebaseAuth auth,
                          FirebaseStorage storage, AuthUI authUI) {
        this.router = router;
        this.database = database;
        this.client = client;
        this.auth = auth;
        this.storage = storage;
        this.authUI = authUI;
    }

    public void inject(PhotoFragment fragment) {
        fragment.router = router;
        fragment.database = database;
        fragment.client = client;
        fragment.auth = auth;
        fragment.storage = storage;
        fragment.authUI = authUI;
        fragment.injected = true;
    }
}
