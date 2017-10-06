package info.mschmitt.battyboost.adminapp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import info.mschmitt.battyboost.adminapp.battery.BatteryFragment;
import info.mschmitt.battyboost.adminapp.batteryediting.BatteryEditingFragment;
import info.mschmitt.battyboost.adminapp.hub.HubFragment;
import info.mschmitt.battyboost.adminapp.partner.PartnerFragment;
import info.mschmitt.battyboost.adminapp.partnerediting.PartnerEditingFragment;
import info.mschmitt.battyboost.adminapp.pos.PosFragment;
import info.mschmitt.battyboost.adminapp.posediting.PosEditingFragment;
import info.mschmitt.battyboost.adminapp.posselection.PosSelectionFragment;
import info.mschmitt.battyboost.adminapp.user.UserFragment;
import info.mschmitt.battyboost.adminapp.userediting.UserEditingFragment;
import info.mschmitt.battyboost.core.network.BattyboostClient;
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
    @Inject public MainActivityComponent component;
    @Inject public BattyboostServiceApplicationComponent applicationComponent;
    @Inject public Router router;
    @Inject public boolean injected;
    @Inject public BattyboostClient client;
    @Inject public FirebaseAuth auth;
    public ViewModel viewModel;
    private boolean postResumed;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BattyboostServiceApplication application = (BattyboostServiceApplication) getApplication();
        application.onAttachActivity(this);
        if (!injected) {
            throw new IllegalStateException("Not injected");
        }
        onPreCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.main_activity);
        if (savedInstanceState == null) {
            router.showHub(this);
        }
    }

    private void onPreCreate(Bundle savedInstanceState) {
        viewModel = savedInstanceState == null ? new ViewModel()
                : (ViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
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
        Disposable disposable = client.connect(auth).subscribe();
        compositeDisposable.add(disposable);
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        if (injectedFragments.containsKey(childFragment)) {
            return;
        }
        if (childFragment instanceof HubFragment) {
            HubFragment hubFragment = (HubFragment) childFragment;
            applicationComponent.plus(hubFragment).inject(hubFragment);
        } else if (childFragment instanceof PartnerFragment) {
            PartnerFragment partnerFragment = (PartnerFragment) childFragment;
            applicationComponent.plus(partnerFragment).inject(partnerFragment);
        } else if (childFragment instanceof PartnerEditingFragment) {
            PartnerEditingFragment partnerEditingFragment = (PartnerEditingFragment) childFragment;
            applicationComponent.plus(partnerEditingFragment).inject(partnerEditingFragment);
        } else if (childFragment instanceof PosFragment) {
            PosFragment posFragment = (PosFragment) childFragment;
            applicationComponent.plus(posFragment).inject(posFragment);
        } else if (childFragment instanceof PosEditingFragment) {
            PosEditingFragment posEditingFragment = (PosEditingFragment) childFragment;
            applicationComponent.plus(posEditingFragment).inject(posEditingFragment);
        } else if (childFragment instanceof PosSelectionFragment) {
            PosSelectionFragment posSelectionFragment = (PosSelectionFragment) childFragment;
            applicationComponent.plus(posSelectionFragment).inject(posSelectionFragment);
        } else if (childFragment instanceof UserFragment) {
            UserFragment userFragment = (UserFragment) childFragment;
            applicationComponent.plus(userFragment).inject(userFragment);
        } else if (childFragment instanceof UserEditingFragment) {
            UserEditingFragment userEditingFragment = (UserEditingFragment) childFragment;
            applicationComponent.plus(userEditingFragment).inject(userEditingFragment);
        } else if (childFragment instanceof BatteryFragment) {
            BatteryFragment batteryFragment = (BatteryFragment) childFragment;
            applicationComponent.plus(batteryFragment).inject(batteryFragment);
        } else if (childFragment instanceof BatteryEditingFragment) {
            BatteryEditingFragment batteryEditingFragment = (BatteryEditingFragment) childFragment;
            applicationComponent.plus(batteryEditingFragment).inject(batteryEditingFragment);
        }
        injectedFragments.put(childFragment, null);
    }

    private static class ViewModel implements Serializable {}
}
