package info.mschmitt.battyboost.adminapp.battery;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import info.mschmitt.battyboost.core.entities.Battery;

import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class BatteryViewModel extends BaseObservable implements Serializable {
    @Bindable public Battery battery;
}
