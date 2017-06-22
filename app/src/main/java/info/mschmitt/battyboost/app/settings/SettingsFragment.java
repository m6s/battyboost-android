package info.mschmitt.battyboost.app.settings;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.app.databinding.SettingsViewBinding;
import info.mschmitt.battyboost.app.databinding.TextInputDialogBinding;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.AuthUser;
import info.mschmitt.battyboost.core.entities.DatabaseUser;
import info.mschmitt.battyboost.core.utils.RxOptional;
import info.mschmitt.battyboost.core.utils.firebase.RxAuth;
import info.mschmitt.battyboost.core.utils.firebase.RxAuthUI;
import info.mschmitt.battyboost.core.utils.firebase.RxDatabaseReference;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class SettingsFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final int RC_SIGN_IN = 123;
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public FirebaseDatabase database;
    @Inject public BattyboostClient client;
    @Inject public FirebaseAuth auth;
    @Inject public AuthUI authUI;
    @Inject public boolean injected;
    private CompositeDisposable compositeDisposable;

    public static Fragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!injected) {
            throw new IllegalStateException("Not injected");
        }
        super.onCreate(savedInstanceState);
        viewModel = savedInstanceState == null ? new ViewModel()
                : (ViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setFirebaseUser(auth.getCurrentUser());
        SettingsViewBinding binding = SettingsViewBinding.inflate(inflater, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Settings");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(0);
        actionBar.setHomeActionContentDescription(0);
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
        Observable<FirebaseUser> signedIn =
                RxAuth.userChanges(auth).filter(optional -> optional.value != null).map(optional -> optional.value);
        Observable<Object> signedOut =
                RxAuth.userChanges(auth).filter(optional -> optional.value == null).map(optional -> RxOptional.empty());
        Disposable disposable = signedIn.subscribe(this::setFirebaseUser);
        compositeDisposable.add(disposable);
        disposable = signedOut.subscribe(ignore -> {
            setFirebaseUser(null);
            setDatabaseUser(null);
        });
        compositeDisposable.add(disposable);
        disposable = signedIn.switchMap(firebaseUser -> {
            DatabaseReference usersRef = client.usersRef.child(firebaseUser.getUid());
            return RxDatabaseReference.valueEvents(usersRef);
        }).filter(DataSnapshot::exists).map(BattyboostClient.DATABASE_USER_MAPPER).subscribe(this::setDatabaseUser);
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

    private void setDatabaseUser(DatabaseUser databaseUser) {
        viewModel.databaseUser = databaseUser;
        viewModel.notifyChange();
    }

    private void setFirebaseUser(FirebaseUser firebaseUser) {
        viewModel.authUser = AuthUser.of(firebaseUser);
        viewModel.notifyChange();
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    public void onSignInClick() {
        startActivityForResult(authUI.createSignInIntentBuilder().build(), RC_SIGN_IN);
    }

    public void onSignOutClick() {
        Disposable disposable = RxAuthUI.signOut(authUI, getActivity())
                .subscribe(() -> Snackbar.make(getView(), "Signed out", Snackbar.LENGTH_SHORT).show());
        compositeDisposable.add(disposable);
    }

    public void onDisplayNameClick() {
        Context context = getView().getContext();
        TextInputDialogBinding binding = TextInputDialogBinding.inflate(LayoutInflater.from(context));
        binding.textInputLayout.setHint("Display name");
        binding.editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        binding.editText.append(viewModel.authUser.displayName != null ? viewModel.authUser.displayName : "");
        new AlertDialog.Builder(context).setView(binding.getRoot()).setPositiveButton("Save", (dialog, which) -> {
            String displayName = binding.editText.getText().toString();
            UserProfileChangeRequest request =
                    new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();
            // TODO Show saving indicator
            viewModel.authUser.displayName = displayName;
            viewModel.authUser.notifyChange();
            RxAuth.updateProfile(auth, request).subscribe();
        }).setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel()).show();
    }

    public void onEmailClick() {
        Context context = getView().getContext();
        TextInputDialogBinding binding = TextInputDialogBinding.inflate(LayoutInflater.from(context));
        binding.textInputLayout.setHint("Email");
        binding.editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        binding.editText.append(viewModel.authUser.email != null ? viewModel.authUser.email : "");
        new AlertDialog.Builder(context).setView(binding.getRoot()).setPositiveButton("Save", (dialog, which) -> {
            String email = binding.editText.getText().toString();
            // TODO Show saving indicator
            viewModel.authUser.email = email;
            viewModel.authUser.notifyChange();
            RxAuth.updateEmail(auth, email).subscribe();
        }).setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel()).show();
    }

    public void onPhotoClick() {
        router.showPhoto(this);
    }

    public void onBankAccountOwnerClick() {
        Context context = getView().getContext();
        TextInputDialogBinding binding = TextInputDialogBinding.inflate(LayoutInflater.from(context));
        binding.textInputLayout.setHint("Bank account owner");
        binding.editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        binding.editText.append(
                viewModel.databaseUser.bankAccountOwner != null ? viewModel.databaseUser.bankAccountOwner : "");
        new AlertDialog.Builder(context).setView(binding.getRoot()).setPositiveButton("Save", (dialog, which) -> {
            // TODO Show saving indicator
            viewModel.databaseUser.bankAccountOwner = binding.editText.getText().toString();
            viewModel.databaseUser.notifyChange();
            client.updateUser(auth.getCurrentUser().getUid(), viewModel.databaseUser).subscribe();
        }).setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel()).show();
    }

    public void onIbanClick() {
        Context context = getView().getContext();
        TextInputDialogBinding binding = TextInputDialogBinding.inflate(LayoutInflater.from(context));
        binding.textInputLayout.setHint("IBAN");
        binding.editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        binding.editText.append(viewModel.databaseUser.iban != null ? viewModel.databaseUser.iban : "");
        new AlertDialog.Builder(context).setView(binding.getRoot()).setPositiveButton("Save", (dialog, which) -> {
            // TODO Show saving indicator
            viewModel.databaseUser.iban = binding.editText.getText().toString();
            viewModel.databaseUser.notifyChange();
            client.updateUser(auth.getCurrentUser().getUid(), viewModel.databaseUser).subscribe();
        }).setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel()).show();
    }

    public void onAboutClick() {
        Toast.makeText(getView().getContext(), "Not implemented", Toast.LENGTH_SHORT).show();
    }

    public void goUp() {
        router.goUp(this);
    }

    public static class ViewModel extends BaseObservable implements Serializable {
        @Bindable public DatabaseUser databaseUser;
        @Bindable public AuthUser authUser;
    }
}
