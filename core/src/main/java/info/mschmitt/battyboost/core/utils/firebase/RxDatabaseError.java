package info.mschmitt.battyboost.core.utils.firebase;

import com.google.firebase.database.DatabaseError;

/**
 * @author Matthias Schmitt
 */
public class RxDatabaseError extends Throwable {
    public final DatabaseError databaseError;

    public RxDatabaseError(DatabaseError databaseError) {
        this.databaseError = databaseError;
    }
}
