package info.mschmitt.battyboost.adminapp.transactionlist;

import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.core.network.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class TransactionListComponent {
    private final BattyboostClient client;
    private final FirebaseDatabase database;
    private final Router router;

    public TransactionListComponent(Router router, FirebaseDatabase database, BattyboostClient client) {
        this.client = client;
        this.database = database;
        this.router = router;
    }

    public void inject(TransactionListFragment fragment) {
        fragment.client = client;
        fragment.database = database;
        fragment.router = router;
        fragment.injected = true;
    }
}
