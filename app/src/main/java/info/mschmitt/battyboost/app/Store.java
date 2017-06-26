package info.mschmitt.battyboost.app;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import info.mschmitt.battyboost.core.entities.DatabaseUser;

/**
 * @author Matthias Schmitt
 */
public class Store extends BaseObservable {
    @Bindable public boolean initialized;
    @Bindable public DatabaseUser databaseUser;
}
