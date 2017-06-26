package info.mschmitt.battyboost.app.schedule;

import android.databinding.BaseObservable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.app.R;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.app.Store;
import info.mschmitt.battyboost.app.databinding.ScheduleViewBinding;
import info.mschmitt.battyboost.core.BattyboostClient;
import io.reactivex.disposables.CompositeDisposable;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class ScheduleFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final int RC_SIGN_IN = 123;
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public FirebaseDatabase database;
    @Inject public BattyboostClient client;
    @Inject public FirebaseAuth auth;
    @Inject public AuthUI authUI;
    @Inject public Store store;
    @Inject public boolean injected;
    private CompositeDisposable compositeDisposable;

    public static Fragment newInstance() {
        return new ScheduleFragment();
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
        ScheduleViewBinding binding = ScheduleViewBinding.inflate(inflater, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Schedule");
        binding.setFragment(this);
        return binding.getRoot();
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_VIEW_MODEL, viewModel);
    }

    @Override
    public void onPause() {
        compositeDisposable.dispose();
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.schedule, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_settings);
        menuItem.setOnMenuItemClickListener(this::onProfileMenuItemClick);
    }

    private boolean onProfileMenuItemClick(MenuItem menuItem) {
        router.showSettings(this);
        return true;
    }

    public void onSignInClick() {
        startActivityForResult(authUI.createSignInIntentBuilder().build(), RC_SIGN_IN);
    }

    public static class ViewModel extends BaseObservable implements Serializable {}
}
