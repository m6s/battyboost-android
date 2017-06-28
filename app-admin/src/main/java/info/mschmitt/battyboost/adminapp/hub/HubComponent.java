package info.mschmitt.battyboost.adminapp.hub;

import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.adminapp.batterylist.BatteryListComponent;
import info.mschmitt.battyboost.adminapp.batterylist.BatteryListFragment;
import info.mschmitt.battyboost.adminapp.drawer.DrawerComponent;
import info.mschmitt.battyboost.adminapp.drawer.DrawerFragment;
import info.mschmitt.battyboost.adminapp.partnerlist.PartnerListComponent;
import info.mschmitt.battyboost.adminapp.partnerlist.PartnerListFragment;
import info.mschmitt.battyboost.adminapp.poslist.PosListComponent;
import info.mschmitt.battyboost.adminapp.poslist.PosListFragment;
import info.mschmitt.battyboost.adminapp.userlist.UserListComponent;
import info.mschmitt.battyboost.adminapp.userlist.UserListFragment;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class HubComponent {
    private final Router router;
    private final FirebaseDatabase database;
    private final BattyboostClient client;

    public HubComponent(Router router, FirebaseDatabase database, BattyboostClient client) {
        this.router = router;
        this.database = database;
        this.client = client;
    }

    public void inject(HubFragment hubFragment) {
        hubFragment.router = router;
        hubFragment.component = this;
        hubFragment.injected = true;
    }

    public DrawerComponent plus(DrawerFragment fragment) {
        return new DrawerComponent(router);
    }

    public PartnerListComponent plus(PartnerListFragment partnerListFragment) {
        return new PartnerListComponent(router, database, client);
    }

    public PosListComponent plus(PosListFragment posListFragment) {
        return new PosListComponent(router, database, client);
    }

    public UserListComponent plus(UserListFragment userListFragment) {
        return new UserListComponent(router, database, client);
    }

    public BatteryListComponent plus(BatteryListFragment userListFragment) {
        return new BatteryListComponent(router, database, client);
    }
}
