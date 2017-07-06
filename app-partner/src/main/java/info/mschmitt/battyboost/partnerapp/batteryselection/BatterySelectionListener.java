package info.mschmitt.battyboost.partnerapp.batteryselection;

import android.support.v4.app.Fragment;
import info.mschmitt.battyboost.core.entities.Battery;

/**
 * @author Matthias Schmitt
 */
public interface BatterySelectionListener {
    void onBatterySelected(Fragment sender, Battery battery);
}
