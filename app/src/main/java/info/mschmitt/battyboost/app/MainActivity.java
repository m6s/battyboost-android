package info.mschmitt.battyboost.app;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import info.mschmitt.battyboost.app.hub.HubFragment;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.WeakHashMap;

/**
 * @author Matthias Schmitt
 */
public class MainActivity extends AppCompatActivity {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private final WeakHashMap<Fragment, Void> injectedFragments = new WeakHashMap<>();
    public ViewModel viewModel;
    @Inject public MainActivityComponent component;
    @Inject public BattyboostApplicationComponent applicationComponent;
    @Inject public Router router;
    @Inject public boolean injected;
    private boolean postResumed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BattyboostApplication application = (BattyboostApplication) getApplication();
        application.onAttachActivity(this);
        super.onCreate(savedInstanceState);
        viewModel = savedInstanceState == null ? new ViewModel()
                : (ViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        DataBindingUtil.setContentView(this, R.layout.main_activity);
        if (savedInstanceState == null) {
            router.showHub(this);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        postResumed = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_VIEW_MODEL, viewModel);
    }

    @Override
    public void onBackPressed() {
        if (!postResumed) {
            // https://www.reddit.com/r/androiddev/comments/4d2aje/ever_launched_a_fragmenttransaction_in_response/
            // https://developer.android.com/topic/libraries/support-library/revisions.html#26-0-0-beta1
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        postResumed = false;
        super.onPause();
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        if (injectedFragments.containsKey(childFragment)) {
            return;
        }
        if (childFragment instanceof HubFragment) {
            HubFragment mapFragment = (HubFragment) childFragment;
            applicationComponent.plus(mapFragment).inject(mapFragment);
        }
        injectedFragments.put(childFragment, null);
    }

    private static class ViewModel implements Serializable {}
}
