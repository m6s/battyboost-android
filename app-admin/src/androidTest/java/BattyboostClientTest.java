import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import info.mschmitt.androidsupport.RxOptional;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.Battery;
import info.mschmitt.battyboost.core.entities.BusinessTransaction;
import info.mschmitt.battyboost.core.entities.Partner;
import info.mschmitt.firebasesupport.RxDatabaseReference;
import info.mschmitt.firebasesupport.RxQuery;
import io.reactivex.functions.Function;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

/**
 * @author Matthias Schmitt
 */
@RunWith(AndroidJUnit4.class)
public class BattyboostClientTest {
    private static final Function<DataSnapshot, List<Partner>> PARTNER_LIST_MAPPER = dataSnapshot -> {
        ArrayList<Partner> list = new ArrayList<>();
        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
            list.add(BattyboostClient.PARTNER_MAPPER.apply(childSnapshot).value);
        }
        return list;
    };
    private static final String UID = "WODzvGGRhwQJS8xFFRD5RkPiFDC3";
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
        client = new BattyboostClient(database, auth, storage);
    }

    @Test
    public void addPartner() throws Exception {
        Partner partner = new Partner();
        partner.name = UUID.randomUUID().toString();
        String partnerId = client.addPartner(UID, partner).blockingGet();
        DatabaseReference partnerRef = client.partnersRef.child(partnerId);
        RxOptional<Partner> optional =
                RxQuery.valueEvents(partnerRef).firstElement().map(BattyboostClient.PARTNER_MAPPER).blockingGet();
        Assert.assertNotNull(optional.value);
        Assert.assertEquals(partner.name, optional.value.name);
    }

    /**
     * "txend": {
     * ".validate": "newData.val() === root.child('txbegin').val()"
     * }
     */
    @Test
    public void transaction() throws Exception {
        DatabaseReference txBeginRef = database.getReference("txbegin");
        String txId = UUID.randomUUID().toString();
        RxDatabaseReference.setValue(txBeginRef, txId).blockingAwait();
        Battery battery = new Battery();
        battery.id = client.batteriesRef.push().getKey();
        battery.rentalTime = System.currentTimeMillis();
        BusinessTransaction transaction = new BusinessTransaction();
        transaction.batteryId = battery.id;
        transaction.partnerCreditedCents = 10;
        transaction.type = BusinessTransaction.TYPE_RENTAL;
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(client.transactionsRef.getKey() + "/" + client.transactionsRef.push().getKey(), transaction);
        updateMap.put(client.batteriesRef.getKey() + "/" + battery.id, battery);
        updateMap.put("/txend", txId);
        RxDatabaseReference.updateChildren(client.rootRef, updateMap).blockingAwait();
    }

    public static class AddPartnerRequest {
        public Partner input;
    }
}
