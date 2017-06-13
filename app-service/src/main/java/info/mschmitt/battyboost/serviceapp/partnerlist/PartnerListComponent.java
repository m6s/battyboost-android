package info.mschmitt.battyboost.serviceapp.partnerlist;

import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.serviceapp.Router;

/**
 * @author Matthias Schmitt
 */
public class PartnerListComponent {
    private final BattyboostClient client;
    private final FirebaseDatabase database;
    private final Router router;

    public PartnerListComponent(Router router, FirebaseDatabase database, BattyboostClient client) {
        this.client = client;
        this.database = database;
        this.router = router;
    }

    public void inject(PartnerListFragment fragment) {
        fragment.client = client;
        fragment.database = database;
        fragment.router = router;
        fragment.injected = true;
    }
}
