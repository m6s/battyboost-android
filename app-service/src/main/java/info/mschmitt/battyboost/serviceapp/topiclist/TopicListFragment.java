package info.mschmitt.battyboost.serviceapp.topiclist;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import info.mschmitt.battyboost.serviceapp.Router;
import info.mschmitt.battyboost.serviceapp.databinding.TopicListViewBinding;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class TopicListFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public OnTopicSelectedListener onTopicSelectedListener;
    @Inject public boolean injected;

    public static Fragment newInstance() {
        return new TopicListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!injected) {
            throw new IllegalStateException("Not injected");
        }
        super.onCreate(savedInstanceState);
        viewModel = savedInstanceState == null ? new ViewModel()
                : (ViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TopicListViewBinding binding = TopicListViewBinding.inflate(inflater, container, false);
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_VIEW_MODEL, viewModel);
    }

    public void onPartnersClick() {
        setTopic(Topic.PARTNERS);
        onTopicSelectedListener.onTopicSelected(Topic.PARTNERS);
    }

    public void setTopic(Topic topic) {
        viewModel.topic = topic;
        viewModel.notifyChange();
    }

    public void onPosClick() {
        setTopic(Topic.POS_LIST);
        onTopicSelectedListener.onTopicSelected(Topic.POS_LIST);
    }

    public enum Topic {
        PARTNERS, POS_LIST
    }

    public interface OnTopicSelectedListener {
        void onTopicSelected(Topic topic);
    }

    public static class ViewModel extends BaseObservable implements Serializable {
        @Bindable public Topic topic = Topic.PARTNERS;
    }
}
