package info.mschmitt.battyboost.serviceapp.topiclist;

import info.mschmitt.battyboost.serviceapp.Router;

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
