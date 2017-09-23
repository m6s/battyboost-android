package info.mschmitt.battyboost.core.network;

import info.mschmitt.battyboost.core.entities.BusinessUser;

/**
 * @author Matthias Schmitt
 */
class UpdateUserInput {
    public final String userId;
    public final BusinessUser user;

    UpdateUserInput(String userId, BusinessUser user) {
        this.userId = userId;
        this.user = user;
    }
}
