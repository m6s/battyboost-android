package info.mschmitt.battyboost.adminapp.batteryediting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.adminapp.R;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.adminapp.battery.BatteryViewModel;
import info.mschmitt.battyboost.adminapp.databinding.BatteryEditingViewBinding;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.Battery;
import info.mschmitt.battyboost.core.utils.ChecksumProcessor;
import info.mschmitt.battyboost.core.utils.RandomStringGenerator;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;

/**
 * @author Matthias Schmitt
 */
public class BatteryEditingFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final String ARG_BATTERY = "BATTERY";
    public BatteryViewModel viewModel;
    @Inject public Router router;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public boolean injected;
    private CompositeDisposable compositeDisposable;

    public static Fragment newInstance(Battery battery) {
        BatteryEditingFragment fragment = new BatteryEditingFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BATTERY, battery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!injected) {
            throw new IllegalStateException("Not injected");
        }
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            viewModel = new BatteryViewModel();
            Bundle args = getArguments();
            viewModel.battery = (Battery) args.getSerializable(ARG_BATTERY);
            if (viewModel.battery == null) {
                viewModel.battery = new Battery();
            }
        } else {
            viewModel = (BatteryViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BatteryEditingViewBinding binding = BatteryEditingViewBinding.inflate(inflater, container, false);
        binding.setFragment(this);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Edit battery");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(0);
        actionBar.setHomeActionContentDescription(0);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.partner_editing, menu);
        MenuItem saveMenuItem = menu.findItem(R.id.menu_item_save);
        saveMenuItem.setOnMenuItemClickListener(this::onSaveMenuItemClick);
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    public void onGenerateCodeClick() {
        RandomStringGenerator randomStringGenerator = new RandomStringGenerator(10);
        ChecksumProcessor checksumProcessor = new ChecksumProcessor();
        viewModel.battery.qr = 0 + checksumProcessor.appendChecksum(randomStringGenerator.nextString());
        viewModel.battery.notifyChange();
    }

    private boolean onSaveMenuItemClick(MenuItem menuItem) {
        Disposable disposable;
        if (viewModel.battery.id == null) {
            disposable = client.addBattery(viewModel.battery).subscribe(s -> router.goUp(this));
        } else {
            disposable =
                    client.updateBattery(viewModel.battery.id, viewModel.battery).subscribe(() -> router.goUp(this));
        }
        compositeDisposable.add(disposable);
        return true;
    }

    public void goUp() {
        router.goUp(this);
    }
}
