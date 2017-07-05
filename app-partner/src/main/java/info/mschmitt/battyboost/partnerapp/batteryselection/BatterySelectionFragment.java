package info.mschmitt.battyboost.partnerapp.batteryselection;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.Query;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.Battery;
import info.mschmitt.battyboost.core.utils.firebase.RxQuery;
import info.mschmitt.battyboost.partnerapp.Cache;
import info.mschmitt.battyboost.partnerapp.Router;
import info.mschmitt.battyboost.partnerapp.databinding.BatterySelectionViewBinding;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class BatterySelectionFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final String ARG_BATTERY = "BATTERY";
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public Cache cache;
    @Inject public AuthUI authUI;
    @Inject public boolean injected;
    @Inject public BattyboostClient client;
    private CompositeDisposable compositeDisposable;
    private Battery battery;

    public static BatterySelectionFragment newInstance(Battery battery) {
        BatterySelectionFragment fragment = new BatterySelectionFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_BATTERY, battery);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!injected) {
            throw new IllegalStateException("Not injected");
        }
        super.onCreate(savedInstanceState);
        battery = (Battery) getArguments().getSerializable(ARG_BATTERY);
        if (savedInstanceState == null) {
            viewModel = new ViewModel();
            viewModel.batteryCode = battery == null ? null : battery.qr;
        } else {
            viewModel = (ViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BatterySelectionViewBinding binding = BatterySelectionViewBinding.inflate(inflater, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Battery code");
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

    public void onOkClick() {
        if (viewModel.batteryCode == null || viewModel.batteryCode.isEmpty()) {
            BatterySelectionListener listener = getBatterySelectionListener();
            if (listener != null) {
                listener.onBatterySelected(this, null);
            }
            router.dismiss(this);
        } else if (battery != null && viewModel.batteryCode.equals(battery.qr)) {
            router.dismiss(this);
        } else {
            // TODO Show loading indicator
            Query batteryQuery = client.batteriesRef.orderByChild("qr").equalTo(viewModel.batteryCode);
            Disposable disposable = RxQuery.valueEvents(batteryQuery)
                    .map(RxQuery.FIRST_CHILD_MAPPER)
                    .map(optional -> optional.flatMap(BattyboostClient.BATTERY_MAPPER))
                    .subscribe(optional -> setBattery(optional.value));
            compositeDisposable.add(disposable);
        }
    }

    @Nullable
    private BatterySelectionListener getBatterySelectionListener() {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            return (BatterySelectionListener) targetFragment;
        }
        Fragment parentFragment = getParentFragment();
        return parentFragment instanceof BatterySelectionListener ? (BatterySelectionListener) parentFragment : null;
    }

    private void setBattery(Battery battery) {
        if (battery == null) {
            Snackbar.make(getView(), "Not a battery code", Snackbar.LENGTH_LONG).show();
        } else {
            BatterySelectionListener listener = getBatterySelectionListener();
            if (listener != null) {
                listener.onBatterySelected(this, battery);
            }
            router.dismiss(this);
        }
    }

    public void onCancelClick() {
        router.dismiss(this);
    }

    public interface BatterySelectionListener {
        void onBatterySelected(BatterySelectionFragment sender, Battery battery);
    }

    public static class ViewModel extends BaseObservable implements Serializable {
        @Bindable public String batteryCode;
    }
}
