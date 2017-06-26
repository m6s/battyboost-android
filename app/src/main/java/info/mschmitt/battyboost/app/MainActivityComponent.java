package info.mschmitt.battyboost.app;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import info.mschmitt.battyboost.app.hub.HubComponent;
import info.mschmitt.battyboost.app.hub.HubFragment;
import info.mschmitt.battyboost.app.photo.PhotoComponent;
import info.mschmitt.battyboost.app.photo.PhotoFragment;
import info.mschmitt.battyboost.app.settings.SettingsComponent;
import info.mschmitt.battyboost.app.settings.SettingsFragment;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class MainActivityComponent {
    private final Router router;
    private final Store store;
    private final FirebaseDatabase database;
    private final BattyboostClient client;
    private final FirebaseAuth auth;
    private final FirebaseStorage storage;
    private final AuthUI authUI;

    public MainActivityComponent(Router router, Store store, FirebaseDatabase database, BattyboostClient client,
                                 FirebaseAuth auth, FirebaseStorage storage, AuthUI authUI) {
        this.router = router;
        this.store = store;
        this.database = database;
        this.client = client;
        this.auth = auth;
        this.storage = storage;
        this.authUI = authUI;
    }

    public void inject(MainActivity activity) {
        activity.component = this;
        activity.auth = auth;
        activity.client = client;
        activity.router = router;
        activity.store = store;
        activity.injected = true;
    }

    public HubComponent plus(HubFragment fragment) {
        return new HubComponent(router, store, database, client, auth, authUI);
    }

    public SettingsComponent plus(SettingsFragment fragment) {
        return new SettingsComponent(router, store, authUI);
    }

    public PhotoComponent plus(PhotoFragment fragment) {
        return new PhotoComponent(router, store, client, storage);
    }
}
