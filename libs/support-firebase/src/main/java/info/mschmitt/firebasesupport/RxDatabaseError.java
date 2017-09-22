package info.mschmitt.firebasesupport;

import com.google.firebase.database.DatabaseError;

/**
 * @author Matthias Schmitt
 */
public class RxDatabaseError extends Throwable {
    public final DatabaseError databaseError;

    public RxDatabaseError(DatabaseError databaseError) {
        super(databaseError.getMessage());
        this.databaseError = databaseError;
    }
}
