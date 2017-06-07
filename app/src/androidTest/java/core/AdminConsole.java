package core;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.Battery;
import info.mschmitt.battyboost.core.entities.Partner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URLEncoder;
import java.util.*;

/**
 * @author Matthias Schmitt
 */
@RunWith(AndroidJUnit4.class)
public class AdminConsole {
    private static final String TAG = "ADMIN";
    private static FirebaseDatabase database;
    private static FirebaseAuth auth;
    private static BattyboostClient client;

    @BeforeClass
    public static void setup() {
        Context context = InstrumentationRegistry.getTargetContext();
        FirebaseApp app = FirebaseApp.initializeApp(context);
        //noinspection ConstantConditions
        database = FirebaseDatabase.getInstance(app);
        auth = FirebaseAuth.getInstance(app);
        client = new BattyboostClient(database, auth);
    }

    @Test
    public void addPartner() throws Exception {
        Partner partner = new Partner();
        partner.balance = 1000;
        String key = client.addPartner(partner).blockingGet();
        Log.i(TAG, key);
    }

    @Test
    public void printBatteryQRs() throws Exception {
        DatabaseReference batteryMapRef = database.getReference("batteries");
        Map<String, Battery> batteries =
                RxFirebaseDatabase.observeSingleValueEvent(batteryMapRef, DataSnapshotMapper.mapOf(Battery.class))
                        .blockingGet();
        for (Map.Entry<String, Battery> entry : batteries.entrySet()) {
            String qrData = compileQRData(entry.getKey(), entry.getValue());
            String url = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + URLEncoder.encode(qrData,
                    "UTF-8");
            Log.i(TAG, url);
        }
    }

    private String compileQRData(String uuid, Battery battery) {
        String version = "0";
        String target = "0";
        return version + target + uuid;
    }

    @Test
    public void addBattery() {
        Battery battery = new Battery();
        Calendar calendar = Calendar.getInstance(Locale.GERMANY); // TODO Use Java 8 date time framework
        calendar.clear();
        calendar.set(Calendar.MONTH, Calendar.AUGUST);
        calendar.set(Calendar.YEAR, 2016);
        Date date = calendar.getTime();
        battery.manufacturingTime = date.getTime();
        String key = client.addBattery(UUID.randomUUID(), battery).blockingGet();
        Log.i(TAG, key);
    }
}
