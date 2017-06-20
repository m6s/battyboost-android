package info.mschmitt.battyboost.partnerapp;

import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.utils.firebase.RxAuth;
import info.mschmitt.battyboost.partnerapp.databinding.MainActivityBinding;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;

/**
 * @author Matthias Schmitt
 */
public class MainActivity extends AppCompatActivity implements Observable {
    private static final int RC_SIGN_IN = 123;
    private final PropertyChangeRegistry propertyChangeRegistry = new PropertyChangeRegistry();
    @Bindable public String text;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public FirebaseAuth auth;
    @Inject public AuthUI authUI;
    @Inject public boolean injected;
    private CompositeDisposable compositeDisposable;

    public void onScanClick() {
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        propertyChangeRegistry.add(onPropertyChangedCallback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        propertyChangeRegistry.remove(onPropertyChangedCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BattyboostPartnerApplication application = (BattyboostPartnerApplication) getApplication();
        application.onAttachActivity(this);
        super.onCreate(savedInstanceState);
        MainActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        binding.setActivity(this);
    }

    @Override
    protected void onPause() {
        compositeDisposable.dispose();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
        Disposable disposable = RxAuth.userChanges(auth).subscribe(optional -> {
            FirebaseUser user = optional.value;
            text = user == null ? "Please log in" : "Hello " + user.getDisplayName() + "!";
            propertyChangeRegistry.notifyChange(MainActivity.this, BR.text);
        });
        compositeDisposable.add(disposable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem signInOutMenuItem = menu.findItem(R.id.menu_item_sign_in_out);
        if (auth.getCurrentUser() != null) {
            signInOutMenuItem.setTitle("Sign out");
            signInOutMenuItem.setOnMenuItemClickListener(this::onSignOutMenuItemClick);
        } else {
            signInOutMenuItem.setTitle("Sign in");
            signInOutMenuItem.setOnMenuItemClickListener(this::onSignInMenuItemClick);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private boolean onSignInMenuItemClick(MenuItem menuItem) {
        startActivityForResult(authUI.createSignInIntentBuilder().build(), RC_SIGN_IN);
        return true;
    }

    private boolean onSignOutMenuItemClick(MenuItem menuItem) {
        authUI.signOut(this).addOnSuccessListener(ignore -> {
//            if (getView() != null) {Snackbar.make(getView(), "Signed out", Snackbar.LENGTH_SHORT).show();}
        });
        return true;
    }
}
