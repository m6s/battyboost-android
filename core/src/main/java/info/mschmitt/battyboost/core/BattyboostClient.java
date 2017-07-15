package info.mschmitt.battyboost.core;

import android.net.Uri;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import info.mschmitt.androidsupport.RxOptional;
import info.mschmitt.battyboost.core.entities.*;
import info.mschmitt.firebasesupport.RxAuth;
import info.mschmitt.firebasesupport.RxDatabaseReference;
import info.mschmitt.firebasesupport.RxQuery;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;

import java.util.Date;

/**
 * @author Matthias Schmitt
 */
public class BattyboostClient {
    public static final Function<DataSnapshot, RxOptional<BusinessUser>> DATABASE_USER_MAPPER = dataSnapshot -> {
        BusinessUser businessUser = dataSnapshot.getValue(BusinessUser.class);
        if (businessUser != null) {
            businessUser.id = dataSnapshot.getKey();
        }
        return new RxOptional<>(businessUser);
    };
    public static final Function<DataSnapshot, RxOptional<Partner>> PARTNER_MAPPER = dataSnapshot -> {
        Partner partner = dataSnapshot.getValue(Partner.class);
        if (partner != null) {
            partner.id = dataSnapshot.getKey();
        }
        return new RxOptional<>(partner);
    };
    public static final Function<DataSnapshot, RxOptional<Pos>> POS_MAPPER = dataSnapshot -> {
        Pos pos = dataSnapshot.getValue(Pos.class);
        if (pos != null) {
            pos.id = dataSnapshot.getKey();
        }
        return new RxOptional<>(pos);
    };
    public static final Function<DataSnapshot, RxOptional<Battery>> BATTERY_MAPPER = dataSnapshot -> {
        Battery battery = dataSnapshot.getValue(Battery.class);
        if (battery != null) {
            battery.id = dataSnapshot.getKey();
        }
        return new RxOptional<>(battery);
    };
    public static final Function<DataSnapshot, RxOptional<BusinessUser>> USER_MAPPER = dataSnapshot -> {
        BusinessUser user = dataSnapshot.getValue(BusinessUser.class);
        if (user != null) {
            user.id = dataSnapshot.getKey();
        }
        return new RxOptional<>(user);
    };
    public final DatabaseReference usersRef;
    public final DatabaseReference partnersRef;
    public final DatabaseReference posListRef;
    public final DatabaseReference batteriesRef;
    public final DatabaseReference invitesRef;
    public final DatabaseReference transactionsRef;
    public final StorageReference usersStorageRef;

    public BattyboostClient(FirebaseDatabase database, FirebaseAuth auth, FirebaseStorage storage) {
        usersRef = database.getReference("users");
        partnersRef = database.getReference("partners");
        posListRef = database.getReference("pos");
        batteriesRef = database.getReference("batteries");
        invitesRef = database.getReference("invites");
        transactionsRef = database.getReference("transactions");
        usersStorageRef = storage.getReference().child("users");
        userCreations(auth).subscribe(this::onUserCreated); // TODO Create auth trigger function
    }

    private Observable<FirebaseUser> userCreations(FirebaseAuth auth) {
        return RxAuth.userChanges(auth)
                .filter(optional -> optional.value != null)
                .map(optional -> optional.value)
                .flatMapMaybe(firebaseUser -> {
                    DatabaseReference userRef = usersRef.child(firebaseUser.getUid());
                    return RxQuery.valueEvents(userRef)
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
        BusinessUser businessUser = new BusinessUser();
        businessUser.displayName = firebaseUser.getDisplayName();
        businessUser.email = firebaseUser.getEmail();
        Uri photoUrl = firebaseUser.getPhotoUrl();
        businessUser.photoUrl = photoUrl != null ? photoUrl.toString() : null;
        userRef.setValue(businessUser);
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

    public Completable updateUser(String userKey, BusinessUser user) {
        DatabaseReference userRef = usersRef.child(userKey);
        return RxDatabaseReference.setValue(userRef, user);
    }

    public Completable updateUserEmail(String userId, String email) {
        DatabaseReference emailRef = usersRef.child(userId).child("email");
        return RxDatabaseReference.setValue(emailRef, email);
    }

    public Completable updateUserDisplayName(String userId, String displayName) {
        DatabaseReference displayNameRef = usersRef.child(userId).child("displayName");
        return RxDatabaseReference.setValue(displayNameRef, displayName);
    }

    public Completable updateUserIban(String userId, String iban) {
        DatabaseReference ibanRef = usersRef.child(userId).child("iban");
        return RxDatabaseReference.setValue(ibanRef, iban);
    }

    public Completable updateUserBankAccountOwner(String userId, String owner) {
        DatabaseReference ownerRef = usersRef.child(userId).child("bankAccountOwner");
        return RxDatabaseReference.setValue(ownerRef, owner);
    }

    public Completable updateUserPhotoUrl(String userId, String url) {
        DatabaseReference urlRef = usersRef.child(userId).child("photoUrl");
        return RxDatabaseReference.setValue(urlRef, url);
    }

    public Single<String> addBattery(Battery battery) {
        DatabaseReference batteryRef = batteriesRef.push();
        return RxDatabaseReference.setValue(batteryRef, battery).toSingleDefault(batteryRef.getKey());
    }

    public Completable updateBattery(String batteryKey, Battery battery) {
        DatabaseReference batteryRef = batteriesRef.child(batteryKey);
        return RxDatabaseReference.setValue(batteryRef, battery);
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

    public Single<RentBatteryResult> rentBattery(String batteryQr, String userQr) {
        Single<PrepareRentBatteryResult> prepareRentBatteryResultSingle = prepareRentBattery(batteryQr, userQr);
        return prepareRentBatteryResultSingle.flatMap(prepareRentBatteryResult -> {
            if (prepareRentBatteryResult.error != null) {
                RentBatteryResult result = new RentBatteryResult();
                result.error = prepareRentBatteryResult.error;
                return Single.just(result);
            } else {
                return rentBattery(prepareRentBatteryResult.battery, prepareRentBatteryResult.user,
                        prepareRentBatteryResult.partnerCreditedCents);
            }
        });
    }

    public Single<PrepareRentBatteryResult> prepareRentBattery(String batteryQr, String userQr) {
        Single<RxOptional<BusinessUser>> userSingle = findUserByQr(userQr);
        Single<RxOptional<Battery>> batterySingle = findBatteryByQr(batteryQr);
        return Single.zip(batterySingle, userSingle,
                (batteryOptional, userOptional) -> toPrepareRentBatteryResult(batteryOptional.value, userOptional.value,
                        userQr == null));
    }

    private Single<RentBatteryResult> rentBattery(Battery battery, BusinessUser user, int partnerCreditedCents) {
        battery.rentalTime = new Date().getTime();
        BusinessTransaction transaction = new BusinessTransaction();
        if (user != null) {
            transaction.renterId = user.id;
        }
        transaction.batteryId = battery.id;
        transaction.partnerCreditedCents = partnerCreditedCents;
        transaction.type = "borrow";
        RentBatteryResult result = new RentBatteryResult();
        result.transaction = transaction;
        return RxDatabaseReference.setValue(transactionsRef.push(), transaction).toSingleDefault(result);
    }

    private Single<RxOptional<BusinessUser>> findUserByQr(String userQr) {
        Query userByQrQuery = usersRef.orderByChild("qr").equalTo(userQr);
        if (userQr != null) {
            return RxQuery.valueEvents(userByQrQuery)
                    .map(RxQuery.FIRST_CHILD_MAPPER)
                    .map(optional -> optional.flatMap(USER_MAPPER))
                    .firstElement()
                    .toSingle();
        } else {
            return Single.just(RxOptional.empty());
        }
    }

    private Single<RxOptional<Battery>> findBatteryByQr(String batteryQr) {
        Query batteryByQrQuery = batteriesRef.orderByChild("qr").equalTo(batteryQr);
        return RxQuery.valueEvents(batteryByQrQuery)
                .map(RxQuery.FIRST_CHILD_MAPPER)
                .map(optional -> optional.flatMap(BATTERY_MAPPER))
                .firstElement()
                .toSingle();
    }

    public PrepareRentBatteryResult toPrepareRentBatteryResult(Battery battery, BusinessUser user, boolean anonymous) {
        PrepareRentBatteryResult result = new PrepareRentBatteryResult();
        if (!anonymous && user == null) {
            result.error = "User does not exist";
            return result;
        }
        if (battery == null) {
            result.error = "Battery does not exist";
        } else {
            result.battery = battery;
            result.user = user;
            result.renterCreditedCents = -1200;
            result.renterCashCreditedCents = -1200;
            result.partnerCreditedCents = 40;
        }
        return result;
    }

    public Single<ReturnBatteryResult> returnBattery(String batteryQr, String userQr) {
        Single<PrepareReturnBatteryResult> prepareReturnBatteryResultSingle = prepareReturnBattery(batteryQr, userQr);
        return prepareReturnBatteryResultSingle.flatMap(prepareReturnBatteryResult -> {
            if (prepareReturnBatteryResult.error != null) {
                ReturnBatteryResult result = new ReturnBatteryResult();
                result.error = prepareReturnBatteryResult.error;
                return Single.just(result);
            } else {
                return returnBattery(prepareReturnBatteryResult.battery, prepareReturnBatteryResult.user,
                        prepareReturnBatteryResult.partnerCreditedCents);
            }
        });
    }

    public Single<PrepareReturnBatteryResult> prepareReturnBattery(String batteryQr, String userQr) {
        Single<RxOptional<BusinessUser>> userSingle = findUserByQr(userQr);
        Single<RxOptional<Battery>> batterySingle = findBatteryByQr(batteryQr);
        return Single.zip(batterySingle, userSingle,
                (batteryOptional, userOptional) -> toPrepareReturnBatteryResult(batteryOptional.value,
                        userOptional.value, userQr == null));
    }

    private Single<ReturnBatteryResult> returnBattery(Battery battery, BusinessUser user, int partnerCreditedCents) {
        battery.rentalTime = new Date().getTime();
        BusinessTransaction transaction = new BusinessTransaction();
        if (user != null) {
            transaction.renterId = user.id;
        }
        transaction.batteryId = battery.id;
        transaction.partnerCreditedCents = partnerCreditedCents;
        transaction.type = "return";
        ReturnBatteryResult result = new ReturnBatteryResult();
        result.transaction = transaction;
        return RxDatabaseReference.setValue(transactionsRef.push(), transaction).toSingleDefault(result);
    }

    public PrepareReturnBatteryResult toPrepareReturnBatteryResult(Battery battery, BusinessUser user,
                                                                   boolean anonymous) {
        PrepareReturnBatteryResult result = new PrepareReturnBatteryResult();
        if (!anonymous && user == null) {
            result.error = "User does not exist";
            return result;
        }
        if (battery == null) {
            result.error = "Battery does not exist";
        } else {
            result.battery = battery;
            result.user = user;
            result.renterCreditedCents = 800;
            result.renterCashCreditedCents = 800;
            result.partnerCreditedCents = 40;
        }
        return result;
    }

    public static class RentBatteryResult {
        public String error;
        public BusinessTransaction transaction;
    }

    public static class PrepareRentBatteryResult {
        public String error;
        public Battery battery;
        public BusinessUser user;
        public int renterCreditedCents;
        public int partnerCreditedCents;
        public int renterCashCreditedCents;
    }

    public static class PrepareReturnBatteryResult {
        public String error;
        public Battery battery;
        public BusinessUser user;
        public int renterCreditedCents;
        public int partnerCreditedCents;
        public int renterCashCreditedCents;
    }

    public static class ReturnBatteryResult {
        public String error;
        public BusinessTransaction transaction;
    }
}
