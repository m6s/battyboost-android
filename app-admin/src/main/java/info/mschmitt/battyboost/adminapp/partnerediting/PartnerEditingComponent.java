package info.mschmitt.battyboost.adminapp.partnerediting;

import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class PartnerEditingComponent {
    private final BattyboostClient client;
    private final FirebaseDatabase database;
    private final Router router;

    public PartnerEditingComponent(Router router, FirebaseDatabase database, BattyboostClient client) {
        this.client = client;
        this.database = database;
        this.router = router;
    }

    public void inject(PartnerEditingFragment fragment) {
        fragment.client = client;
        fragment.database = database;
        fragment.router = router;
        fragment.injected = true;
    }
}
