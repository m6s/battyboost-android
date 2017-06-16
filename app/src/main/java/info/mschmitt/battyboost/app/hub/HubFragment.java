package info.mschmitt.battyboost.app.hub;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.app.R;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.app.databinding.HubViewBinding;
import info.mschmitt.battyboost.app.map.MapFragment;
import info.mschmitt.battyboost.app.profile.ProfileFragment;
import info.mschmitt.battyboost.app.schedule.ScheduleFragment;
import info.mschmitt.battyboost.core.BattyboostClient;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.WeakHashMap;

/**
 * @author Matthias Schmitt
 */
public class HubFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final int RC_SIGN_IN = 123;
    private final WeakHashMap<Fragment, Void> injectedFragments = new WeakHashMap<>();
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public FirebaseDatabase database;
    @Inject public BattyboostClient client;
    @Inject public FirebaseAuth auth;
    @Inject public AuthUI authUI;
    @Inject public HubComponent component;
    @Inject public boolean injected;

    public static Fragment newInstance() {
        return new HubFragment();
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        if (injectedFragments.containsKey(childFragment)) {
            return;
        }
        if (childFragment instanceof MapFragment) {
            MapFragment mapFragment = (MapFragment) childFragment;
            component.plus(mapFragment).inject(mapFragment);
        } else if (childFragment instanceof ScheduleFragment) {
            ScheduleFragment scheduleFragment = (ScheduleFragment) childFragment;
            component.plus(scheduleFragment).inject(scheduleFragment);
        } else if (childFragment instanceof ProfileFragment) {
            ProfileFragment profileFragment = (ProfileFragment) childFragment;
            component.plus(profileFragment).inject(profileFragment);
        }
        injectedFragments.put(childFragment, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!injected) {
            throw new IllegalStateException("Not injected");
        }
        super.onCreate(savedInstanceState);
        viewModel = savedInstanceState == null ? new ViewModel()
                : (ViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HubViewBinding binding = HubViewBinding.inflate(inflater, container, false);
        binding.setFragment(this);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        if (getChildFragmentManager().findFragmentById(R.id.navigationContentView) == null) {
            router.showMap(this);
        }
        updateActionBar();
        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_VIEW_MODEL, viewModel);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.hub, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem signInOutMenuItem = menu.findItem(R.id.menu_item_sign_in_out);
        if (auth.getCurrentUser() != null) {
            signInOutMenuItem.setTitle("Sign out");
            signInOutMenuItem.setOnMenuItemClickListener(this::onSignOutMenuItemClick);
        } else {
            signInOutMenuItem.setTitle("Sign in");
            signInOutMenuItem.setOnMenuItemClickListener(this::onSignInMenuItemClick);
        }
    }

    private void updateActionBar() {
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.navigationContentView);
        ActionBar actionBar = getSupportActionBar();
        if (fragment instanceof MapFragment) {
            actionBar.setTitle("Map");
        } else if (fragment instanceof ScheduleFragment) {
            actionBar.setTitle("Schedule");
        } else if (fragment instanceof ProfileFragment) {
            actionBar.setTitle("Profile");
        }
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    private boolean onSignInMenuItemClick(MenuItem menuItem) {
        startActivityForResult(authUI.createSignInIntentBuilder().build(), RC_SIGN_IN);
        return true;
    }

    private boolean onSignOutMenuItemClick(MenuItem menuItem) {
        authUI.signOut(getActivity()).addOnSuccessListener(ignore -> {
            if (getView() != null) {Snackbar.make(getView(), "Signed out", Snackbar.LENGTH_SHORT).show();}
        });
        return true;
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                router.showMap(this);
                break;
            case R.id.action_schedule:
                router.showSchedule(this);
                break;
            case R.id.action_profile:
                router.showProfile(this);
                break;
        }
        updateActionBar();
        return true;
    }

    public static class ViewModel extends BaseObservable implements Serializable {
        @Bindable public String text;
    }
}
