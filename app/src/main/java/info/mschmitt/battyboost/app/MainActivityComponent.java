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
    private final MainActivity activity;
    private final Router router;
    private final FirebaseDatabase database;
    private final BattyboostClient client;
    private final FirebaseAuth auth;
    private final FirebaseStorage storage;
    private final AuthUI authUI;

    public MainActivityComponent(MainActivity activity, Router router, FirebaseDatabase database,
                                 BattyboostClient client,
                                 FirebaseAuth auth, FirebaseStorage storage, AuthUI authUI) {
        this.activity = activity;
        this.router = router;
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
        activity.injected = true;
    }

    public HubComponent plus(HubFragment fragment) {
        return new HubComponent(router, activity.cache, database, client, auth, authUI);
    }

    public SettingsComponent plus(SettingsFragment fragment) {
        return new SettingsComponent(router, activity.cache, client, authUI);
    }

    public PhotoComponent plus(PhotoFragment fragment) {
        return new PhotoComponent(router, activity.cache, client, storage);
    }
}
