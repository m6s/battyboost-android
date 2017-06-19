package info.mschmitt.battyboost.adminapp.user;

import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class UserComponent {
    private final BattyboostClient client;
    private final FirebaseDatabase database;
    private final Router router;

    public UserComponent(Router router, FirebaseDatabase database, BattyboostClient client) {
        this.client = client;
        this.database = database;
        this.router = router;
    }

    public void inject(UserFragment fragment) {
        fragment.client = client;
        fragment.database = database;
        fragment.router = router;
        fragment.injected = true;
    }
}
