package info.mschmitt.battyboost.partnerapp.guidedrental;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.Battery;
import info.mschmitt.battyboost.core.entities.BusinessUser;
import info.mschmitt.battyboost.partnerapp.Cache;
import info.mschmitt.battyboost.partnerapp.Router;
import info.mschmitt.battyboost.partnerapp.batteryselection.BatterySelectionFragment;
import info.mschmitt.battyboost.partnerapp.databinding.GuidedRentalViewBinding;
import io.reactivex.disposables.CompositeDisposable;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class GuidedRentalFragment extends Fragment implements BatterySelectionFragment.BatterySelectionListener {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final Step[] steps = new Step[]{BatteryStep.INSTANCE, RenterStep.INSTANCE, VerifyStep.INSTANCE};
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public Cache cache;
    @Inject public AuthUI authUI;
    @Inject public boolean injected;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    private CompositeDisposable compositeDisposable;

    public static Fragment newInstance() {
        return new GuidedRentalFragment();
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
        GuidedRentalViewBinding binding = GuidedRentalViewBinding.inflate(inflater, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        steps[viewModel.step].onEnter(this);
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

    public void onPrevClick() {
        viewModel.completedStepCount--;
        viewModel.remainingStepCount++;
        viewModel.step--;
        steps[viewModel.step].onEnter(this);
        viewModel.notifyChange();
    }

    public void onNextClick() {
        boolean valid = steps[viewModel.step].validate(this);
        if (!valid) {
            return;
        }
        viewModel.completedStepCount++;
        viewModel.remainingStepCount--;
        viewModel.step++;
        steps[viewModel.step].onEnter(this);
        viewModel.notifyChange();
    }

    public void onFinishClick() {
        router.showCheckout(this, viewModel.renter, viewModel.battery);
    }

    public void goUp() {
        router.goUp(this);
    }

    public void onEnterBatteryCodeClick() {
        router.showBatterySelection(this, viewModel.battery);
    }

    public void onScanBatteryCodeClick() {
    }

    public void onEnterRenterCodeClick() {
    }

    public void onScanRenterCodeClick() {
    }

    @Override
    public void onBatterySelected(BatterySelectionFragment sender, Battery battery) {
        viewModel.battery = battery;
        viewModel.notifyChange();
    }

    private interface Step {
        void onEnter(GuidedRentalFragment fragment);
        boolean validate(GuidedRentalFragment fragment);
    }

    private static class BatteryStep implements Step {
        static final BatteryStep INSTANCE = new BatteryStep();

        @Override
        public void onEnter(GuidedRentalFragment fragment) {
            ActionBar actionBar = fragment.getSupportActionBar();
            actionBar.setTitle("Battery");
        }

        @Override
        public boolean validate(GuidedRentalFragment fragment) {
            if (fragment.viewModel.battery == null) {
                Snackbar.make(fragment.getView(), "Battery not set", Snackbar.LENGTH_LONG).show();
                return false;
            }
            return true;
        }
    }

    private static class RenterStep implements Step {
        static final RenterStep INSTANCE = new RenterStep();

        @Override
        public void onEnter(GuidedRentalFragment fragment) {
            ActionBar actionBar = fragment.getSupportActionBar();
            actionBar.setTitle("Renter");
        }

        @Override
        public boolean validate(GuidedRentalFragment fragment) {
            if (fragment.viewModel.renter == null && !fragment.viewModel.anonymous) {
                Snackbar.make(fragment.getView(), "Renter not set and not anonymous", Snackbar.LENGTH_LONG).show();
                return false;
            }
            return true;
        }
    }

    private static class VerifyStep implements Step {
        static final VerifyStep INSTANCE = new VerifyStep();

        @Override
        public void onEnter(GuidedRentalFragment fragment) {
            ActionBar actionBar = fragment.getSupportActionBar();
            actionBar.setTitle("Verify");
        }

        @Override
        public boolean validate(GuidedRentalFragment fragment) {
            return true;
        }
    }

    public static class ViewModel extends BaseObservable implements Serializable {
        @Bindable public int skippedStepCount;
        @Bindable public int completedStepCount;
        @Bindable public int remainingStepCount = 2;
        @Bindable public int step;
        @Bindable public BusinessUser renter;
        @Bindable public Battery battery;
        @Bindable public boolean anonymous;
    }
}
