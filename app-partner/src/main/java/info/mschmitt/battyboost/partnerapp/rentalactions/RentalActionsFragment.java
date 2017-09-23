package info.mschmitt.battyboost.partnerapp.rentalactions;

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
import info.mschmitt.battyboost.core.entities.Battery;
import info.mschmitt.battyboost.core.network.BattyboostClient;
import info.mschmitt.battyboost.partnerapp.Cache;
import info.mschmitt.battyboost.partnerapp.Router;
import info.mschmitt.battyboost.partnerapp.databinding.RentalActionsViewBinding;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class RentalActionsFragment extends Fragment {
    public static final String ARG_QR = "QR";
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public Cache cache;
    @Inject public AuthUI authUI;
    @Inject public boolean injected;
    @Inject public BattyboostClient client;
    private CompositeDisposable compositeDisposable;
    private String qr;

    public static Fragment newInstance(String qr) {
        RentalActionsFragment fragment = new RentalActionsFragment();
        Bundle arguments = new Bundle();
        arguments.putString(ARG_QR, qr);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!injected) {
            throw new IllegalStateException("Not injected");
        }
        super.onCreate(savedInstanceState);
        viewModel = savedInstanceState == null ? new ViewModel()
                : (ViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        qr = getArguments().getString(ARG_QR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RentalActionsViewBinding binding = RentalActionsViewBinding.inflate(inflater, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Rental");
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
        Disposable disposable = client.findBatteryByQr(qr)
                .subscribe((batteryOptional, throwable) -> onFindBatterByQrComplete(batteryOptional.value, throwable));
        compositeDisposable.add(disposable);
    }

    private void onFindBatterByQrComplete(Battery battery, Throwable throwable) {
        if (throwable != null) {
            return; // TODO
        }
        if (battery == null) {
            return; // TODO
        }
        viewModel.batteryQr = battery.qr;
        viewModel.batteryRentalTime = battery.rentalTime;
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

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    public void goUp() {
        router.goUp(this);
    }

    public void onRentBatteryClick() {
        if (viewModel.batteryRentalTime > 0) {
            showRentBatteryAlert();
        } else {
            rentBattery();
        }
    }

    private void showRentBatteryAlert() {
    }

    private void rentBattery() {
        Disposable disposable = client.rentBattery(viewModel.batteryQr).subscribe(this::onRentBatteryComplete);
        compositeDisposable.add(disposable);
    }

    private void onRentBatteryComplete(BattyboostClient.RentBatteryResult result, Throwable throwable) {
        router.dismiss(this);
    }

    private void onReturnBatteryComplete(BattyboostClient.ReturnBatteryResult result, Throwable throwable) {
        router.dismiss(this);
    }

    public void onReturnBatteryClick() {
        if (viewModel.batteryRentalTime > 0) {
            returnBattery();
        } else {
            showReturnBatteryAlert();
        }
    }

    private void returnBattery() {
        Disposable disposable = client.returnBattery(viewModel.batteryQr).subscribe(this::onReturnBatteryComplete);
        compositeDisposable.add(disposable);
    }

    private void showReturnBatteryAlert() {
    }

    public static class ViewModel extends BaseObservable implements Serializable {
        @Bindable public String batteryQr;
        @Bindable public long batteryRentalTime;
    }
}
