package info.mschmitt.battyboost.core.entities;

import android.databinding.BaseObservable;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class BattyboostUser extends BaseObservable implements Serializable {
    @Exclude public String id;
    public String qr;
    public int balanceCents;
    public String bankAccountOwner;
    public String iban;
    public String photoUrl;
    public String email;
    public String displayName;
}
