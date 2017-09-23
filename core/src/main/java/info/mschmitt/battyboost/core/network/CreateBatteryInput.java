package info.mschmitt.battyboost.core.network;

import info.mschmitt.battyboost.core.entities.Battery;

/**
 * @author Matthias Schmitt
 */
class CreateBatteryInput {
    public final Battery battery;

    CreateBatteryInput(Battery battery) {
        this.battery = battery;
    }
}
