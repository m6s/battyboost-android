package info.mschmitt.battyboost.core.entities;

import android.databinding.BaseObservable;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class Battery extends BaseObservable implements Serializable {
    @Exclude public String id;
    public String qr;
    public long manufacturingTime;
    public int chargeCycleCount;
    public long borrowTime;
}
