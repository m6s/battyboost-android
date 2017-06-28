package info.mschmitt.battyboost.adminapp.user;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import info.mschmitt.battyboost.core.entities.BusinessUser;

import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class UserViewModel extends BaseObservable implements Serializable {
    @Bindable public BusinessUser user;
}
