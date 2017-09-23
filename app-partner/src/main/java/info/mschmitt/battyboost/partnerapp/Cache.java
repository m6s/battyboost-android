package info.mschmitt.battyboost.partnerapp;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import info.mschmitt.battyboost.core.entities.BattyboostUser;

import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class Cache extends BaseObservable implements Serializable {
    @Bindable public BattyboostUser user;
    @Bindable public boolean initialized;
}
