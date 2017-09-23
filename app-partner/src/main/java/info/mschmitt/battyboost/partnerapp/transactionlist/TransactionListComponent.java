package info.mschmitt.battyboost.partnerapp.transactionlist;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.core.network.BattyboostClient;
import info.mschmitt.battyboost.partnerapp.Cache;
import info.mschmitt.battyboost.partnerapp.Router;

/**
 * @author Matthias Schmitt
 */
public class TransactionListComponent {
    private final Router router;
    private final AuthUI authUI;
    private final Cache cache;
    private final FirebaseDatabase database;
    private final BattyboostClient client;

    public TransactionListComponent(Router router, Cache cache, FirebaseDatabase database, BattyboostClient client,
                                    AuthUI authUI) {
        this.cache = cache;
        this.client = client;
        this.database = database;
        this.router = router;
        this.authUI = authUI;
    }

    public void inject(TransactionListFragment fragment) {
        fragment.router = router;
        fragment.cache = cache;
        fragment.database = database;
        fragment.client = client;
        fragment.authUI = authUI;
        fragment.injected = true;
    }
}
