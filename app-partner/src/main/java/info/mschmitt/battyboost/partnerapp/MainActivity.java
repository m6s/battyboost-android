package info.mschmitt.battyboost.partnerapp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.utils.firebase.RxAuth;
import info.mschmitt.battyboost.core.utils.firebase.RxDatabaseReference;
import info.mschmitt.battyboost.partnerapp.stepper.StepperFragment;
import info.mschmitt.battyboost.partnerapp.transactionlist.TransactionListFragment;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.WeakHashMap;

/**
 * @author Matthias Schmitt
 */
public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private final WeakHashMap<Fragment, Void> injectedFragments = new WeakHashMap<>();
    @Inject public MainActivityComponent component;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public FirebaseAuth auth;
    @Inject public Router router;
    @Inject public Cache cache;
    @Inject public boolean injected;
    private ViewModel viewModel;
    private CompositeDisposable compositeDisposable;
    private boolean postResumed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BattyboostPartnerApplication application = (BattyboostPartnerApplication) getApplication();
        application.onAttachActivity(this);
        if (!injected) {
            throw new IllegalStateException("Not injected");
        }
        onPreCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.main_activity);
        if (savedInstanceState == null) {
            router.showTransactionList(this);
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
        Disposable disposable =
                RxAuth.userChanges(auth).filter(optional -> optional.value == null).subscribe(ignore -> {
                    cache.databaseUser = null;
                    cache.initialized = true;
                    cache.notifyChange();
                });
        compositeDisposable.add(disposable);
        disposable = RxAuth.userChanges(auth)
                .filter(optional -> optional.value != null)
                .map(optional -> optional.value)
                .switchMap(
                        firebaseUser -> RxDatabaseReference.valueEvents(client.usersRef.child(firebaseUser.getUid())))
                .map(BattyboostClient.DATABASE_USER_MAPPER)
                .subscribe(optional -> {
                    cache.databaseUser = optional.value;
                    cache.initialized = true;
                    cache.notifyChange();
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        if (injectedFragments.containsKey(childFragment)) {
            return;
        }
        if (childFragment instanceof TransactionListFragment) {
            TransactionListFragment transactionListFragment = (TransactionListFragment) childFragment;
            component.plus(transactionListFragment).inject(transactionListFragment);
        } else if (childFragment instanceof StepperFragment) {
            StepperFragment stepperFragment = (StepperFragment) childFragment;
            component.plus(stepperFragment).inject(stepperFragment);
        }
        injectedFragments.put(childFragment, null);
    }

    private static class ViewModel implements Serializable {}
}
