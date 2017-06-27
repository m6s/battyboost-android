package info.mschmitt.battyboost.partnerapp;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import info.mschmitt.battyboost.core.entities.DatabaseUser;

/**
 * @author Matthias Schmitt
 */
public class Cache extends BaseObservable {
    @Bindable public DatabaseUser databaseUser;
    @Bindable public boolean initialized;
}
