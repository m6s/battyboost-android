package info.mschmitt.battyboost.partnerapp.batteryscanner;

import com.firebase.ui.auth.AuthUI;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.partnerapp.Cache;
import info.mschmitt.battyboost.partnerapp.Router;

/**
 * @author Matthias Schmitt
 */
public class BatteryScannerComponent {
    private final Router router;
    private final Cache cache;
    private final BattyboostClient client;
    private final AuthUI authUI;

    public BatteryScannerComponent(Router router, Cache cache, BattyboostClient client, AuthUI authUI) {
        this.router = router;
        this.cache = cache;
        this.client = client;
        this.authUI = authUI;
    }

    public void inject(BatteryScannerFragment fragment) {
        fragment.router = router;
        fragment.cache = cache;
        fragment.client = client;
        fragment.authUI = authUI;
        fragment.injected = true;
    }
}
