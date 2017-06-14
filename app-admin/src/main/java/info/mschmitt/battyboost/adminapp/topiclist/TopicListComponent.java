package info.mschmitt.battyboost.adminapp.topiclist;

import info.mschmitt.battyboost.adminapp.Router;

/**
 * @author Matthias Schmitt
 */
public class TopicListComponent {
    private final Router router;
    private final TopicListFragment.OnTopicSelectedListener onTopicSelectedListener;

    public TopicListComponent(Router router, TopicListFragment.OnTopicSelectedListener onTopicSelectedListener) {
        this.router = router;
        this.onTopicSelectedListener = onTopicSelectedListener;
    }

    public void inject(TopicListFragment fragment) {
        fragment.router = router;
        fragment.onTopicSelectedListener = onTopicSelectedListener;
        fragment.injected = true;
    }
}
