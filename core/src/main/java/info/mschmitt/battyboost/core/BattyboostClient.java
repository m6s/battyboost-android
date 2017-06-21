package info.mschmitt.battyboost.core;

import android.util.Pair;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.core.entities.Battery;
import info.mschmitt.battyboost.core.entities.DatabaseUser;
import info.mschmitt.battyboost.core.entities.Partner;
import info.mschmitt.battyboost.core.entities.Pos;
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
    public static final Function<DataSnapshot, DatabaseUser> DATABASE_USER_MAPPER =
            dataSnapshot -> dataSnapshot.getValue(DatabaseUser.class);
    public static final Function<DataSnapshot, Partner> PARTNER_MAPPER =
            dataSnapshot -> dataSnapshot.getValue(Partner.class);
    public static final Function<DataSnapshot, Pos> POS_MAPPER = dataSnapshot -> dataSnapshot.getValue(Pos.class);
    public static final Function<DataSnapshot, Pair<String, Pos>> KEY_POS_MAPPER =
            dataSnapshot -> Pair.create(dataSnapshot.getKey(), dataSnapshot.getValue(Pos.class));
    public static final Function<DataSnapshot, Battery> BATTERY_MAPPER =
            dataSnapshot -> dataSnapshot.getValue(Battery.class);
    public final DatabaseReference usersRef;
    public final DatabaseReference partnersRef;
    public final DatabaseReference posListRef;
    public final DatabaseReference batteriesRef;
    public final DatabaseReference invitesRef;

    public BattyboostClient(FirebaseDatabase database, FirebaseAuth auth) {
        usersRef = database.getReference("users");
        partnersRef = database.getReference("partners");
        posListRef = database.getReference("pos");
        batteriesRef = database.getReference("batteries");
        invitesRef = database.getReference("invites");
        userCreations(auth).subscribe(this::onUserCreated); // TODO Create auth trigger function
    }

    private Observable<FirebaseUser> userCreations(FirebaseAuth auth) {
        return RxAuth.userChanges(auth)
                .filter(optional -> optional.value != null)
                .map(optional -> optional.value)
                .flatMapMaybe(firebaseUser -> {
                    DatabaseReference userRef = usersRef.child(firebaseUser.getUid());
                    return RxDatabaseReference.valueEvents(userRef)
                            .firstElement()
                            .filter(dataSnapshot -> !dataSnapshot.exists())
                            .map(ignore -> firebaseUser);
                });
    }

    /**
     * Trigger
     */
    private void onUserCreated(FirebaseUser firebaseUser) {
        String uid = firebaseUser.getUid();
        DatabaseReference userRef = usersRef.child(uid);
        DatabaseUser databaseUser = new DatabaseUser();
        userRef.setValue(databaseUser);
    }

    public Single<String> addPartner(Partner partner) {
        DatabaseReference partnerRef = partnersRef.push();
        return RxDatabaseReference.setValue(partnerRef, partner).toSingleDefault(partnerRef.getKey());
    }

    public Completable updatePartner(String partnerKey, Partner partner) {
        DatabaseReference partnerRef = partnersRef.child(partnerKey);
        return RxDatabaseReference.setValue(partnerRef, partner);
    }

    public Completable deletePartner(String partnerKey) {
        DatabaseReference partnerRef = partnersRef.child(partnerKey);
        return RxDatabaseReference.removeValue(partnerRef);
    }

    public Single<String> addPos(Pos pos) {
        DatabaseReference posRef = posListRef.push();
        String key = posRef.getKey();
        return RxDatabaseReference.setValue(posRef, pos).toSingleDefault(key);
    }

    public Completable updatePos(String posKey, Pos pos) {
        DatabaseReference partnerRef = posListRef.child(posKey);
        return RxDatabaseReference.setValue(partnerRef, pos);
    }

    public Completable deletePos(String posKey) {
        DatabaseReference partnerRef = posListRef.child(posKey);
        return RxDatabaseReference.removeValue(partnerRef);
    }

    public Completable updateUser(String userKey, DatabaseUser user) {
        DatabaseReference userRef = usersRef.child(userKey);
        return RxDatabaseReference.setValue(userRef, user);
    }

    public Single<String> addBattery(UUID uuid, Battery battery) {
        DatabaseReference batteryRef = batteriesRef.child(uuid.toString());
        return RxDatabaseReference.setValue(batteryRef, battery).toSingleDefault(batteryRef.getKey());
    }

    /**
     * The token will be cleared on first use.
     *
     * @param token       A random string used to connect cashiers to a partner.
     * @param validMillis How long the token is valid
     */
    public Completable pushInvite(String token, long validMillis) {
        String partnerId = ""; // TODO Get from db
        DatabaseReference inviteRef = invitesRef.child(partnerId);
        return RxDatabaseReference.setValue(inviteRef, token);
    }
}
