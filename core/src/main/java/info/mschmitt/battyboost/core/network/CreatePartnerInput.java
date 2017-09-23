package info.mschmitt.battyboost.core.network;

import info.mschmitt.battyboost.core.entities.Partner;

/**
 * @author Matthias Schmitt
 */
class CreatePartnerInput {
    public final Partner partner;

    CreatePartnerInput(Partner partner) {
        this.partner = partner;
    }
}
