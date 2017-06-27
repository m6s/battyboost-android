package info.mschmitt.battyboost.app.balance;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.app.Cache;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class BalanceComponent {
    private final Router router;
    private final Cache cache;
    private final FirebaseDatabase database;
    private final BattyboostClient client;
    private final FirebaseAuth auth;
    private final AuthUI authUI;

    public BalanceComponent(Router router, Cache cache, FirebaseDatabase database, BattyboostClient client,
                            FirebaseAuth auth,
                            AuthUI authUI) {
        this.router = router;
        this.cache = cache;
        this.database = database;
        this.client = client;
        this.auth = auth;
        this.authUI = authUI;
    }

    public void inject(BalanceFragment fragment) {
        fragment.router = router;
        fragment.database = database;
        fragment.client = client;
        fragment.auth = auth;
        fragment.authUI = authUI;
        fragment.cache = cache;
        fragment.injected = true;
    }
}
