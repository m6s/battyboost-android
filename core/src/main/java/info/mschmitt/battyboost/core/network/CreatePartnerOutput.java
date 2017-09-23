package info.mschmitt.battyboost.core.network;

import info.mschmitt.battyboost.core.entities.BattyboostError;

/**
 * @author Matthias Schmitt
 */
class CreatePartnerOutput implements ErrorOutput {
    public String partnerId;
    public BattyboostError error;

    @Override
    public BattyboostError getError() {
        return null;
    }
}
