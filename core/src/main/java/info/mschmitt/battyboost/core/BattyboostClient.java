package info.mschmitt.battyboost.core;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.core.entities.Battery;
import info.mschmitt.battyboost.core.entities.Partner;
import info.mschmitt.battyboost.core.entities.Pos;
import info.mschmitt.battyboost.core.entities.User;
import info.mschmitt.battyboost.core.utils.firebase.RxAuth;
import info.mschmitt.battyboost.core.utils.firebase.RxDatabaseReference;
import io.reactivex.Completable;
import io.reactivex.Observable;
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
        userCreateTrigger().flatMapCompletable(this::onUserCreate).subscribe();
    }

    private Observable<FirebaseUser> userCreateTrigger() {
        return RxAuth.stateChanges(auth)
                .filter(ignore -> auth.getCurrentUser() != null)
                .flatMapMaybe(ignore -> RxDatabaseReference.valueEvents(
                        database.getReference("users").child(auth.getCurrentUser().getUid())).firstElement())
                .filter(dataSnapshot -> !dataSnapshot.exists())
                .map(ignore -> auth.getCurrentUser());
    }

    private Completable onUserCreate(FirebaseUser firebaseUser) {
        DatabaseReference userRef = database.getReference("users").child(firebaseUser.getUid());
        User user = new User();
        return RxDatabaseReference.setValue(userRef, user);
    }

    public Single<String> addPartner(Partner partner) {
        DatabaseReference partnerRef = database.getReference("partners").push();
        return RxDatabaseReference.setValue(partnerRef, partner).toSingleDefault(partnerRef.getKey());
    }

    public Completable updatePartner(String partnerKey, Partner partner) {
        DatabaseReference partnerRef = database.getReference("partners").child(partnerKey);
        return RxDatabaseReference.setValue(partnerRef, partner);
    }

    public Completable deletePartner(String partnerKey) {
        DatabaseReference partnerRef = database.getReference("partners").child(partnerKey);
        return RxDatabaseReference.removeValue(partnerRef);
    }

    public Single<String> addPos(Pos pos) {
        DatabaseReference posRef = database.getReference("pos").push();
        return RxDatabaseReference.setValue(posRef, pos).toSingleDefault(posRef.getKey());
    }

    public Completable updatePos(String posKey, Pos pos) {
        DatabaseReference partnerRef = database.getReference("pos").child(posKey);
        return RxDatabaseReference.setValue(partnerRef, pos);
    }

    public Completable deletePos(String posKey) {
        DatabaseReference partnerRef = database.getReference("pos").child(posKey);
        return RxDatabaseReference.removeValue(partnerRef);
    }

    public Single<String> addBattery(UUID uuid, Battery battery) {
        DatabaseReference batteryRef = database.getReference("batteries").child(uuid.toString());
        return RxDatabaseReference.setValue(batteryRef, battery).toSingleDefault(batteryRef.getKey());
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
        return RxDatabaseReference.setValue(inviteRef, token);
    }
}
