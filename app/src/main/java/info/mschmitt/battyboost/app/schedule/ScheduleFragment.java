package info.mschmitt.battyboost.app.schedule;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.app.databinding.ScheduleViewBinding;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.ObservableFirebaseUser;
import info.mschmitt.battyboost.core.utils.firebase.RxAuth;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class ScheduleFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final int RC_SIGN_IN = 123;
    public ViewModel viewModel;
    @Inject public FirebaseDatabase database;
    @Inject public BattyboostClient client;
    @Inject public RxAuth rxAuth;
    @Inject public AuthUI authUI;
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
        if (savedInstanceState == null) {
            viewModel = new ViewModel();
            setFirebaseUser(rxAuth.auth.getCurrentUser());
        } else {
            viewModel = (ViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        }
        setHasOptionsMenu(true);
    }

    private void setFirebaseUser(FirebaseUser firebaseUser) {
        viewModel.firebaseUser = firebaseUser == null ? null : new ObservableFirebaseUser(firebaseUser);
        viewModel.notifyChange();
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
        Disposable disposable = rxAuth.userChanges().subscribe(optional -> {
            setFirebaseUser(optional.value);
            viewModel.notifyChange();
        });
        compositeDisposable.add(disposable);
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

    public void onSignInClick() {
        startActivityForResult(authUI.createSignInIntentBuilder().build(), RC_SIGN_IN);
    }

    public static class ViewModel extends BaseObservable implements Serializable {
        @Bindable public String text;
        public ObservableFirebaseUser firebaseUser;

        @Bindable
        public boolean isSignedIn() {
            return firebaseUser != null;
        }
    }
}
