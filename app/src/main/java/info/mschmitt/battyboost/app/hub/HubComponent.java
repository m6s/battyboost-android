package info.mschmitt.battyboost.app.hub;

import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.app.map.MapComponent;
import info.mschmitt.battyboost.app.map.MapFragment;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class HubComponent {
    private final BattyboostClient client;
    private final FirebaseDatabase database;
    private final Router router;

    public HubComponent(BattyboostClient client, FirebaseDatabase database, Router router) {
        this.client = client;
        this.database = database;
        this.router = router;
    }

    public void inject(HubFragment fragment) {
        fragment.client = client;
        fragment.database = database;
        fragment.router = router;
        fragment.component = this;
        fragment.injected = true;
    }

    public MapComponent plus(MapFragment fragment) {
        return new MapComponent(client, database, router);
    }
}
