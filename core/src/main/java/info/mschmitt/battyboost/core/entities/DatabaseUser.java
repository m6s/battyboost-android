package info.mschmitt.battyboost.core.entities;

import android.databinding.BaseObservable;

import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class DatabaseUser extends BaseObservable implements Serializable {
    public int balanceCents;
    public String bankAccountOwner;
    public String iban;
}
