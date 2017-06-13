package info.mschmitt.battyboost.core;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import durdinapps.rxfirebase2.RxFirebaseAuth;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import info.mschmitt.battyboost.core.entities.Battery;
import info.mschmitt.battyboost.core.entities.Partner;
import info.mschmitt.battyboost.core.entities.Pos;
import io.reactivex.Completable;
import io.reactivex.Single;

import java.util.UUID;

/**
 * @author Matthias Schmitt
 */
public class BattyboostClient {
    private final FirebaseDatabase database;
    private final FirebaseAuth auth;

    public BattyboostClient(FirebaseDatabase database, FirebaseAuth auth) {
        this.database = database;
        this.auth = auth;
        connectTriggers();
    }

    /**
     * TODO Move to backend
     */
    private void connectTriggers() {
        RxFirebaseAuth.observeAuthState(auth)
                .filter(ignore -> auth.getCurrentUser() != null)
                .map(FirebaseAuth::getCurrentUser)
                .subscribe(this::onUserSignIn);
    }

    private void onUserSignIn(FirebaseUser user) {
    }

    public Single<String> addPartner(Partner partner) {
        DatabaseReference partnerMapRef = database.getReference("partners");
        DatabaseReference partnerRef = partnerMapRef.push();
        return RxFirebaseDatabase.setValue(partnerRef, partner).toSingleDefault(partnerRef.getKey());
    }

    public Completable updatePartner(String partnerKey, Partner partner) {
        DatabaseReference partnerRef = database.getReference("partners/" + partnerKey);
        return RxFirebaseDatabase.setValue(partnerRef, partner);
    }

    public Single<String> addPos(Pos pos) {
        DatabaseReference posMapRef = database.getReference("pos");
        DatabaseReference posRef = posMapRef.push();
        return RxFirebaseDatabase.setValue(posRef, pos).toSingleDefault(posRef.getKey());
    }

    public Completable updatePos(String posKey, Pos pos) {
        DatabaseReference partnerRef = database.getReference("pos/" + posKey);
        return RxFirebaseDatabase.setValue(partnerRef, pos);
    }

    public Single<String> addBattery(UUID uuid, Battery battery) {
        DatabaseReference batteryMapRef = database.getReference("batteries");
        DatabaseReference batteryRef = batteryMapRef.child(uuid.toString());
        return RxFirebaseDatabase.setValue(batteryRef, battery).toSingleDefault(batteryRef.getKey());
    }

    /**
     * The token will be cleared on first use.
     *
     * @param token       A random string used to connect cashiers to a partner.
     * @param validMillis How long the token is valid
     */
    public Completable pushInvite(String token, long validMillis) {
        DatabaseReference inviteMapRef = database.getReference("invites");
        String partnerId = ""; // TODO Get from db
        DatabaseReference inviteRef = inviteMapRef.child(partnerId);
        return RxFirebaseDatabase.setValue(inviteRef, token);
    }
}
