package info.mschmitt.battyboost.adminapp.posselection;

import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.core.network.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class PosSelectionComponent {
    private final BattyboostClient client;
    private final FirebaseDatabase database;
    private final Router router;

    public PosSelectionComponent(Router router, FirebaseDatabase database, BattyboostClient client) {
        this.client = client;
        this.database = database;
        this.router = router;
    }

    public void inject(PosSelectionFragment fragment) {
        fragment.client = client;
        fragment.database = database;
        fragment.router = router;
        fragment.injected = true;
    }
}
