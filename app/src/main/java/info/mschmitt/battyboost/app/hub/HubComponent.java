package info.mschmitt.battyboost.app.hub;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.app.balance.BalanceComponent;
import info.mschmitt.battyboost.app.balance.BalanceFragment;
import info.mschmitt.battyboost.app.map.MapComponent;
import info.mschmitt.battyboost.app.map.MapFragment;
import info.mschmitt.battyboost.app.schedule.ScheduleComponent;
import info.mschmitt.battyboost.app.schedule.ScheduleFragment;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.utils.firebase.RxAuth;

/**
 * @author Matthias Schmitt
 */
public class HubComponent {
    private final BattyboostClient client;
    private final FirebaseDatabase database;
    private final Router router;
    private final RxAuth rxAuth;
    private final AuthUI authUI;

    public HubComponent(Router router, FirebaseDatabase database, BattyboostClient client, RxAuth rxAuth,
                        AuthUI authUI) {
        this.client = client;
        this.database = database;
        this.router = router;
        this.rxAuth = rxAuth;
        this.authUI = authUI;
    }

    public void inject(HubFragment fragment) {
        fragment.router = router;
        fragment.database = database;
        fragment.client = client;
        fragment.rxAuth = rxAuth;
        fragment.authUI = authUI;
        fragment.component = this;
        fragment.injected = true;
    }

    public MapComponent plus(MapFragment fragment) {
        return new MapComponent(client, database, router);
    }

    public ScheduleComponent plus(ScheduleFragment fragment) {
        return new ScheduleComponent(router, database, client, rxAuth, authUI);
    }

    public BalanceComponent plus(BalanceFragment fragment) {
        return new BalanceComponent(router, database, client, rxAuth, authUI);
    }
}
