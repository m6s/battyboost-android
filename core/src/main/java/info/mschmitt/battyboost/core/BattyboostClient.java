package info.mschmitt.battyboost.core;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import info.mschmitt.battyboost.core.entities.Battery;
import info.mschmitt.battyboost.core.entities.Partner;
import io.reactivex.Single;

import java.util.UUID;

/**
 * @author Matthias Schmitt
 */
public class BattyboostClient {
    private final FirebaseDatabase database;

    public BattyboostClient(FirebaseDatabase database) {
        this.database = database;
    }

    public Single<String> addPartner(Partner partner) {
        DatabaseReference partnerMapRef = database.getReference("partners");
        DatabaseReference partnerRef = partnerMapRef.push();
        return RxFirebaseDatabase.setValue(partnerRef, partner).toSingleDefault(partnerRef.getKey());
    }

    public Single<String> addBattery(UUID uuid, Battery battery) {
        DatabaseReference batteryMapRef = database.getReference("batteries");
        DatabaseReference batteryRef = batteryMapRef.child(uuid.toString());
        return RxFirebaseDatabase.setValue(batteryRef, battery).toSingleDefault(batteryRef.getKey());
    }
}
