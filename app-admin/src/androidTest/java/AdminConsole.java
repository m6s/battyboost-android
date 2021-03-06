import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import info.mschmitt.androidsupport.RxOptional;
import info.mschmitt.battyboost.core.entities.Battery;
import info.mschmitt.battyboost.core.network.BattyboostClient;
import info.mschmitt.firebasesupport.RxQuery;
import io.reactivex.functions.Function;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Matthias Schmitt
 */
@RunWith(AndroidJUnit4.class)
public class AdminConsole {
    private static final String TAG = "ADMIN";
    private static final Function<DataSnapshot, Map<String, Battery>> BATTERY_MAP_MAPPER = dataSnapshot -> {
        Map<String, Battery> map = new HashMap<>((int) dataSnapshot.getChildrenCount());
        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
            map.put(childSnapshot.getKey(),
                    ((Function<DataSnapshot, RxOptional<Battery>>) BattyboostClient::mapBattery).apply(
                            childSnapshot).value);
        }
        return map;
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
        FirebaseStorage storage = FirebaseStorage.getInstance(app);
        client = new BattyboostClient(database, storage, "default");
        client.connect(auth);
    }

    @Test
    public void printBatteryQRs() throws Exception {
        DatabaseReference batteriesRef = client.batteriesRef;
        Map<String, Battery> batteries =
                RxQuery.valueEvents(batteriesRef).firstElement().map(BATTERY_MAP_MAPPER).blockingGet();
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
        String key = client.createBattery(battery).blockingGet();
        Log.i(TAG, key);
    }
}
