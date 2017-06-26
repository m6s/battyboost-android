package info.mschmitt.battyboost.core.entities;

import android.databinding.BaseObservable;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class Partner extends BaseObservable implements Serializable {
    @Exclude public String id;
    public String name;
    public int balanceCents;
    public String adminId;
    public String posId;
}
