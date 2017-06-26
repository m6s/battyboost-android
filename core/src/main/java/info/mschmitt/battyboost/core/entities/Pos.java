package info.mschmitt.battyboost.core.entities;

import android.databinding.BaseObservable;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class Pos extends BaseObservable implements Serializable {
    @Exclude public String id;
    public String imageUrl;
    public String name;
    public String info;
    public String url;
    public double latitude;
    public double longitude;
    public int availableBatteryCount;
}
