package info.mschmitt.battyboost.core.network;

import info.mschmitt.battyboost.core.entities.Battery;

/**
 * @author Matthias Schmitt
 */
class UpdateBatteryInput {
    public final String batteryId;
    public final Battery battery;

    UpdateBatteryInput(String batteryId, Battery battery) {
        this.batteryId = batteryId;
        this.battery = battery;
    }
}
