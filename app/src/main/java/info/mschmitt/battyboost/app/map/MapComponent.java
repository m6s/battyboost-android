package info.mschmitt.battyboost.app.map;

import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class MapComponent {
    private final BattyboostClient client;
    private final FirebaseDatabase database;
    private final Router router;

    public MapComponent(BattyboostClient client, FirebaseDatabase database, Router router) {
        this.client = client;
        this.database = database;
        this.router = router;
    }

    public void inject(MapFragment fragment) {
        fragment.client = client;
        fragment.database = database;
        fragment.injected = true;
    }
}
