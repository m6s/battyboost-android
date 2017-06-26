package info.mschmitt.battyboost.app;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
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
    private final Store store;

    public BattyboostApplicationComponent(BattyboostApplication application) {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        authUI = AuthUI.getInstance();
        client = new BattyboostClient(database, auth, storage);
        router = new Router();
        store = new Store(client);
    }

    public void inject(BattyboostApplication application) {
        application.applicationComponent = this;
        application.injected = true;
    }

    public MainActivityComponent plus(MainActivity activity) {
        return new MainActivityComponent(router, store, database, client, auth, storage, authUI);
    }
}
