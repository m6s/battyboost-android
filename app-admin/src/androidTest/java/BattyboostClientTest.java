import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.Partner;
import info.mschmitt.battyboost.core.utils.firebase.RxDatabaseReference;
import io.reactivex.functions.Function;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Matthias Schmitt
 */
@RunWith(AndroidJUnit4.class)
public class BattyboostClientTest {
    private static final Function<DataSnapshot, List<Partner>> PARTNER_LIST_MAPPER = dataSnapshot -> {
        ArrayList<Partner> list = new ArrayList<>();
        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
            list.add(BattyboostClient.PARTNER_MAPPER.apply(childSnapshot));
        }
        return list;
    };
    private static FirebaseDatabase database;
    private static BattyboostClient client;

    @BeforeClass
    public static void setup() {
        Context context = InstrumentationRegistry.getTargetContext();
        FirebaseApp app = FirebaseApp.initializeApp(context);
        //noinspection ConstantConditions
        database = FirebaseDatabase.getInstance(app);
        FirebaseAuth auth = FirebaseAuth.getInstance(app);
        client = new BattyboostClient(database, auth);
    }
//    @Test
//    public void queryAtLocation() throws InterruptedException {
//        double radius = 8589; // Fails
////        double radius = 8587.8; //Passes
//        CountDownLatch latch = new CountDownLatch(1);
//        final boolean[] entered = {false};
//        new GeoFire(database.getReference("_geofirePos")).queryAtLocation(new GeoLocation(0, 0), radius)
//                .addGeoQueryEventListener(new GeoQueryEventListener() {
//                    @Override
//                    public void onKeyEntered(String key, GeoLocation location) {
//                        entered[0] = true;
//                    }
//
//                    @Override
//                    public void onKeyExited(String key) {
//                    }
//
//                    @Override
//                    public void onKeyMoved(String key, GeoLocation location) {
//                    }
//
//                    @Override
//                    public void onGeoQueryReady() {
//                        latch.countDown();
//                    }
//
//                    @Override
//                    public void onGeoQueryError(DatabaseError error) {
//                    }
//                });
//        latch.await();
//        Assert.assertTrue(entered[0]);
//    }

    @Test
    public void addPartner() throws Exception {
        DatabaseReference partnersRef = database.getReference("partners");
        List<Partner> partners =
                RxDatabaseReference.valueEvents(partnersRef).firstElement().map(PARTNER_LIST_MAPPER).blockingGet();
        int oldSize = partners.size();
        client.addPartner(new Partner()).blockingGet();
        partners = RxDatabaseReference.valueEvents(partnersRef).firstElement().map(PARTNER_LIST_MAPPER).blockingGet();
        Assert.assertEquals(partners.size(), oldSize + 1);
    }
}
