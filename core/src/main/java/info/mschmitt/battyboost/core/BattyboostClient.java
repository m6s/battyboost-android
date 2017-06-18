package info.mschmitt.battyboost.core;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.core.entities.Battery;
import info.mschmitt.battyboost.core.entities.DatabaseUser;
import info.mschmitt.battyboost.core.entities.Partner;
import info.mschmitt.battyboost.core.entities.Pos;
import info.mschmitt.battyboost.core.utils.RxOptional;
import info.mschmitt.battyboost.core.utils.firebase.RxAuth;
import info.mschmitt.battyboost.core.utils.firebase.RxDatabaseReference;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;

import java.util.UUID;

/**
 * @author Matthias Schmitt
 */
public class BattyboostClient {
    public static final Function<DataSnapshot, RxOptional<DatabaseUser>> DATABASE_USER_MAPPER =
            dataSnapshot -> dataSnapshot.exists() ? new RxOptional<>(dataSnapshot.getValue(DatabaseUser.class))
                    : RxOptional.empty();
    private final FirebaseDatabase database;
    private final RxAuth rxAuth;

    public BattyboostClient(FirebaseDatabase database, RxAuth rxAuth) {
        this.database = database;
        this.rxAuth = rxAuth;
        connectTriggers();
    }

    /**
     * TODO Move to backend
     */
    private void connectTriggers() {
        userCreateTrigger().flatMapCompletable(this::onUserCreate).subscribe();
    }

    private Observable<FirebaseUser> userCreateTrigger() {
        return rxAuth.userChanges().flatMapMaybe(optional -> {
            FirebaseUser user = optional.value;
            return user != null ? RxDatabaseReference.valueEvents(database.getReference("users").child(user.getUid()))
                    .firstElement() : Completable.complete().toMaybe();
        }).filter(dataSnapshot -> !dataSnapshot.exists()).map(ignore -> rxAuth.auth.getCurrentUser());
    }

    private Completable onUserCreate(FirebaseUser firebaseUser) {
        DatabaseReference userRef = database.getReference("users").child(firebaseUser.getUid());
        DatabaseUser databaseUser = new DatabaseUser();
        return RxDatabaseReference.setValue(userRef, databaseUser);
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
