package info.mschmitt.battyboost.partnerapp.scanner;

import com.firebase.ui.auth.AuthUI;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.partnerapp.Cache;
import info.mschmitt.battyboost.partnerapp.Router;

/**
 * @author Matthias Schmitt
 */
public class ScannerComponent {
    private final Router router;
    private final Cache cache;
    private final BattyboostClient client;
    private final AuthUI authUI;

    public ScannerComponent(Router router, Cache cache, BattyboostClient client, AuthUI authUI) {
        this.router = router;
        this.cache = cache;
        this.client = client;
        this.authUI = authUI;
    }

    public void inject(ScannerFragment fragment) {
        fragment.router = router;
        fragment.injected = true;
    }
}
