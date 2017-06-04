package core;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.Partner;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * @author Matthias Schmitt
 */
@RunWith(AndroidJUnit4.class)
public class BattyboostClientTest {
    private static FirebaseDatabase database;
    private static BattyboostClient client;

    @BeforeClass
    public static void setup() {
        Context context = InstrumentationRegistry.getTargetContext();
        FirebaseApp app = FirebaseApp.initializeApp(context);
        //noinspection ConstantConditions
        database = FirebaseDatabase.getInstance(app);
        client = new BattyboostClient(database);
    }

    @Test
    public void addPartner() throws Exception {
        DatabaseReference partnersRef = database.getReference("partners");
        List<Partner> partners =
                RxFirebaseDatabase.observeSingleValueEvent(partnersRef, DataSnapshotMapper.listOf(Partner.class))
                        .blockingGet();
        int oldSize = partners.size();
        client.addPartner(new Partner()).blockingGet();
        partners = RxFirebaseDatabase.observeSingleValueEvent(partnersRef, DataSnapshotMapper.listOf(Partner.class))
                .blockingGet();
        Assert.assertEquals(partners.size(), oldSize + 1);
    }
}
