package info.mschmitt.battyboost.adminapp.partner;

import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.core.network.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class PartnerComponent {
    private final BattyboostClient client;
    private final FirebaseDatabase database;
    private final Router router;

    public PartnerComponent(Router router, FirebaseDatabase database, BattyboostClient client) {
        this.client = client;
        this.database = database;
        this.router = router;
    }

    public void inject(PartnerFragment fragment) {
        fragment.client = client;
        fragment.database = database;
        fragment.router = router;
        fragment.injected = true;
    }
}
