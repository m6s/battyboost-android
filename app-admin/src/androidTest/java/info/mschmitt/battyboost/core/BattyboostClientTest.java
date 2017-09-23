package info.mschmitt.battyboost.core;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import info.mschmitt.androidsupport.RxOptional;
import info.mschmitt.battyboost.core.entities.*;
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
    private static String userId;

    @BeforeClass
    public static void setup() {
        Context context = InstrumentationRegistry.getTargetContext();
        FirebaseApp app = FirebaseApp.initializeApp(context);
        //noinspection ConstantConditions
        FirebaseDatabase database = FirebaseDatabase.getInstance(app);
        FirebaseAuth auth = FirebaseAuth.getInstance(app);
        AuthResult authResult =
                RxAuth.signInWithEmailAndPassword(auth, "emailclient.m6s@gmail.com", "qwerty!@").blockingGet();
        userId = authResult.getUser().getUid();
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
    public void updateUser() throws Exception {
        BusinessUser user = new BusinessUser();
        user.qr = "qr1";
        client.updateUser(userId, user).blockingAwait();
        DatabaseReference userRef = client.usersRef.child(userId);
        BusinessUser before =
                RxQuery.valueEvents(userRef).firstElement().map(BattyboostClient.USER_MAPPER).blockingGet().value;
        user.qr = "qr2";
        client.updateUser(userId, user).blockingAwait();
        BusinessUser after =
                RxQuery.valueEvents(userRef).firstElement().map(BattyboostClient.USER_MAPPER).blockingGet().value;
        Assert.assertNotSame(before.qr, after.qr);
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

    @Test
    public void updateUserEmail() throws Exception {
        client.updateUserEmail(userId, "email1").blockingAwait();
        DatabaseReference userRef = client.usersRef.child(userId);
        BusinessUser before =
                RxQuery.valueEvents(userRef).firstElement().map(BattyboostClient.USER_MAPPER).blockingGet().value;
        client.updateUserEmail(userId, "email2").blockingAwait();
        BusinessUser after =
                RxQuery.valueEvents(userRef).firstElement().map(BattyboostClient.USER_MAPPER).blockingGet().value;
        Assert.assertNotSame(before.email, after.email);
    }

    @Test
    public void updateUserDisplayName() throws Exception {
        client.updateUserDisplayName(userId, "name1").blockingAwait();
        DatabaseReference userRef = client.usersRef.child(userId);
        BusinessUser before =
                RxQuery.valueEvents(userRef).firstElement().map(BattyboostClient.USER_MAPPER).blockingGet().value;
        client.updateUserDisplayName(userId, "name2").blockingAwait();
        BusinessUser after =
                RxQuery.valueEvents(userRef).firstElement().map(BattyboostClient.USER_MAPPER).blockingGet().value;
        Assert.assertNotSame(before.displayName, after.displayName);
    }

    @Test
    public void updateUserIban() throws Exception {
        client.updateUserIban(userId, "iban1").blockingAwait();
        DatabaseReference userRef = client.usersRef.child(userId);
        BusinessUser before =
                RxQuery.valueEvents(userRef).firstElement().map(BattyboostClient.USER_MAPPER).blockingGet().value;
        client.updateUserIban(userId, "iban2").blockingAwait();
        BusinessUser after =
                RxQuery.valueEvents(userRef).firstElement().map(BattyboostClient.USER_MAPPER).blockingGet().value;
        Assert.assertNotSame(before.iban, after.iban);
    }

    @Test
    public void updateUserBankAccountOwner() throws Exception {
        client.updateUserBankAccountOwner(userId, "owner1").blockingAwait();
        DatabaseReference userRef = client.usersRef.child(userId);
        BusinessUser before =
                RxQuery.valueEvents(userRef).firstElement().map(BattyboostClient.USER_MAPPER).blockingGet().value;
        client.updateUserBankAccountOwner(userId, "owner2").blockingAwait();
        BusinessUser after =
                RxQuery.valueEvents(userRef).firstElement().map(BattyboostClient.USER_MAPPER).blockingGet().value;
        Assert.assertNotSame(before.bankAccountOwner, after.bankAccountOwner);
    }

    @Test
    public void updateUserPhotoUrl() throws Exception {
        client.updateUserPhotoUrl(userId, "url1").blockingAwait();
        DatabaseReference userRef = client.usersRef.child(userId);
        BusinessUser before =
                RxQuery.valueEvents(userRef).firstElement().map(BattyboostClient.USER_MAPPER).blockingGet().value;
        client.updateUserPhotoUrl(userId, "url2").blockingAwait();
        BusinessUser after =
                RxQuery.valueEvents(userRef).firstElement().map(BattyboostClient.USER_MAPPER).blockingGet().value;
        Assert.assertNotSame(before.photoUrl, after.photoUrl);
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
