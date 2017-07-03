package info.mschmitt.battyboost.partnerapp;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class BattyboostPartnerApplicationComponent {
    public final FirebaseDatabase database;
    public final FirebaseAuth auth;
    public final AuthUI authUI;
    public final BattyboostClient client;
    public final FirebaseStorage storage;
    public final Router router;

    public BattyboostPartnerApplicationComponent(BattyboostPartnerApplication application) {
        router = new Router();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        authUI = AuthUI.getInstance();
        storage = FirebaseStorage.getInstance();
        client = new BattyboostClient(database, auth, storage);
    }

    public void inject(BattyboostPartnerApplication application) {
        application.applicationComponent = this;
        application.injected = true;
    }

    public MainActivityComponent plus(MainActivity activity) {
        return new MainActivityComponent(activity, router, database, client, auth, authUI);
    }
}
