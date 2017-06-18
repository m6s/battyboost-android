package info.mschmitt.battyboost.app;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.app.hub.HubComponent;
import info.mschmitt.battyboost.app.hub.HubFragment;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.utils.firebase.RxAuth;

/**
 * @author Matthias Schmitt
 */
public class BattyboostApplicationComponent {
    public final FirebaseDatabase database;
    public final AuthUI authUI;
    public final BattyboostClient client;
    public final Router router;
    private final RxAuth rxAuth;

    public BattyboostApplicationComponent(BattyboostApplication application) {
        database = FirebaseDatabase.getInstance();
        rxAuth = new RxAuth(FirebaseAuth.getInstance());
        authUI = AuthUI.getInstance();
        client = new BattyboostClient(database, rxAuth);
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
        return new HubComponent(router, database, client, rxAuth, authUI);
    }
}
