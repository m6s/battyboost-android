package info.mschmitt.battyboost.partnerapp.batteryscanner;

import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.Query;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.journeyapps.barcodescanner.camera.CameraSettings;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.Battery;
import info.mschmitt.battyboost.core.utils.firebase.RxQuery;
import info.mschmitt.battyboost.partnerapp.Cache;
import info.mschmitt.battyboost.partnerapp.Router;
import info.mschmitt.battyboost.partnerapp.batteryselection.BatterySelectionListener;
import info.mschmitt.battyboost.partnerapp.databinding.BatteryScannerViewBinding;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Matthias Schmitt
 */
public class BatteryScannerFragment extends Fragment {
    public static final String PREFIX = "https://battyboost.com/qr?";
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final String ARG_BATTERY = "BATTERY";
    private final ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public Cache cache;
    @Inject public AuthUI authUI;
    @Inject public boolean injected;
    @Inject public BattyboostClient client;
    private CompositeDisposable compositeDisposable;
    private Battery battery;
    private final BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            processBarcode(result);
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    public static BatteryScannerFragment newInstance(Battery battery) {
        BatteryScannerFragment fragment = new BatteryScannerFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_BATTERY, battery);
        fragment.setArguments(arguments);
        return fragment;
    }

    private void processBarcode(BarcodeResult result) {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
        String text = result.getText();
        if (text.length() != PREFIX.length() + 12 || !text.startsWith(PREFIX)) {
            onWrongQR();
            return;
        }
        String qr = text.substring(PREFIX.length());
        char version = qr.charAt(0);
        String id = qr.substring(1, 11);
        char checksum = qr.charAt(11);
        if (battery != null && qr.equals(battery.qr)) {
            router.dismiss(BatteryScannerFragment.this);
        } else {
            // TODO Show loading indicator
            Query batteryQuery = client.batteriesRef.orderByChild("qr").equalTo(qr);
            Disposable disposable = RxQuery.valueEvents(batteryQuery)
                    .map(RxQuery.FIRST_CHILD_MAPPER)
                    .map(optional -> optional.flatMap(BattyboostClient.BATTERY_MAPPER))
                    .subscribe(optional -> setBattery(optional.value));
            compositeDisposable.add(disposable);
        }
    }

    private void onWrongQR() {
        Snackbar.make(getView(), "Not a battery code", Snackbar.LENGTH_LONG)
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        BatteryScannerViewBinding binding = getBinding();
                        if (binding != null) {
                            binding.barcodeView.decodeSingle(barcodeCallback);
                        }
                    }

                    @Override
                    public void onShown(Snackbar transientBottomBar) {
                    }
                })
                .show();
    }

    private void setBattery(Battery battery) {
        if (battery == null) {
            onWrongQR();
        } else {
            BatterySelectionListener listener = getBatterySelectionListener();
            if (listener != null) {
                listener.onBatterySelected(this, battery);
            }
            router.dismiss(this);
        }
    }

    private BatteryScannerViewBinding getBinding() {
        return getView() == null ? null : DataBindingUtil.getBinding(getView());
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!injected) {
            throw new IllegalStateException("Not injected");
        }
        super.onCreate(savedInstanceState);
        battery = (Battery) getArguments().getSerializable(ARG_BATTERY);
        if (savedInstanceState == null) {
            viewModel = new ViewModel();
        } else {
            viewModel = (ViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BatteryScannerViewBinding binding = BatteryScannerViewBinding.inflate(inflater, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Battery code");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(0);
        actionBar.setHomeActionContentDescription(0);
        binding.setFragment(this);
        initBarcodeView(binding.barcodeView);
        binding.barcodeView.decodeSingle(barcodeCallback);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getBinding().barcodeView.resume();
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
        getBinding().barcodeView.pause();
        super.onPause();
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    private void initBarcodeView(DecoratedBarcodeView barcodeView) {
        CameraSettings settings = new CameraSettings();
        barcodeView.getBarcodeView().setCameraSettings(settings);
        Collection<BarcodeFormat> decodeFormats = Collections.singletonList(BarcodeFormat.QR_CODE);
        Map<DecodeHintType, ?> decodeHints = null;
        String characterSet = null;
        boolean inverted = false;
        barcodeView.getBarcodeView()
                .setDecoderFactory(new DefaultDecoderFactory(decodeFormats, decodeHints, characterSet, inverted));
    }

    public static class ViewModel extends BaseObservable implements Serializable {}
}
