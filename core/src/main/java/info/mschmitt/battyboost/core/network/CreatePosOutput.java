package info.mschmitt.battyboost.core.network;

import info.mschmitt.battyboost.core.entities.BattyboostError;

/**
 * @author Matthias Schmitt
 */
class CreatePosOutput implements ErrorOutput {
    public String posId;
    public BattyboostError error;

    @Override
    public BattyboostError getError() {
        return error;
    }
}
