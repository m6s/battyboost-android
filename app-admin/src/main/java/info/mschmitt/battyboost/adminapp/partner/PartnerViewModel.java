package info.mschmitt.battyboost.adminapp.partner;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import info.mschmitt.battyboost.core.entities.Partner;

import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class PartnerViewModel extends BaseObservable implements Serializable {
    @Bindable public Partner partner;
}
