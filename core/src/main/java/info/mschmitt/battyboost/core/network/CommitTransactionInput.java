package info.mschmitt.battyboost.core.network;

import info.mschmitt.battyboost.core.entities.BattyboostTransaction;

/**
 * @author Matthias Schmitt
 */
class CommitTransactionInput {
    public final BattyboostTransaction transaction;

    CommitTransactionInput(BattyboostTransaction transaction) {
        this.transaction = transaction;
    }
}
