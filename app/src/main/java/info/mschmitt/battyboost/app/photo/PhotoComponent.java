package info.mschmitt.battyboost.app.photo;

import com.google.firebase.storage.FirebaseStorage;
import info.mschmitt.battyboost.app.Cache;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.core.network.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class PhotoComponent {
    private final Router router;
    private final Cache cache;
    private final BattyboostClient client;
    private final FirebaseStorage storage;

    public PhotoComponent(Router router, Cache cache, BattyboostClient client, FirebaseStorage storage) {
        this.router = router;
        this.cache = cache;
        this.client = client;
        this.storage = storage;
    }

    public void inject(PhotoFragment fragment) {
        fragment.router = router;
        fragment.cache = cache;
        fragment.client = client;
        fragment.storage = storage;
        fragment.injected = true;
    }
}
