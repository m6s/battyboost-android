package info.mschmitt.battyboost.app;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.app.hub.HubComponent;
import info.mschmitt.battyboost.app.hub.HubFragment;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class BattyboostApplicationComponent {
    public final FirebaseDatabase database;
    public final FirebaseAuth auth;
    public final AuthUI authUI;
    public final BattyboostClient client;
    public final Router router;

    public BattyboostApplicationComponent(BattyboostApplication application) {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
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
}
