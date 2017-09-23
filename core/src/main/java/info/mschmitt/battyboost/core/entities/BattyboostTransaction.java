package info.mschmitt.battyboost.core.entities;

import android.databinding.BaseObservable;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class BattyboostTransaction extends BaseObservable implements Serializable {
    public static final String TYPE_RENTAL = "rental";
    public static final String TYPE_RETURN = "return";
    public static final String TYPE_DELIVERY = "delivery";
    public static final String TYPE_COLLECTION = "collection";
    @Exclude public String id;
    public String type;
    public String batteryId;
    public String partnerId;
    public int partnerCreditedCents;
    public String cashierId;
    public String conflictTransactionId;
    public long time;
}
