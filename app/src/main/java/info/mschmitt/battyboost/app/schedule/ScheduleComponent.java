package info.mschmitt.battyboost.app.schedule;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class ScheduleComponent {
    private final Router router;
    private final FirebaseDatabase database;
    private final BattyboostClient client;
    private final FirebaseAuth auth;
    private final AuthUI authUI;

    public ScheduleComponent(Router router, FirebaseDatabase database, BattyboostClient client, FirebaseAuth auth,
                             AuthUI authUI) {
        this.router = router;
        this.database = database;
        this.client = client;
        this.auth = auth;
        this.authUI = authUI;
    }

    public void inject(ScheduleFragment fragment) {
        fragment.router = router;
        fragment.database = database;
        fragment.client = client;
        fragment.auth = auth;
        fragment.authUI = authUI;
        fragment.injected = true;
    }
}
