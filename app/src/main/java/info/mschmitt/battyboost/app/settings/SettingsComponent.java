package info.mschmitt.battyboost.app.settings;

import com.firebase.ui.auth.AuthUI;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.app.Store;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class SettingsComponent {
    private final Router router;
    private final Store store;
    private final BattyboostClient client;
    private final AuthUI authUI;

    public SettingsComponent(Router router, Store store, BattyboostClient client, AuthUI authUI) {
        this.router = router;
        this.store = store;
        this.client = client;
        this.authUI = authUI;
    }

    public void inject(SettingsFragment fragment) {
        fragment.router = router;
        fragment.store = store;
        fragment.client = client;
        fragment.authUI = authUI;
        fragment.injected = true;
    }
}
