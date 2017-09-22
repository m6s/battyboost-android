package info.mschmitt.battyboost.core;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import info.mschmitt.androidsupport.RxOptional;
import info.mschmitt.battyboost.core.entities.*;
import info.mschmitt.firebasesupport.RxDatabaseReference;
import info.mschmitt.firebasesupport.RxQuery;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Function;

import java.util.HashMap;
import java.util.Map;

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
    public final DatabaseReference prefixRef;
    public final DatabaseReference usersRef;
    public final DatabaseReference partnersRef;
    public final DatabaseReference posListRef;
    public final DatabaseReference batteriesRef;
    public final DatabaseReference invitesRef;
    public final DatabaseReference transactionsRef;
    public final StorageReference usersStorageRef;
    private final DatabaseReference logicRef;
    private final FirebaseAuth auth;

    public BattyboostClient(FirebaseAuth auth, FirebaseDatabase database, FirebaseStorage storage, String prefix) {
        this.auth = auth;
        prefixRef = database.getReference(prefix);
        usersRef = prefixRef.child("data/users");
        partnersRef = prefixRef.child("data/partners");
        posListRef = prefixRef.child("data/pos");
        batteriesRef = prefixRef.child("data/batteries");
        invitesRef = prefixRef.child("data/invites");
        transactionsRef = prefixRef.child("data/transactions");
        logicRef = prefixRef.child("logic");
        usersStorageRef = storage.getReference().child("users");
    }

    public Single<String> createPartner(Partner partner) {
        return executeFunction("createPartner", new CreatePartnerInput(partner), CreatePartnerOutput.class).map(
                createPartnerOutput -> createPartnerOutput.partnerId);
    }

    private <InputT, OutputT> Single<OutputT> executeFunction(String functionName, InputT input,
                                                              Class<OutputT> outputClass) {
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference executionRef = logicRef.child(userId).child(functionName).push();
        DatabaseReference inputRef = executionRef.child("input");
        DatabaseReference outputRef = executionRef.child("output");
        return RxDatabaseReference.setValue(inputRef, input).andThen(RxQuery.valueEvents(outputRef)
                        .filter(DataSnapshot::exists)
                .map(dataSnapshot -> dataSnapshot.getValue(outputClass))
                .firstOrError());
    }

    public Completable updatePartner(String partnerId, Partner partner) {
        return executeFunction("updatePartner", new UpdatePartnerInput(partnerId, partner),
                UpdatePartnerOutput.class).toCompletable();
    }

    public Completable deletePartner(String partnerId) {
        return executeFunction("deletePartner", new DeletePartnerInput(partnerId),
                DeletePartnerOutput.class).toCompletable();
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

    public Single<RentBatteryResult> rentBattery(String batteryQr) {
        Single<PrepareRentBatteryResult> prepareRentBatteryResultSingle = prepareRentBattery(batteryQr);
        return prepareRentBatteryResultSingle.flatMap(prepareRentBatteryResult -> {
            if (prepareRentBatteryResult.error != null) {
                RentBatteryResult result = new RentBatteryResult();
                result.error = prepareRentBatteryResult.error;
                return Single.just(result);
            }
            return rentBattery(prepareRentBatteryResult.battery, prepareRentBatteryResult.partnerCreditedCents);
        });
    }

    public Single<PrepareRentBatteryResult> prepareRentBattery(String batteryQr) {
        return findBatteryByQr(batteryQr).map(batteryOptional -> toPrepareRentBatteryResult(batteryOptional.value));
    }

    private Single<RentBatteryResult> rentBattery(Battery battery, int partnerCreditedCents) {
        battery.rentalTime = System.currentTimeMillis();
        BusinessTransaction transaction = new BusinessTransaction();
        transaction.batteryId = battery.id;
        transaction.partnerCreditedCents = partnerCreditedCents;
        transaction.type = BusinessTransaction.TYPE_RENTAL;
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(transactionsRef.getKey() + "/" + transactionsRef.push().getKey(), transaction);
        updateMap.put(batteriesRef.getKey() + "/" + battery.id, battery);
        RentBatteryResult result = new RentBatteryResult();
        result.transaction = transaction;
        result.battery = battery;
        return RxDatabaseReference.updateChildren(prefixRef, updateMap).toSingleDefault(result);
    }

    public Single<RxOptional<Battery>> findBatteryByQr(String batteryQr) {
        Query batteryByQrQuery = batteriesRef.orderByChild("qr").equalTo(batteryQr);
        return RxQuery.valueEvents(batteryByQrQuery)
                .map(RxQuery.FIRST_CHILD_MAPPER)
                .map(optional -> optional.flatMap(BATTERY_MAPPER))
                .firstElement()
                .toSingle();
    }

    public PrepareRentBatteryResult toPrepareRentBatteryResult(Battery battery) {
        PrepareRentBatteryResult result = new PrepareRentBatteryResult();
        if (battery == null) {
            result.error = "Battery does not exist";
        } else {
            result.battery = battery;
            result.renterCreditedCents = -1200;
            result.renterCashCreditedCents = -1200;
            result.partnerCreditedCents = 40;
        }
        return result;
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

    public Single<ReturnBatteryResult> returnBattery(String batteryQr) {
        Single<PrepareReturnBatteryResult> prepareReturnBatteryResultSingle = prepareReturnBattery(batteryQr);
        return prepareReturnBatteryResultSingle.flatMap(prepareReturnBatteryResult -> {
            if (prepareReturnBatteryResult.error != null) {
                ReturnBatteryResult result = new ReturnBatteryResult();
                result.error = prepareReturnBatteryResult.error;
                return Single.just(result);
            } else {
                return returnBattery(prepareReturnBatteryResult.battery,
                        prepareReturnBatteryResult.partnerCreditedCents);
            }
        });
    }

    public Single<PrepareReturnBatteryResult> prepareReturnBattery(String batteryQr) {
        return findBatteryByQr(batteryQr).map(batteryOptional -> toPrepareReturnBatteryResult(batteryOptional.value));
    }

    private Single<ReturnBatteryResult> returnBattery(Battery battery, int partnerCreditedCents) {
        battery.rentalTime = 0;
        BusinessTransaction transaction = new BusinessTransaction();
        transaction.batteryId = battery.id;
        transaction.partnerCreditedCents = partnerCreditedCents;
        transaction.type = "return";
        ReturnBatteryResult result = new ReturnBatteryResult();
        result.transaction = transaction;
        return RxDatabaseReference.setValue(transactionsRef.push(), transaction).toSingleDefault(result);
    }

    public PrepareReturnBatteryResult toPrepareReturnBatteryResult(Battery battery) {
        PrepareReturnBatteryResult result = new PrepareReturnBatteryResult();
        if (battery == null) {
            result.error = "Battery does not exist";
        } else {
            result.battery = battery;
            result.renterCreditedCents = 800;
            result.renterCashCreditedCents = 800;
            result.partnerCreditedCents = 40;
        }
        return result;
    }

    private static class CreatePartnerInput {
        public final Partner partner;

        CreatePartnerInput(Partner partner) {
            this.partner = partner;
        }
    }

    private static class CreatePartnerOutput {
        public String error;
        public String partnerId;
    }

    private static class UpdatePartnerInput {
        public final String partnerId;
        public final Partner partner;

        private UpdatePartnerInput(String partnerId, Partner partner) {
            this.partnerId = partnerId;
            this.partner = partner;
        }
    }

    private static class UpdatePartnerOutput {
        public String error;
    }

    private static class DeletePartnerInput {
        public String partnerId;

        public DeletePartnerInput(String partnerId) {
            this.partnerId = partnerId;
        }
    }

    private static class DeletePartnerOutput {
        public String error;
    }

    public static class RentBatteryResult {
        public String error;
        public BusinessTransaction transaction;
        public Battery battery;
    }

    public static class PrepareRentBatteryResult {
        public String error;
        public Battery battery;
        public int renterCreditedCents;
        public int partnerCreditedCents;
        public int renterCashCreditedCents;
    }

    public static class PrepareReturnBatteryResult {
        public String error;
        public Battery battery;
        public int renterCreditedCents;
        public int partnerCreditedCents;
        public int renterCashCreditedCents;
    }

    public static class ReturnBatteryResult {
        public String error;
        public BusinessTransaction transaction;
    }
}
