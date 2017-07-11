package info.mschmitt.battyboost.partnerapp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.utils.firebase.RxAuth;
import info.mschmitt.battyboost.core.utils.firebase.RxQuery;
import info.mschmitt.battyboost.partnerapp.rental.RentalFragment;
import info.mschmitt.battyboost.partnerapp.scanner.ScannerFragment;
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
    private static final String STATE_CACHE = "CACHE";
    private final WeakHashMap<Fragment, Void> injectedFragments = new WeakHashMap<>();
    @Inject public MainActivityComponent component;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public FirebaseAuth auth;
    @Inject public Router router;
    @Inject public boolean injected;
    public Cache cache;
    private MainViewModel viewModel;
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
        // https://stackoverflow.com/a/28041425/2317680
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        DataBindingUtil.setContentView(this, R.layout.main_activity);
        if (savedInstanceState == null) {
            router.showTransactionList(this);
        }
    }

    private void onPreCreate(Bundle savedInstanceState) {
        viewModel = savedInstanceState == null ? new MainViewModel()
                : (MainViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        cache = savedInstanceState == null ? new Cache() : (Cache) savedInstanceState.getSerializable(STATE_CACHE);
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
        outState.putSerializable(STATE_CACHE, cache);
    }

    @Override
    public void onBackPressed() {
        if (!postResumed) {
            // https://www.reddit.com/r/androiddev/comments/4d2aje/ever_launched_a_fragmenttransaction_in_response/
            // https://developer.android.com/topic/libraries/support-library/revisions.html#26-0-0-beta1
            return;
        }
        if (router.goBack(this)) {
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
                    cache.user = null;
                    cache.initialized = true;
                    cache.notifyChange();
                });
        compositeDisposable.add(disposable);
        disposable = RxAuth.userChanges(auth)
                .filter(optional -> optional.value != null)
                .map(optional -> optional.value)
                .switchMap(firebaseUser -> RxQuery.valueEvents(client.usersRef.child(firebaseUser.getUid())))
                .map(BattyboostClient.DATABASE_USER_MAPPER)
                .subscribe(optional -> {
                    cache.user = optional.value;
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
//        } else if (childFragment instanceof RentalIntroFragment) {
//            RentalIntroFragment rentalIntroFragment = (RentalIntroFragment) childFragment;
//            component.plus(rentalIntroFragment).inject(rentalIntroFragment);
//        } else if (childFragment instanceof GuidedRentalFragment) {
//            GuidedRentalFragment guidedRentalFragment = (GuidedRentalFragment) childFragment;
//            component.plus(guidedRentalFragment).inject(guidedRentalFragment);
        } else if (childFragment instanceof RentalFragment) {
            RentalFragment rentalFragment = (RentalFragment) childFragment;
            component.plus(rentalFragment).inject(rentalFragment);
//        } else if (childFragment instanceof BatterySelectionFragment) {
//            BatterySelectionFragment batterySelectionFragment = (BatterySelectionFragment) childFragment;
//            component.plus(batterySelectionFragment).inject(batterySelectionFragment);
        } else if (childFragment instanceof ScannerFragment) {
            ScannerFragment scannerFragment = (ScannerFragment) childFragment;
            component.plus(scannerFragment).inject(scannerFragment);
        }
        injectedFragments.put(childFragment, null);
    }

    private static class MainViewModel implements Serializable {}
}
