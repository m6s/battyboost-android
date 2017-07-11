package info.mschmitt.battyboost.partnerapp.scanner;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.journeyapps.barcodescanner.camera.CameraSettings;
import info.mschmitt.battyboost.core.QrParser;
import info.mschmitt.battyboost.partnerapp.R;
import info.mschmitt.battyboost.partnerapp.databinding.ScannerViewBinding;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Matthias Schmitt
 */
public class ScannerFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private final QrParser parser = new QrParser();
    public ViewModel viewModel;
    @Inject public OnQrScannedListener onQrScannedListener;
    @Inject public OnUpButtonClickListener onUButtonClickListener;
    @Inject public boolean injected;
    private BeepManager beepManager;
    private boolean resumed;
    private final BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            beepManager.playBeepSoundAndVibrate();
            processQrCode(result.getText());
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            if (!resumed) {
                return;
            }
            ScannerViewBinding binding = getBinding();
            for (ResultPoint point : resultPoints) {
                binding.viewFinderView.addPossibleResultPoint(point);
            }
        }
    };

    public static ScannerFragment newInstance() {
        return new ScannerFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        beepManager = new BeepManager(getActivity());
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
        ScannerViewBinding binding = ScannerViewBinding.inflate(inflater, container, false);
        updateActionBar(binding);
        binding.setFragment(this);
        initBarcodeView(binding.barcodeView);
        binding.viewFinderView.setCameraPreview(binding.barcodeView);
        binding.barcodeView.decodeSingle(barcodeCallback);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        resumed = true;
        checkCameraPermission();
        getBinding().barcodeView.resume();
    }

    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
    }

    private ScannerViewBinding getBinding() {
        return DataBindingUtil.getBinding(getView());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_VIEW_MODEL, viewModel);
    }

    @Override
    public void onPause() {
        getBinding().barcodeView.pause();
        resumed = false;
        super.onPause();
    }

    @Override
    public void onDetach() {
        beepManager = null;
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.scanner, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_manual_entry);
        menuItem.setVisible(!viewModel.manualEntry);
        menuItem.setOnMenuItemClickListener(this::onManualEntryMenuItemClick);
        menuItem = menu.findItem(R.id.menu_item_camera);
        menuItem.setVisible(viewModel.manualEntry);
        menuItem.setOnMenuItemClickListener(this::onCameraEntryMenuItemClick);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goUp(); // Bound handler cleared when setting as action bar
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void goUp() {
        onUButtonClickListener.onUpButtonClick();
    }

    private void updateActionBar(ScannerViewBinding binding) {
        Toolbar toolbar = viewModel.manualEntry ? binding.manualEntryToolbar : binding.cameraToolbar;
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(viewModel.manualEntry ? "Enter QR code" : "Scan QR code");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(0);
        actionBar.setHomeActionContentDescription(0);
    }

    private void initBarcodeView(BarcodeView barcodeView) {
        CameraSettings settings = new CameraSettings();
        barcodeView.setCameraSettings(settings);
        Collection<BarcodeFormat> decodeFormats = Collections.singletonList(BarcodeFormat.QR_CODE);
        Map<DecodeHintType, ?> decodeHints = null;
        String characterSet = null;
        boolean inverted = false;
        barcodeView.setDecoderFactory(new DefaultDecoderFactory(decodeFormats, decodeHints, characterSet, inverted));
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    public void onOkClick() {
        processQrCode(viewModel.batteryCode);
    }

    private void processQrCode(String text) {
        QrParser.ParsingResult parsingResult = parser.parse(text);
        if (parsingResult.error == null) {
            onWrongQr();
        } else {
            onQrScannedListener.onQrScanned(parsingResult.qr);
        }
    }

    private void onWrongQr() {
        if (!resumed) {
            return;
        }
        //noinspection ConstantConditions
        Snackbar.make(getView(), "Invalid QR", Snackbar.LENGTH_LONG)
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (!resumed) {
                            return;
                        }
                        getBinding().barcodeView.decodeSingle(barcodeCallback);
                    }

                    @Override
                    public void onShown(Snackbar transientBottomBar) {
                    }
                })
                .show();
    }

    public boolean onManualEntryMenuItemClick(MenuItem menuItem) {
        viewModel.manualEntry = true;
        viewModel.notifyChange();
        updateActionBar(getBinding());
        getActivity().invalidateOptionsMenu();
        return true;
    }

    public boolean onCameraEntryMenuItemClick(MenuItem menuItem) {
        viewModel.manualEntry = false;
        viewModel.notifyChange();
        updateActionBar(getBinding());
        getActivity().invalidateOptionsMenu();
        return true;
    }

    public interface OnQrScannedListener {
        void onQrScanned(String qr);
    }

    public interface OnUpButtonClickListener {
        void onUpButtonClick();
    }

    public static class ViewModel extends BaseObservable implements Serializable {
        @Bindable public String batteryCode;
        @Bindable public boolean manualEntry;
    }
}
