package info.mschmitt.battyboost.adminapp;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.adminapp.hub.HubComponent;
import info.mschmitt.battyboost.adminapp.hub.HubFragment;
import info.mschmitt.battyboost.adminapp.partner.PartnerComponent;
import info.mschmitt.battyboost.adminapp.partner.PartnerFragment;
import info.mschmitt.battyboost.adminapp.partnerediting.PartnerEditingComponent;
import info.mschmitt.battyboost.adminapp.partnerediting.PartnerEditingFragment;
import info.mschmitt.battyboost.adminapp.pos.PosComponent;
import info.mschmitt.battyboost.adminapp.pos.PosFragment;
import info.mschmitt.battyboost.adminapp.posediting.PosEditingComponent;
import info.mschmitt.battyboost.adminapp.posediting.PosEditingFragment;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class BattyboostServiceApplicationComponent {
    public final FirebaseDatabase database;
    public final FirebaseAuth auth;
    public final AuthUI authUI;
    public final BattyboostClient client;
    public final Router router;

    public BattyboostServiceApplicationComponent(BattyboostServiceApplication application) {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        authUI = AuthUI.getInstance();
        client = new BattyboostClient(database, auth);
        router = new Router();
    }

    public void inject(BattyboostServiceApplication application) {
        application.applicationComponent = this;
        application.injected = true;
    }

    public MainActivityComponent plus(MainActivity activity) {
        return new MainActivityComponent(this);
    }

    public HubComponent plus(HubFragment fragment) {
        return new HubComponent(fragment, router, database, client);
    }

    public PartnerComponent plus(PartnerFragment fragment) {
        return new PartnerComponent(router, database, client);
    }

    public PartnerEditingComponent plus(PartnerEditingFragment fragment) {
        return new PartnerEditingComponent(router, database, client);
    }

    public PosComponent plus(PosFragment fragment) {
        return new PosComponent(router, database, client);
    }

    public PosEditingComponent plus(PosEditingFragment fragment) {
        return new PosEditingComponent(router, database, client);
    }
}
