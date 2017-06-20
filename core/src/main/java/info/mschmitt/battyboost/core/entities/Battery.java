package info.mschmitt.battyboost.core.entities;

import android.databinding.BaseObservable;

import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class Battery extends BaseObservable implements Serializable {
    public String qr;
    public long manufacturingTime;
    public int chargeCycleCount;
    public long borrowTime;
}
