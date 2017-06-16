package info.mschmitt.battyboost.adminapp.pos;

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
import info.mschmitt.battyboost.adminapp.R;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.adminapp.databinding.PosViewBinding;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.Pos;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class PosFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final String ARG_POS_KEY = "POS_KEY";
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public boolean injected;
    private CompositeDisposable compositeDisposable;

    public static Fragment newInstance(String key) {
        PosFragment fragment = new PosFragment();
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
        viewModel = savedInstanceState == null ? new ViewModel()
                : (ViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PosViewBinding binding = PosViewBinding.inflate(inflater, container, false);
        binding.setFragment(this);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("POS");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(0);
        actionBar.setHomeActionContentDescription(0);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
        Bundle args = getArguments();
        String partnerKey = args.getString(ARG_POS_KEY);
        DatabaseReference reference = database.getReference("pos/" + partnerKey);
        Disposable disposable = RxFirebaseDatabase.observeValueEvent(reference, Pos.class).subscribe(this::setPos);
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
        inflater.inflate(R.menu.pos, menu);
        MenuItem editMenuItem = menu.findItem(R.id.menu_item_edit);
        editMenuItem.setOnMenuItemClickListener(this::onEditMenuItemClick);
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    private boolean onEditMenuItemClick(MenuItem menuItem) {
        router.showPosEditing(this, getArguments().getString(ARG_POS_KEY));
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
