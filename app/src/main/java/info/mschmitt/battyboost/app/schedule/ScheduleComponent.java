package info.mschmitt.battyboost.app.schedule;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.utils.firebase.RxAuth;

/**
 * @author Matthias Schmitt
 */
public class ScheduleComponent {
    private final Router router;
    private final FirebaseDatabase database;
    private final BattyboostClient client;
    private final RxAuth rxAuth;
    private final AuthUI authUI;

    public ScheduleComponent(Router router, FirebaseDatabase database, BattyboostClient client, RxAuth rxAuth,
                             AuthUI authUI) {
        this.router = router;
        this.database = database;
        this.client = client;
        this.rxAuth = rxAuth;
        this.authUI = authUI;
    }

    public void inject(ScheduleFragment fragment) {
        fragment.database = database;
        fragment.client = client;
        fragment.rxAuth = rxAuth;
        fragment.authUI = authUI;
        fragment.injected = true;
        fragment.injected = true;
    }
}
