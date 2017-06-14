package info.mschmitt.battyboost.adminapp.hub;

import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.adminapp.partnerlist.PartnerListComponent;
import info.mschmitt.battyboost.adminapp.partnerlist.PartnerListFragment;
import info.mschmitt.battyboost.adminapp.poslist.PosListComponent;
import info.mschmitt.battyboost.adminapp.poslist.PosListFragment;
import info.mschmitt.battyboost.adminapp.topiclist.TopicListComponent;
import info.mschmitt.battyboost.adminapp.topiclist.TopicListFragment;
import info.mschmitt.battyboost.core.BattyboostClient;

/**
 * @author Matthias Schmitt
 */
public class HubComponent {
    private final Router router;
    private final TopicListFragment.OnTopicSelectedListener onTopicSelectedListener;
    private final FirebaseDatabase database;
    private final BattyboostClient client;

    public HubComponent(HubFragment fragment, Router router, FirebaseDatabase database, BattyboostClient client) {
        this.router = router;
        onTopicSelectedListener = fragment::onTopicSelected;
        this.database = database;
        this.client = client;
    }

    public void inject(HubFragment hubFragment) {
        hubFragment.router = router;
        hubFragment.component = this;
        hubFragment.injected = true;
    }

    public TopicListComponent plus(TopicListFragment fragment) {
        return new TopicListComponent(router, onTopicSelectedListener);
    }

    public PartnerListComponent plus(PartnerListFragment partnerListFragment) {
        return new PartnerListComponent(router, database, client);
    }

    public PosListComponent plus(PosListFragment posListFragment) {
        return new PosListComponent(router, database, client);
    }
}
