package info.mschmitt.battyboost.serviceapp;

import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class BattyboostServiceApplicationComponent {
    public final BattyboostClient client;
    public final FirebaseDatabase database;

    public BattyboostServiceApplicationComponent(BattyboostServiceApplication application) {
        database = FirebaseDatabase.getInstance();
        client = new BattyboostClient(database);
    }

    public void inject(BattyboostServiceApplication application) {
        application.applicationComponent = this;
        application.injected = true;
    }

    public MainActivityComponent plus(MainActivity activity) {
        return new MainActivityComponent(this);
    }
}
