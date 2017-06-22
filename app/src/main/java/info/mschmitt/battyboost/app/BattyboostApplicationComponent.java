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
public class BattyboostApplicationComponent {
    public final Router router;
    public final FirebaseDatabase database;
    public final BattyboostClient client;
    public final FirebaseAuth auth;
    public final FirebaseStorage storage;
    public final AuthUI authUI;

    public BattyboostApplicationComponent(BattyboostApplication application) {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        authUI = AuthUI.getInstance();
        client = new BattyboostClient(database, auth);
        router = new Router();
    }

    public void inject(BattyboostApplication application) {
        application.applicationComponent = this;
        application.injected = true;
    }

    public MainActivityComponent plus(MainActivity activity) {
        return new MainActivityComponent(this);
    }

    public HubComponent plus(HubFragment fragment) {
        return new HubComponent(router, database, client, auth, authUI);
    }

    public SettingsComponent plus(SettingsFragment fragment) {
        return new SettingsComponent(router, database, client, auth, storage, authUI);
    }

    public PhotoComponent plus(PhotoFragment fragment) {
        return new PhotoComponent(router, database, client, auth, storage, authUI);
    }
}
