package info.mschmitt.battyboost.serviceapp;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class BattyboostServiceApplicationComponent {
    public final FirebaseDatabase database;
    public final FirebaseAuth auth;
    public final AuthUI authUI;
    public final BattyboostClient client;

    public BattyboostServiceApplicationComponent(BattyboostServiceApplication application) {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        authUI = AuthUI.getInstance();
        client = new BattyboostClient(database, auth);
    }

    public void inject(BattyboostServiceApplication application) {
        application.applicationComponent = this;
        application.injected = true;
    }

    public MainActivityComponent plus(MainActivity activity) {
        return new MainActivityComponent(this);
    }
}
