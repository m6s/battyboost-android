package info.mschmitt.battyboost.app.profile;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.app.R;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.app.databinding.ProfileViewBinding;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.AuthUser;
import info.mschmitt.battyboost.core.entities.DatabaseUser;
import info.mschmitt.battyboost.core.utils.firebase.RxAuth;
import info.mschmitt.battyboost.core.utils.firebase.RxDatabaseReference;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class ProfileFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public FirebaseDatabase database;
    @Inject public BattyboostClient client;
    @Inject public FirebaseAuth auth;
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
        viewModel = savedInstanceState == null ? new ViewModel()
                : (ViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setFirebaseUser(auth.getCurrentUser());
        ProfileViewBinding binding = ProfileViewBinding.inflate(inflater, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            onSignedOut();
            return;
        }
        setFirebaseUser(firebaseUser);
        Disposable disposable =
                RxAuth.userChanges(auth).filter(optional -> optional.value == null).subscribe(ignore -> onSignedOut());
        compositeDisposable.add(disposable);
        DatabaseReference usersRef = database.getReference("users").child(firebaseUser.getUid());
        disposable = RxDatabaseReference.valueEvents(usersRef)
                .filter(DataSnapshot::exists)
                .map(BattyboostClient.DATABASE_USER_MAPPER)
                .subscribe(this::setDatabaseUser);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_sign_out);
        menuItem.setOnMenuItemClickListener(this::onSignOutMenuItemClick);
    }

    private void onSignedOut() {
        viewModel.signedOut = true;
        viewModel.notifyChange();
        router.goBack(this);
    }

    private void setFirebaseUser(FirebaseUser firebaseUser) {
        viewModel.authUser = new AuthUser(firebaseUser);
        viewModel.notifyChange();
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    private void setDatabaseUser(DatabaseUser databaseUser) {
        viewModel.databaseUser = databaseUser;
        viewModel.notifyChange();
    }

    private boolean onSignOutMenuItemClick(MenuItem menuItem) {
        authUI.signOut(getActivity());
        return true;
    }

    public void goUp() {
        router.goUp(this);
    }

    public static class ViewModel extends BaseObservable implements Serializable {
        @Bindable public DatabaseUser databaseUser;
        @Bindable public AuthUser authUser;
        @Bindable public boolean signedOut;
    }
}
