package info.mschmitt.battyboost.core.entities;

import android.databinding.BaseObservable;

import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class Pos extends BaseObservable implements Serializable {
    public String imageUrl;
    public String name;
    public String info;
    public String url;
    public double latitude;
    public double longitude;
    public int availableBatteryCount;
}
