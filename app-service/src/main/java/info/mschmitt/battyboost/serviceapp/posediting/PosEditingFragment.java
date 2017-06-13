package info.mschmitt.battyboost.serviceapp.posediting;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.Pos;
import info.mschmitt.battyboost.serviceapp.R;
import info.mschmitt.battyboost.serviceapp.Router;
import info.mschmitt.battyboost.serviceapp.databinding.PosEditingViewBinding;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class PosEditingFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final String ARG_POS_KEY = "POS_KEY";
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public boolean injected;
    private CompositeDisposable compositeDisposable;
    private String posKey;

    public static Fragment newInstance(String key) {
        PosEditingFragment fragment = new PosEditingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POS_KEY, key);
        fragment.setArguments(args);
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
        Bundle args = getArguments();
        posKey = args.getString(ARG_POS_KEY);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PosEditingViewBinding binding = PosEditingViewBinding.inflate(inflater, container, false);
        binding.setFragment(this);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Edit pos");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(0);
        actionBar.setHomeActionContentDescription(0);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
        if (viewModel.pos == null && posKey != null) {
            DatabaseReference reference = database.getReference("pos/" + posKey);
            Disposable disposable =
                    RxFirebaseDatabase.observeSingleValueEvent(reference, Pos.class).subscribe(this::setPos);
            compositeDisposable.add(disposable);
        }
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
        inflater.inflate(R.menu.pos_editing, menu);
        MenuItem saveMenuItem = menu.findItem(R.id.menu_item_save);
        saveMenuItem.setOnMenuItemClickListener(this::onSaveMenuItemClick);
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    private boolean onSaveMenuItemClick(MenuItem menuItem) {
        Pos pos = new Pos();
        pos.name = viewModel.name;
        pos.info = viewModel.info;
        pos.imageUrl = viewModel.imageUrl;
        pos.url = viewModel.url;
        pos.availableBatteryCount = Integer.parseInt(viewModel.availableBatteryCount);
        pos.latitude = Double.parseDouble(viewModel.latitude);
        pos.longitude = Double.parseDouble(viewModel.longitude);
        Disposable disposable;
        if (posKey == null) {
            disposable = client.addPos(pos).subscribe(s -> router.goUp(this));
        } else {
            disposable = client.updatePos(posKey, pos).subscribe(() -> router.goUp(this));
        }
        compositeDisposable.add(disposable);
//        Snackbar.make(getView(), pos.toString(), Snackbar.LENGTH_SHORT).show();
        return true;
    }

    public void setPos(Pos pos) {
        viewModel.pos = pos;
        viewModel.name = pos.name;
        viewModel.info = pos.info;
        viewModel.imageUrl = pos.imageUrl;
        viewModel.url = pos.url;
        viewModel.availableBatteryCount = String.valueOf(pos.availableBatteryCount);
        viewModel.latitude = String.valueOf(pos.latitude);
        viewModel.longitude = String.valueOf(pos.longitude);
        viewModel.notifyChange();
    }

    public void onSelectImageUrlClick() {
    }

    public void goUp() {
        router.goUp(this);
    }

    public static class ViewModel extends BaseObservable implements Serializable {
        @Bindable public String name;
        @Bindable public String info;
        @Bindable public String imageUrl;
        @Bindable public String url;
        @Bindable public String availableBatteryCount;
        @Bindable public String latitude;
        @Bindable public String longitude;
        private Pos pos;
    }
}
