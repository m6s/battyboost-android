package info.mschmitt.battyboost.app.hub;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.app.Cache;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.app.balance.BalanceComponent;
import info.mschmitt.battyboost.app.balance.BalanceFragment;
import info.mschmitt.battyboost.app.map.MapComponent;
import info.mschmitt.battyboost.app.map.MapFragment;
import info.mschmitt.battyboost.app.schedule.ScheduleComponent;
import info.mschmitt.battyboost.app.schedule.ScheduleFragment;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class HubComponent {
    private final Cache cache;
    private final BattyboostClient client;
    private final FirebaseDatabase database;
    private final Router router;
    private final FirebaseAuth auth;
    private final AuthUI authUI;

    public HubComponent(Router router, Cache cache, FirebaseDatabase database, BattyboostClient client,
                        FirebaseAuth auth,
                        AuthUI authUI) {
        this.cache = cache;
        this.client = client;
        this.database = database;
        this.router = router;
        this.auth = auth;
        this.authUI = authUI;
    }

    public void inject(HubFragment fragment) {
        fragment.router = router;
        fragment.database = database;
        fragment.client = client;
        fragment.auth = auth;
        fragment.authUI = authUI;
        fragment.component = this;
        fragment.injected = true;
    }

    public MapComponent plus(MapFragment fragment) {
        return new MapComponent(router, database, client);
    }

    public ScheduleComponent plus(ScheduleFragment fragment) {
        return new ScheduleComponent(router, cache, database, client, auth, authUI);
    }

    public BalanceComponent plus(BalanceFragment fragment) {
        return new BalanceComponent(router, cache, database, client, auth, authUI);
    }
}
