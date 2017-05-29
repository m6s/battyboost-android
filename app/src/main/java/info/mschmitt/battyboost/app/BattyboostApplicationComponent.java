package info.mschmitt.battyboost.app;

import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class BattyboostApplicationComponent {
    public final BattyboostClient client;
    public final FirebaseDatabase database;

    public BattyboostApplicationComponent(BattyboostApplication application) {
        database = FirebaseDatabase.getInstance();
        client = new BattyboostClient(database);
    }

    public void inject(BattyboostApplication application) {
        application.applicationComponent = this;
        application.injected = true;
    }

    public MainActivityComponent plus(MainActivity activity) {
        return new MainActivityComponent(this);
    }
}
