package info.mschmitt.battyboost.core.network;

import android.util.Pair;
import com.google.firebase.auth.FirebaseAuth;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Matthias Schmitt
 */
public class BattyboostClient {
    public static final Function<DataSnapshot, RxOptional<BattyboostUser>> DATABASE_USER_MAPPER = dataSnapshot -> {
        BattyboostUser battyboostUser = dataSnapshot.getValue(BattyboostUser.class);
        if (battyboostUser != null) {
            battyboostUser.id = dataSnapshot.getKey();
        }
        return new RxOptional<>(battyboostUser);
    };
    public static final Function<DataSnapshot, RxOptional<Partner>> PARTNER_MAPPER = dataSnapshot -> {
        Partner partner = dataSnapshot.getValue(Partner.class);
        if (partner != null) {
            partner.id = dataSnapshot.getKey();
        }
        return new RxOptional<>(partner);
    };
    public static final Function<DataSnapshot, List<Partner>> PARTNER_LIST_MAPPER = dataSnapshot -> {
        if (!dataSnapshot.exists()) {
            return Collections.emptyList();
        }
        ArrayList<Partner> partners = new ArrayList<>();
        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
            Partner partner = PARTNER_MAPPER.apply(childSnapshot).value;
            partners.add(partner);
        }
        return partners;
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
    public static final Function<DataSnapshot, RxOptional<BattyboostUser>> USER_MAPPER = dataSnapshot -> {
        BattyboostUser user = dataSnapshot.getValue(BattyboostUser.class);
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
    private volatile BattyboostUser user;

    public BattyboostClient(FirebaseDatabase database, FirebaseStorage storage, String prefix) {
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

    public Observable<RxOptional<BattyboostUser>> connect(FirebaseAuth auth) {
        return userChanges(usersRef, auth).doOnNext(optional -> user = optional.value);
    }

    private static Observable<RxOptional<BattyboostUser>> userChanges(DatabaseReference usersRef, FirebaseAuth auth) {
        return RxAuth.userChanges(auth).switchMap(optional -> {
            if (optional.value == null) {
                return Observable.just(RxOptional.<BattyboostUser>empty());
            }
            DatabaseReference userRef = usersRef.child(optional.value.getUid());
            return RxQuery.valueEvents(userRef).filter(DataSnapshot::exists).map(USER_MAPPER);
        });
    }

    public Single<String> createPartner(Partner partner) {
        return executeFunction("createPartner", new CreatePartnerInput(partner), CreatePartnerOutput.class).map(
                createPartnerOutput -> createPartnerOutput.partnerId);
    }

    private <InputT, OutputT extends ErrorOutput> Single<OutputT> executeFunction(String functionName, InputT input,
                                                                                  Class<OutputT> outputClass) {
        return Single.defer(() -> {
            if (user == null) {
                throw new NotSignedInException();
            }
            DatabaseReference executionRef = logicRef.child(user.id).child(functionName).push();
            DatabaseReference inputRef = executionRef.child("input");
            DatabaseReference outputRef = executionRef.child("output");
            return RxDatabaseReference.setValue(inputRef, input)
                    .andThen(RxQuery.valueEvents(outputRef)
                            .filter(DataSnapshot::exists)
                            .map(dataSnapshot -> dataSnapshot.getValue(outputClass))
                            .doOnNext(output -> {
                                if (output.getError() != null) {
                                    throw new ClientException(output.getError());
                                }
                            })
                            .firstOrError());
        });
    }

    public Completable updatePartner(String partnerId, Partner partner) {
        return executeFunction("updatePartner", new UpdatePartnerInput(partnerId, partner),
                UpdatePartnerOutput.class).toCompletable();
    }

    public Completable deletePartner(String partnerId) {
        return executeFunction("deletePartner", new DeletePartnerInput(partnerId),
                DeletePartnerOutput.class).toCompletable();
    }

    public Single<String> createPos(Pos pos) {
        return executeFunction("createPos", new CreatePosInput(pos), CreatePosOutput.class).map(
                createPartnerOutput -> createPartnerOutput.posId);
    }

    public Completable updatePos(String posId, Pos pos) {
        return executeFunction("updatePos", new UpdatePosInput(posId, pos), UpdatePosOutput.class).toCompletable();
    }

    public Completable deletePos(String posId) {
        return executeFunction("deletePos", new DeletePosInput(posId), DeletePosOutput.class).toCompletable();
    }

    public Single<String> createBattery(Battery battery) {
        return executeFunction("createBattery", new CreateBatteryInput(battery), CreateBatteryOutput.class).map(
                createPartnerOutput -> createPartnerOutput.batteryId);
    }

    public Completable updateBattery(String batteryId, Battery battery) {
        return executeFunction("updateBattery", new UpdateBatteryInput(batteryId, battery),
                UpdateBatteryOutput.class).toCompletable();
    }

    public Completable deleteBattery(String batteryId) {
        return executeFunction("deleteBattery", new DeleteBatteryInput(batteryId),
                DeleteBatteryOutput.class).toCompletable();
    }

    public Completable updateUser(String userId, BattyboostUser user) {
        return executeFunction("updateUser", new UpdateUserInput(userId, user), UpdateUserOutput.class).toCompletable();
    }

    public Completable updateUserEmail(String userId, String email) {
        UpdateUserFieldsInput input = new UpdateUserFieldsInput(userId);
        input.email = email;
        input.updateEmail = true;
        return executeFunction("updateUserFields", input, UpdateUserFieldsOutput.class).toCompletable();
    }

    public Completable updateUserDisplayName(String userId, String displayName) {
        UpdateUserFieldsInput input = new UpdateUserFieldsInput(userId);
        input.displayName = displayName;
        input.updateDisplayName = true;
        return executeFunction("updateUserFields", input, UpdateUserFieldsOutput.class).toCompletable();
    }

    public Completable updateUserIban(String userId, String iban) {
        UpdateUserFieldsInput input = new UpdateUserFieldsInput(userId);
        input.iban = iban;
        input.updateIban = true;
        return executeFunction("updateUserFields", input, UpdateUserFieldsOutput.class).toCompletable();
    }

    public Completable updateUserBankAccountOwner(String userId, String owner) {
        UpdateUserFieldsInput input = new UpdateUserFieldsInput(userId);
        input.bankAccountOwner = owner;
        input.updateBankAccountOwner = true;
        return executeFunction("updateUserFields", input, UpdateUserFieldsOutput.class).toCompletable();
    }

    public Completable updateUserPhotoUrl(String userId, String url) {
        UpdateUserFieldsInput input = new UpdateUserFieldsInput(userId);
        input.photoUrl = url;
        input.updatePhotoUrl = true;
        return executeFunction("updateUserFields", input, UpdateUserFieldsOutput.class).toCompletable();
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

    public Single<BattyboostTransaction> prepareRentBattery(String batteryId) {
        return Single.just(new BattyboostTransaction());
    }

    public Single<Pair<BattyboostTransaction, String>> commitTransaction(BattyboostTransaction transaction) {
        return Single.just(Pair.create(null, null));
    }

    public Single<RxOptional<Battery>> findBatteryByQr(String batteryQr) {
        Query batteryByQrQuery = batteriesRef.orderByChild("qr").equalTo(batteryQr);
        return RxQuery.valueEvents(batteryByQrQuery)
                .map(RxQuery.FIRST_CHILD_MAPPER)
                .map(optional -> optional.flatMap(BATTERY_MAPPER))
                .firstElement()
                .toSingle();
    }
}
