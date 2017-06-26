package info.mschmitt.battyboost.app.settings;

import android.content.Context;
import android.databinding.BaseObservable;
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
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.app.Store;
import info.mschmitt.battyboost.app.databinding.SettingsViewBinding;
import info.mschmitt.battyboost.app.databinding.TextInputDialogBinding;
import info.mschmitt.battyboost.core.utils.firebase.RxAuthUI;
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
    @Inject public Store store;
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
        if (store.databaseUser.displayName != null) {
            binding.editText.append(store.databaseUser.displayName);
        }
        new AlertDialog.Builder(context).setView(binding.getRoot())
                .setPositiveButton("Save",
                        (dialog, which) -> store.updateDisplayName(binding.editText.getText().toString()).subscribe())
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel())
                .show();
        // TODO Show saving indicator
    }

    public void onEmailClick() {
        Context context = getView().getContext();
        TextInputDialogBinding binding = TextInputDialogBinding.inflate(LayoutInflater.from(context));
        binding.textInputLayout.setHint("Email");
        binding.editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        if (store.databaseUser.email != null) {
            binding.editText.append(store.databaseUser.email);
        }
        new AlertDialog.Builder(context).setView(binding.getRoot())
                .setPositiveButton("Save",
                        (dialog, which) -> store.updateEmail(binding.editText.getText().toString()).subscribe())
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel())
                .show();
    }

    public void onPhotoClick() {
        router.showPhoto(this);
    }

    public void onBankAccountOwnerClick() {
        Context context = getView().getContext();
        TextInputDialogBinding binding = TextInputDialogBinding.inflate(LayoutInflater.from(context));
        binding.textInputLayout.setHint("Bank account owner");
        binding.editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        binding.editText.append(store.databaseUser.bankAccountOwner != null ? store.databaseUser.bankAccountOwner : "");
        new AlertDialog.Builder(context).setView(binding.getRoot())
                .setPositiveButton("Save",
                        (dialog, which) -> store.updateBankAccountOwner(binding.editText.getText().toString())
                                .subscribe())
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel())
                .show();
    }

    public void onIbanClick() {
        Context context = getView().getContext();
        TextInputDialogBinding binding = TextInputDialogBinding.inflate(LayoutInflater.from(context));
        binding.textInputLayout.setHint("IBAN");
        binding.editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        binding.editText.append(store.databaseUser.iban != null ? store.databaseUser.iban : "");
        new AlertDialog.Builder(context).setView(binding.getRoot())
                .setPositiveButton("Save",
                        (dialog, which) -> store.updateIban(binding.editText.getText().toString()).subscribe())
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel())
                .show();
    }

    public void onAboutClick() {
        Toast.makeText(getView().getContext(), "Not implemented", Toast.LENGTH_SHORT).show();
    }

    public void goUp() {
        router.goUp(this);
    }

    public static class ViewModel extends BaseObservable implements Serializable {}
}
