package info.mschmitt.battyboost.app.photo;

import com.google.firebase.storage.FirebaseStorage;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.app.Store;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class PhotoComponent {
    private final Router router;
    private final Store store;
    private final BattyboostClient client;
    private final FirebaseStorage storage;

    public PhotoComponent(Router router, Store store, BattyboostClient client, FirebaseStorage storage) {
        this.router = router;
        this.store = store;
        this.client = client;
        this.storage = storage;
    }

    public void inject(PhotoFragment fragment) {
        fragment.router = router;
        fragment.store = store;
        fragment.client = client;
        fragment.storage = storage;
        fragment.injected = true;
    }
}
