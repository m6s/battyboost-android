package info.mschmitt.battyboost.partnerapp.guidedrental;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.partnerapp.Cache;
import info.mschmitt.battyboost.partnerapp.Router;

/**
 * @author Matthias Schmitt
 */
public class GuidedRentalComponent {
    private final Router router;
    private final Cache cache;
    private final FirebaseDatabase database;
    private final BattyboostClient client;
    private final AuthUI authUI;

    public GuidedRentalComponent(Router router, Cache cache, FirebaseDatabase database, BattyboostClient client,
                                 AuthUI authUI) {
        this.router = router;
        this.cache = cache;
        this.database = database;
        this.client = client;
        this.authUI = authUI;
    }

    public void inject(GuidedRentalFragment fragment) {
        fragment.router = router;
        fragment.cache = cache;
        fragment.database = database;
        fragment.client = client;
        fragment.authUI = authUI;
        fragment.injected = true;
    }
}
