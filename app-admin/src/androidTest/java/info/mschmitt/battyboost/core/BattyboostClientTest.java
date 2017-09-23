package info.mschmitt.battyboost.core;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import info.mschmitt.androidsupport.RxOptional;
import info.mschmitt.battyboost.core.entities.Battery;
import info.mschmitt.battyboost.core.entities.BusinessTransaction;
import info.mschmitt.battyboost.core.entities.Partner;
import info.mschmitt.battyboost.core.entities.Pos;
import info.mschmitt.battyboost.core.network.BattyboostClient;
import info.mschmitt.firebasesupport.RxAuth;
import info.mschmitt.firebasesupport.RxDatabaseReference;
import info.mschmitt.firebasesupport.RxQuery;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Matthias Schmitt
 */
@RunWith(AndroidJUnit4.class)
public class BattyboostClientTest {
    private static BattyboostClient client;

    @BeforeClass
    public static void setup() {
        Context context = InstrumentationRegistry.getTargetContext();
        FirebaseApp app = FirebaseApp.initializeApp(context);
        //noinspection ConstantConditions
        FirebaseDatabase database = FirebaseDatabase.getInstance(app);
        FirebaseAuth auth = FirebaseAuth.getInstance(app);
        RxAuth.signInWithEmailAndPassword(auth, "emailclient.m6s@gmail.com", "qwerty!@").blockingGet();
        FirebaseStorage storage = FirebaseStorage.getInstance(app);
        client = new BattyboostClient(auth, database, storage, "default");
    }

    @Test
    public void createPartner() throws Exception {
        Partner partner = new Partner();
        partner.name = UUID.randomUUID().toString();
        String partnerId = client.createPartner(partner).blockingGet();
        DatabaseReference partnerRef = client.partnersRef.child(partnerId);
        RxOptional<Partner> optional =
                RxQuery.valueEvents(partnerRef).firstElement().map(BattyboostClient.PARTNER_MAPPER).blockingGet();
        Assert.assertNotNull(optional.value);
        Assert.assertEquals(partner.name, optional.value.name);
    }

    @Test
    public void updatePartner() throws Exception {
        Partner partner = new Partner();
        partner.name = UUID.randomUUID().toString();
        String partnerId = client.createPartner(partner).blockingGet();
        partner.name = UUID.randomUUID().toString();
        client.updatePartner(partnerId, partner).blockingAwait();
        DatabaseReference partnerRef = client.partnersRef.child(partnerId);
        RxOptional<Partner> optional =
                RxQuery.valueEvents(partnerRef).firstElement().map(BattyboostClient.PARTNER_MAPPER).blockingGet();
        Assert.assertNotNull(optional.value);
        Assert.assertEquals(partner.name, optional.value.name);
    }

    @Test
    public void deletePartner() throws Exception {
        Partner partner = new Partner();
        partner.name = UUID.randomUUID().toString();
        String partnerId = client.createPartner(partner).blockingGet();
        client.deletePartner(partnerId).blockingAwait();
        DatabaseReference partnerRef = client.partnersRef.child(partnerId);
        RxOptional<Partner> optional =
                RxQuery.valueEvents(partnerRef).firstElement().map(BattyboostClient.PARTNER_MAPPER).blockingGet();
        Assert.assertNull(optional.value);
    }

    @Test
    public void createPos() throws Exception {
        Pos pos = new Pos();
        pos.name = UUID.randomUUID().toString();
        String partnerId = client.createPos(pos).blockingGet();
        DatabaseReference posRef = client.posListRef.child(partnerId);
        RxOptional<Pos> optional =
                RxQuery.valueEvents(posRef).firstElement().map(BattyboostClient.POS_MAPPER).blockingGet();
        Assert.assertNotNull(optional.value);
        Assert.assertEquals(pos.name, optional.value.name);
    }

    @Test
    public void updatePos() throws Exception {
        Pos pos = new Pos();
        pos.name = UUID.randomUUID().toString();
        String posId = client.createPos(pos).blockingGet();
        pos.name = UUID.randomUUID().toString();
        client.updatePos(posId, pos).blockingAwait();
        DatabaseReference posRef = client.posListRef.child(posId);
        RxOptional<Pos> optional =
                RxQuery.valueEvents(posRef).firstElement().map(BattyboostClient.POS_MAPPER).blockingGet();
        Assert.assertNotNull(optional.value);
        Assert.assertEquals(pos.name, optional.value.name);
    }

    @Test
    public void deletePos() throws Exception {
        Pos pos = new Pos();
        pos.name = UUID.randomUUID().toString();
        String posId = client.createPos(pos).blockingGet();
        client.deletePos(posId).blockingAwait();
        DatabaseReference posRef = client.posListRef.child(posId);
        RxOptional<Pos> optional =
                RxQuery.valueEvents(posRef).firstElement().map(BattyboostClient.POS_MAPPER).blockingGet();
        Assert.assertNull(optional.value);
    }

    @Test
    public void createBattery() throws Exception {
        Battery battery = new Battery();
        battery.qr = UUID.randomUUID().toString();
        String partnerId = client.createBattery(battery).blockingGet();
        DatabaseReference batteryRef = client.batteriesRef.child(partnerId);
        RxOptional<Battery> optional =
                RxQuery.valueEvents(batteryRef).firstElement().map(BattyboostClient.BATTERY_MAPPER).blockingGet();
        Assert.assertNotNull(optional.value);
        Assert.assertEquals(battery.qr, optional.value.qr);
    }

    @Test
    public void updateBattery() throws Exception {
        Battery battery = new Battery();
        battery.qr = UUID.randomUUID().toString();
        String batteryId = client.createBattery(battery).blockingGet();
        battery.qr = UUID.randomUUID().toString();
        client.updateBattery(batteryId, battery).blockingAwait();
        DatabaseReference batteryRef = client.batteriesRef.child(batteryId);
        RxOptional<Battery> optional =
                RxQuery.valueEvents(batteryRef).firstElement().map(BattyboostClient.BATTERY_MAPPER).blockingGet();
        Assert.assertNotNull(optional.value);
        Assert.assertEquals(battery.qr, optional.value.qr);
    }

    @Test
    public void deleteBattery() throws Exception {
        Battery battery = new Battery();
        battery.qr = UUID.randomUUID().toString();
        String batteryId = client.createBattery(battery).blockingGet();
        client.deleteBattery(batteryId).blockingAwait();
        DatabaseReference batteryRef = client.batteriesRef.child(batteryId);
        RxOptional<Battery> optional =
                RxQuery.valueEvents(batteryRef).firstElement().map(BattyboostClient.BATTERY_MAPPER).blockingGet();
        Assert.assertNull(optional.value);
    }

    /**
     * "txend": {
     * ".validate": "newData.val() === root.child('txbegin').val()"
     * }
     */
    @Test
    public void transaction() throws Exception {
        DatabaseReference txBeginRef = client.prefixRef.child("txbegin");
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
        updateMap.put("data/" + client.transactionsRef.getKey() + "/" + client.transactionsRef.push().getKey(),
                transaction);
        updateMap.put("data/" + client.batteriesRef.getKey() + "/" + battery.id, battery);
        updateMap.put("txend", txId);
        RxDatabaseReference.updateChildren(client.prefixRef, updateMap).blockingAwait();
    }
}
