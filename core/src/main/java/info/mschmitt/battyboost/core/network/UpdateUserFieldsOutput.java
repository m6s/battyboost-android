package info.mschmitt.battyboost.core.network;

import info.mschmitt.battyboost.core.entities.BattyboostError;

/**
 * @author Matthias Schmitt
 */
class UpdateUserFieldsOutput implements ErrorOutput {
    public BattyboostError error;

    @Override
    public BattyboostError getError() {
        return error;
    }
}
