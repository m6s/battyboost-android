package info.mschmitt.battyboost.app;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import info.mschmitt.battyboost.app.hub.HubFragment;
import info.mschmitt.battyboost.app.photo.PhotoFragment;
import info.mschmitt.battyboost.app.settings.SettingsFragment;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.utils.firebase.RxAuth;
import info.mschmitt.battyboost.core.utils.firebase.RxDatabaseReference;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

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
    @Inject public FirebaseAuth auth;
    @Inject public BattyboostClient client;
    @Inject public Router router;
    @Inject public Store store;
    @Inject public boolean injected;
    private boolean postResumed;
    private CompositeDisposable compositeDisposable;

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
        compositeDisposable.dispose();
        postResumed = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
        Disposable disposable =
                RxAuth.userChanges(auth).filter(optional -> optional.value == null).subscribe(ignore -> {
                    store.databaseUser = null;
                    store.initialized = true;
                    store.notifyChange();
                });
        compositeDisposable.add(disposable);
        disposable = RxAuth.userChanges(auth)
                .filter(optional -> optional.value != null)
                .map(optional -> optional.value)
                .switchMap(
                        firebaseUser -> RxDatabaseReference.valueEvents(client.usersRef.child(firebaseUser.getUid())))
                .map(BattyboostClient.DATABASE_USER_MAPPER)
                .subscribe(optional -> {
                    store.databaseUser = optional.value;
                    store.initialized = true;
                    store.notifyChange();
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        if (injectedFragments.containsKey(childFragment)) {
            return;
        }
        if (childFragment instanceof HubFragment) {
            HubFragment hubFragment = (HubFragment) childFragment;
            component.plus(hubFragment).inject(hubFragment);
        } else if (childFragment instanceof SettingsFragment) {
            SettingsFragment settingsFragment = (SettingsFragment) childFragment;
            component.plus(settingsFragment).inject(settingsFragment);
        } else if (childFragment instanceof PhotoFragment) {
            PhotoFragment photoFragment = (PhotoFragment) childFragment;
            component.plus(photoFragment).inject(photoFragment);
        }
        injectedFragments.put(childFragment, null);
    }

    private static class ViewModel implements Serializable {}
}
