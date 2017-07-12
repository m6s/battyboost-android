package info.mschmitt.battyboost.partnerapp;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.partnerapp.rentalactions.RentalActionsComponent;
import info.mschmitt.battyboost.partnerapp.rentalactions.RentalActionsFragment;
import info.mschmitt.battyboost.partnerapp.scanner.ScannerComponent;
import info.mschmitt.battyboost.partnerapp.scanner.ScannerFragment;
import info.mschmitt.battyboost.partnerapp.transactionlist.TransactionListComponent;
import info.mschmitt.battyboost.partnerapp.transactionlist.TransactionListFragment;

/**
 * @author Matthias Schmitt
 */
public class MainActivityComponent {
    private final MainActivity activity;
    private final Router router;
    private final FirebaseDatabase database;
    private final BattyboostClient client;
    private final FirebaseAuth auth;
    private final AuthUI authUI;

    public MainActivityComponent(MainActivity activity, Router router, FirebaseDatabase database,
                                 BattyboostClient client, FirebaseAuth auth, AuthUI authUI) {
        this.activity = activity;
        this.router = router;
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
        activity.injected = true;
    }

    public TransactionListComponent plus(TransactionListFragment fragment) {
        return new TransactionListComponent(router, activity.cache, database, client, authUI);
    }

    public ScannerComponent plus(ScannerFragment fragment) {
        return new ScannerComponent(router, activity.cache, client, authUI);
    }

    public RentalActionsComponent plus(RentalActionsFragment fragment) {
        return new RentalActionsComponent(router, activity.cache, client, authUI);
    }
}
