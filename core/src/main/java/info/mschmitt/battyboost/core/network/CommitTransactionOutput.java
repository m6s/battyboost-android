package info.mschmitt.battyboost.core.network;

import info.mschmitt.battyboost.core.entities.BattyboostError;
import info.mschmitt.battyboost.core.entities.BattyboostTransaction;

/**
 * @author Matthias Schmitt
 */
class CommitTransactionOutput implements ErrorOutput {
    public BattyboostError error;
    public String transactionId;
    public BattyboostTransaction transaction;

    @Override
    public BattyboostError getError() {
        return error;
    }
}
