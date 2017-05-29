package info.mschmitt.battyboost.partnerapp;

import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class BattyboostPartnerApplicationComponent {
    public final BattyboostClient client;
    public final FirebaseDatabase database;

    public BattyboostPartnerApplicationComponent(BattyboostPartnerApplication application) {
        database = FirebaseDatabase.getInstance();
        client = new BattyboostClient(database);
    }

    public void inject(BattyboostPartnerApplication application) {
        application.applicationComponent = this;
        application.injected = true;
    }

    public MainActivityComponent plus(MainActivity activity) {
        return new MainActivityComponent(this);
    }
}
