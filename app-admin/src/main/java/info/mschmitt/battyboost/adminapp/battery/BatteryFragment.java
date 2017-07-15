package info.mschmitt.battyboost.adminapp.battery;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import info.mschmitt.battyboost.adminapp.R;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.adminapp.databinding.BatteryViewBinding;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.Battery;
import info.mschmitt.firebasesupport.RxQuery;
import info.mschmitt.zxingsupport.ZXingImageLoader;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;

/**
 * @author Matthias Schmitt
 */
public class BatteryFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final String ARG_BATTERY_KEY = "BATTERY_KEY";
    public BatteryViewModel viewModel;
    @Inject public Router router;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public boolean injected;
    private CompositeDisposable compositeDisposable;
    private String batteryKey;

    public static Fragment newInstance(String key) {
        BatteryFragment fragment = new BatteryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BATTERY_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!injected) {
            throw new IllegalStateException("Not injected");
        }
        super.onCreate(savedInstanceState);
        batteryKey = getArguments().getString(ARG_BATTERY_KEY);
        viewModel = savedInstanceState == null ? new BatteryViewModel()
                : (BatteryViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BatteryViewBinding binding = BatteryViewBinding.inflate(inflater, container, false);
        binding.setFragment(this);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Battery");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(0);
        actionBar.setHomeActionContentDescription(0);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
        DatabaseReference reference = database.getReference("batteries").child(batteryKey);
        Disposable disposable = RxQuery.valueEvents(reference)
                .map(BattyboostClient.BATTERY_MAPPER)
                .subscribe(optional -> setBattery(optional.value));
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.partner, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_edit);
        menuItem.setOnMenuItemClickListener(this::onEditMenuItemClick);
        menuItem = menu.findItem(R.id.menu_item_delete);
        menuItem.setOnMenuItemClickListener(this::onDeleteMenuItemClick);
    }

    private void setBattery(Battery battery) {
        if (battery.qr != null) {
            Glide.with(this)
                    .using(ZXingImageLoader.create(BarcodeFormat.QR_CODE, ErrorCorrectionLevel.L, 0))
                    .load("https://battyboost.com/qr?" + battery.qr)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(getBinding().qrImageView);
        }
        viewModel.battery = battery;
        viewModel.notifyChange();
    }

    private BatteryViewBinding getBinding() {
        return DataBindingUtil.getBinding(getView());
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    private boolean onDeleteMenuItemClick(MenuItem menuItem) {
        Disposable disposable = client.deletePartner(batteryKey).subscribe(() -> router.goUp(this));
        compositeDisposable.add(disposable);
        return true;
    }

    private boolean onEditMenuItemClick(MenuItem menuItem) {
        if (viewModel.battery == null) {
            return false;
        }
        router.showBatteryEditing(this, viewModel.battery);
        return true;
    }

    public void goUp() {
        router.goUp(this);
    }
}
