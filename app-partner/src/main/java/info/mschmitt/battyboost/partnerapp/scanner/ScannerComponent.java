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
    private final ScannerFragment.OnQrScannedListener onQrScannedListener;
    private final ScannerFragment.OnUpButtonClickListener onUButtonClickListener;

    public ScannerComponent(Router router, Cache cache, BattyboostClient client, AuthUI authUI,
                            ScannerFragment.OnQrScannedListener onQrScannedListener,
                            ScannerFragment.OnUpButtonClickListener onUButtonClickListener) {
        this.router = router;
        this.cache = cache;
        this.client = client;
        this.authUI = authUI;
        this.onQrScannedListener = onQrScannedListener;
        this.onUButtonClickListener = onUButtonClickListener;
    }

    public void inject(ScannerFragment fragment) {
        fragment.onQrScannedListener = onQrScannedListener;
        fragment.onUButtonClickListener = onUButtonClickListener;
        fragment.injected = true;
    }
}
