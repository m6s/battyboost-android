package info.mschmitt.battyboost.app.settings;

import com.firebase.ui.auth.AuthUI;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.app.Store;

/**
 * @author Matthias Schmitt
 */
public class SettingsComponent {
    private final Router router;
    private final Store store;
    private final AuthUI authUI;

    public SettingsComponent(Router router, Store store, AuthUI authUI) {
        this.router = router;
        this.store = store;
        this.authUI = authUI;
    }

    public void inject(SettingsFragment fragment) {
        fragment.router = router;
        fragment.store = store;
        fragment.authUI = authUI;
        fragment.injected = true;
    }
}
