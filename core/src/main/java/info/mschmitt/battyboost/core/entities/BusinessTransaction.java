package info.mschmitt.battyboost.core.entities;

import android.databinding.BaseObservable;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class BusinessTransaction extends BaseObservable implements Serializable {
    @Exclude public String id;
}
