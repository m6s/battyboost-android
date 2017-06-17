package info.mschmitt.battyboost.adminapp.pos;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import info.mschmitt.battyboost.core.entities.Pos;

import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class PosViewModel extends BaseObservable implements Serializable {
    @Bindable public Pos pos;
}
