package info.mschmitt.battyboost.app.profile;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.app.R;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.app.databinding.ProfileViewBinding;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.ObservableFirebaseUser;
import info.mschmitt.battyboost.core.entities.User;
import info.mschmitt.battyboost.core.utils.RxOptional;
import info.mschmitt.battyboost.core.utils.firebase.RxAuth;
import info.mschmitt.battyboost.core.utils.firebase.RxDatabaseReference;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class ProfileFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final int RC_SIGN_IN = 123;
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public FirebaseDatabase database;
    @Inject public BattyboostClient client;
    @Inject public RxAuth rxAuth;
    @Inject public AuthUI authUI;
    @Inject public boolean injected;
    private CompositeDisposable compositeDisposable;

    public static Fragment newInstance() {
        return new ProfileFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ProfileViewBinding binding = ProfileViewBinding.inflate(inflater, container, false);
        binding.setFragment(this);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
        Disposable disposable = rxAuth.userChanges().subscribe(optional -> setFirebaseUser(optional.value));
        compositeDisposable.add(disposable);
        disposable = rxAuth.userChanges()
                .switchMap(optional -> {
                    FirebaseUser firebaseUser = optional.value;
                    if (firebaseUser != null) {
                        DatabaseReference reference = database.getReference("users").child(firebaseUser.getUid());
                        return RxDatabaseReference.valueEvents(reference).map(RxOptional::new);
                    } else {
                        return Single.just(RxOptional.<DataSnapshot>empty()).toObservable();
                    }
                })
                .map(optional -> optional.map(
                        dataSnapshot -> dataSnapshot.exists() ? dataSnapshot.getValue(User.class) : null))
                .subscribe(optional -> setUser(optional.value));
        compositeDisposable.add(disposable);
    }

    private void setUser(User user) {
        viewModel.user = user;
        viewModel.notifyChange();
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
        inflater.inflate(R.menu.profile, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem signInOutMenuItem = menu.findItem(R.id.menu_item_sign_in_out);
        if (rxAuth.auth.getCurrentUser() != null) {
            signInOutMenuItem.setTitle("Sign out");
            signInOutMenuItem.setOnMenuItemClickListener(this::onSignOutMenuItemClick);
        } else {
            signInOutMenuItem.setTitle("Sign in");
            signInOutMenuItem.setOnMenuItemClickListener(this::onSignInMenuItemClick);
        }
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    private void setFirebaseUser(FirebaseUser firebaseUser) {
        viewModel.firebaseUser = firebaseUser == null ? null : new ObservableFirebaseUser(firebaseUser);
        viewModel.notifyChange();
    }

    private boolean onSignInMenuItemClick(MenuItem menuItem) {
        startActivityForResult(authUI.createSignInIntentBuilder().build(), RC_SIGN_IN);
        return true;
    }

    private boolean onSignOutMenuItemClick(MenuItem menuItem) {
        authUI.signOut(getActivity()).addOnSuccessListener(ignore -> router.notifySignedOut(this));
        return true;
    }

    public void onSignInClick() {
        startActivityForResult(authUI.createSignInIntentBuilder().build(), RC_SIGN_IN);
    }

    public static class ViewModel extends BaseObservable implements Serializable {
        @Bindable public User user;
        @Bindable public String text;
        @Bindable public String displayName;
        @Bindable public boolean signedIn;
        @Bindable public ObservableFirebaseUser firebaseUser;

        @Bindable
        public boolean isSignedIn() {
            return firebaseUser != null;
        }
    }
}
