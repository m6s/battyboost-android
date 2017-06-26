package info.mschmitt.battyboost.app;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.DatabaseUser;
import io.reactivex.Completable;

/**
 * @author Matthias Schmitt
 */
public class Store extends BaseObservable {
    private final BattyboostClient client;
    @Bindable public DatabaseUser databaseUser;

    public Store(BattyboostClient client) {
        this.client = client;
    }

    public Completable updateDisplayName(String displayName) {
        databaseUser.displayName = displayName;
        databaseUser.notifyChange();
        return client.updateUser(databaseUser.id, databaseUser);
    }

    public Completable updateEmail(String email) {
        databaseUser.email = email;
        databaseUser.notifyChange();
        return client.updateUser(databaseUser.id, databaseUser);
    }

    public Completable updateBankAccountOwner(String owner) {
        databaseUser.bankAccountOwner = owner;
        databaseUser.notifyChange();
        return client.updateUser(databaseUser.id, databaseUser);
    }

    public Completable updateIban(String iban) {
        databaseUser.iban = iban;
        databaseUser.notifyChange();
        return client.updateUser(databaseUser.id, databaseUser);
    }

    public Completable updatePhotoUrl(String url) {
        databaseUser.photoUrl = url;
        databaseUser.notifyChange();
        return client.updateUser(databaseUser.id, databaseUser);
    }
}
