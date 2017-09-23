package info.mschmitt.battyboost.core.network;

import info.mschmitt.battyboost.core.entities.Partner;

/**
 * @author Matthias Schmitt
 */
class UpdatePartnerInput {
    public final String partnerId;
    public final Partner partner;

    UpdatePartnerInput(String partnerId, Partner partner) {
        this.partnerId = partnerId;
        this.partner = partner;
    }
}
