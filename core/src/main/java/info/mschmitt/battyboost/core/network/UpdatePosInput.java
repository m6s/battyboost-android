package info.mschmitt.battyboost.core.network;

import info.mschmitt.battyboost.core.entities.Pos;

/**
 * @author Matthias Schmitt
 */
class UpdatePosInput {
    public final String posId;
    public final Pos pos;

    UpdatePosInput(String posId, Pos pos) {
        this.posId = posId;
        this.pos = pos;
    }
}
