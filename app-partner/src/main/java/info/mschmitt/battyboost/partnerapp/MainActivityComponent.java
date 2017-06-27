package info.mschmitt.battyboost.partnerapp;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.partnerapp.cart.CartComponent;
import info.mschmitt.battyboost.partnerapp.cart.CartFragment;
import info.mschmitt.battyboost.partnerapp.transactionlist.TransactionListComponent;
import info.mschmitt.battyboost.partnerapp.transactionlist.TransactionListFragment;

/**
 * @author Matthias Schmitt
 */
public class MainActivityComponent {
    private final Router router;
    private final Cache cache;
    private final FirebaseDatabase database;
    private final BattyboostClient client;
    private final FirebaseAuth auth;
    private final AuthUI authUI;

    public MainActivityComponent(Router router, Cache cache, FirebaseDatabase database, BattyboostClient client,
                                 FirebaseAuth auth, AuthUI authUI) {
        this.router = router;
        this.cache = cache;
        this.database = database;
        this.client = client;
        this.auth = auth;
        this.authUI = authUI;
    }

    public void inject(MainActivity activity) {
        activity.component = this;
        activity.auth = auth;
        activity.client = client;
        activity.router = router;
        activity.cache = cache;
        activity.injected = true;
    }

    public TransactionListComponent plus(TransactionListFragment fragment) {
        return new TransactionListComponent(router, cache, database, client, authUI);
    }

    public CartComponent plus(CartFragment fragment) {
        return new CartComponent(router, cache, client, authUI);
    }
}
