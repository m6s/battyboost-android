package info.mschmitt.battyboost.core;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.core.entities.Partner;

/**
 * @author Matthias Schmitt
 */
public class BattyboostClient {
    private final FirebaseDatabase database;

    public BattyboostClient(FirebaseDatabase database) {
        this.database = database;
    }

    public void addPartner(Partner partner) {
        DatabaseReference partners = database.getReference("partners");
        DatabaseReference partnerRef = partners.push();
        partnerRef.setValue(partner);
    }
}
