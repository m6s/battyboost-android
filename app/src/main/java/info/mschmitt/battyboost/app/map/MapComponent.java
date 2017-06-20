package info.mschmitt.battyboost.app.map;

import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class MapComponent {
    private final Router router;
    private final FirebaseDatabase database;
    private final BattyboostClient client;

    public MapComponent(Router router, FirebaseDatabase database, BattyboostClient client) {
        this.router = router;
        this.database = database;
        this.client = client;
    }

    public void inject(MapFragment fragment) {
        fragment.router = router;
        fragment.database = database;
        fragment.client = client;
        fragment.injected = true;
    }
}
