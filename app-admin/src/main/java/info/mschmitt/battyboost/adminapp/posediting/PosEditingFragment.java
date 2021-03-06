package info.mschmitt.battyboost.adminapp.posediting;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.Toast;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.adminapp.R;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.adminapp.databinding.PosEditingViewBinding;
import info.mschmitt.battyboost.adminapp.pos.PosViewModel;
import info.mschmitt.battyboost.core.GeoCoordinates;
import info.mschmitt.battyboost.core.entities.Pos;
import info.mschmitt.battyboost.core.network.BattyboostClient;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;

/**
 * @author Matthias Schmitt
 */
public class PosEditingFragment extends Fragment {
    private static final LatLngBounds BOUNDS_BERLIN =
            new LatLngBounds(new LatLng(GeoCoordinates.BERLIN_LAT - 0.1, GeoCoordinates.BERLIN_LNG - 0.1),
                    new LatLng(GeoCoordinates.BERLIN_LAT + 0.1, GeoCoordinates.BERLIN_LNG + 0.1));
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final String ARG_POS = "POS";
    private static final int PLACE_PICKER_REQUEST = 1;
    public PosViewModel viewModel;
    @Inject public Router router;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public boolean injected;
    private CompositeDisposable compositeDisposable;

    public static Fragment newInstance(Pos pos) {
        PosEditingFragment fragment = new PosEditingFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_POS, pos);
        fragment.setArguments(args);
        return fragment;
    }

    private boolean onSaveMenuItemClick(MenuItem menuItem) {
        if (!coordinatesValid(viewModel.pos.latitude, viewModel.pos.longitude)) {
            Toast.makeText(getContext(), "Not a valid geo location", Toast.LENGTH_LONG).show();
            return true;
        }
        Disposable disposable;
        if (viewModel.pos.id == null) {
            disposable = client.createPos(viewModel.pos).subscribe(s -> router.goUp(this));
        } else {
            disposable = client.updatePos(viewModel.pos.id, viewModel.pos).subscribe(() -> router.goUp(this));
        }
        compositeDisposable.add(disposable);
        return true;
    }

    private static boolean coordinatesValid(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90) {
            return false;
        } else if (longitude < -180 || longitude > 180) {
            return false;
        }
        return true;
    }

    public void onSelectImageUrlClick() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(getContext(), data);
                CharSequence name = place.getName();
                viewModel.pos.name = name == null ? null : name.toString();
                Uri websiteUri = place.getWebsiteUri();
                viewModel.pos.url = websiteUri == null ? null : websiteUri.toString();
                CharSequence address = place.getAddress();
                viewModel.pos.info = address == null ? null : address.toString();
                viewModel.pos.latitude = place.getLatLng().latitude;
                viewModel.pos.longitude = place.getLatLng().longitude;
                viewModel.notifyChange();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!injected) {
            throw new IllegalStateException("Not injected");
        }
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            viewModel = new PosViewModel();
            Bundle args = getArguments();
            viewModel.pos = (Pos) args.getSerializable(ARG_POS);
            if (viewModel.pos == null) {
                viewModel.pos = new Pos();
            }
        } else {
            viewModel = (PosViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        }
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
        MenuItem menuItem = menu.findItem(R.id.menu_item_save);
        menuItem.setOnMenuItemClickListener(this::onSaveMenuItemClick);
        menuItem = menu.findItem(R.id.menu_item_pick_place);
        menuItem.setOnMenuItemClickListener(this::onPickPlaceMenuItemClick);
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    private boolean onPickPlaceMenuItemClick(MenuItem menuItem) {
        try {
            Intent intent = new PlacePicker.IntentBuilder().setLatLngBounds(BOUNDS_BERLIN).build(getActivity());
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
        return true;
    }

    public void goUp() {
        router.goUp(this);
    }
}
