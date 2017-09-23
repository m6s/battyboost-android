package info.mschmitt.battyboost.core.network;

import info.mschmitt.battyboost.core.entities.BattyboostUser;

/**
 * @author Matthias Schmitt
 */
class UpdateUserInput {
    public final String userId;
    public final BattyboostUser user;

    UpdateUserInput(String userId, BattyboostUser user) {
        this.userId = userId;
        this.user = user;
    }
}
