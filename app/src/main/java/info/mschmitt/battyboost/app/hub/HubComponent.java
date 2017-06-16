package info.mschmitt.battyboost.app.hub;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.app.map.MapComponent;
import info.mschmitt.battyboost.app.map.MapFragment;
import info.mschmitt.battyboost.app.profile.ProfileComponent;
import info.mschmitt.battyboost.app.profile.ProfileFragment;
import info.mschmitt.battyboost.app.schedule.ScheduleComponent;
import info.mschmitt.battyboost.app.schedule.ScheduleFragment;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class HubComponent {
    private final BattyboostClient client;
    private final FirebaseDatabase database;
    private final Router router;
    private final FirebaseAuth auth;
    private final AuthUI authUI;

    public HubComponent(Router router, FirebaseDatabase database, BattyboostClient client, FirebaseAuth auth,
                        AuthUI authUI) {
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
        return new MapComponent(client, database, router);
    }

    public ScheduleComponent plus(ScheduleFragment fragment) {
        return new ScheduleComponent(router, database, client, auth, authUI);
    }

    public ProfileComponent plus(ProfileFragment fragment) {
        return new ProfileComponent(router, database, client, auth, authUI);
    }
}
