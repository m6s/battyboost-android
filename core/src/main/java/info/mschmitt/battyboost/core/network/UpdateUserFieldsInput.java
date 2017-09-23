package info.mschmitt.battyboost.core.network;

/**
 * @author Matthias Schmitt
 */
class UpdateUserFieldsInput {
    public final String userId;
    public String bankAccountOwner;
    public boolean updateBankAccountOwner;
    public String iban;
    public boolean updateIban;
    public String photoUrl;
    public boolean updatePhotoUrl;
    public String email;
    public boolean updateEmail;
    public String displayName;
    public boolean updateDisplayName;

    UpdateUserFieldsInput(String userId) {
        this.userId = userId;
    }
}
