package info.mschmitt.battyboost.app.hub;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.database.*;
import info.mschmitt.battyboost.app.R;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.app.databinding.HubViewBinding;
import info.mschmitt.battyboost.app.map.MapFragment;
import info.mschmitt.battyboost.core.BattyboostClient;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.WeakHashMap;

/**
 * @author Matthias Schmitt
 */
public class HubFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private final WeakHashMap<Fragment, Void> injectedFragments = new WeakHashMap<>();
    public ViewModel viewModel;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public Router router;
    @Inject public HubComponent component;
    @Inject public boolean injected;

    public static Fragment newInstance() {
        return new HubFragment();
    }

    public void onClick() {
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        if (injectedFragments.containsKey(childFragment)) {
            return;
        }
        if (childFragment instanceof MapFragment) {
            MapFragment mapFragment = (MapFragment) childFragment;
            component.plus(mapFragment).inject(mapFragment);
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
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DatabaseReference partnersRef = database.getReference("partners");
        partnersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        HubViewBinding binding = HubViewBinding.inflate(inflater, container, false);
//        binding.bottomNavigationView.getMenu().getItem(1).setEnabled(false);
//        binding.bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        binding.setFragment(this);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        if (savedInstanceState == null) {
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

    private void updateActionBar() {
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.navigationContentView);
        ActionBar actionBar = getSupportActionBar();
        if (fragment instanceof MapFragment) {
            actionBar.setTitle("Map");
        }
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                router.showMap(this);
                break;
            case R.id.action_schedule:
                break;
            case R.id.action_balance:
                break;
        }
        return true;
    }

    public static class ViewModel extends BaseObservable implements Serializable {
        @Bindable public String text;
    }
}
