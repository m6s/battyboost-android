package info.mschmitt.battyboost.app.map;

import android.databinding.BaseObservable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.*;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.app.R;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.app.databinding.MapViewBinding;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.GeoCoordinates;
import info.mschmitt.battyboost.core.entities.Pos;
import info.mschmitt.battyboost.core.utils.firebase.RxDatabaseReference;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Matthias Schmitt
 */
public class MapFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final LatLng CAMERA_TARGET_BERLIN = new LatLng(GeoCoordinates.BERLIN_LAT, GeoCoordinates.BERLIN_LNG);
    private static final int CAMERA_ZOOM = 12;
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public boolean injected;
    private CompositeDisposable compositeDisposable;
    private Map<String, Marker> markerMap = new HashMap<>();

    public static Fragment newInstance() {
        return new MapFragment();
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
        MapViewBinding binding = MapViewBinding.inflate(inflater, container, false);
        binding.setFragment(this);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Map");
        SupportMapFragment googleMapFragment = findGoogleMapFragment();
        if (googleMapFragment == null) {
            googleMapFragment = SupportMapFragment.newInstance(
                    new GoogleMapOptions().camera(CameraPosition.fromLatLngZoom(CAMERA_TARGET_BERLIN, CAMERA_ZOOM)));
            getChildFragmentManager().beginTransaction().add(R.id.mapContentView, googleMapFragment).commitNow();
        }
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
        findGoogleMapFragment().getMapAsync(this::resumeMap);
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
        inflater.inflate(R.menu.map, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_settings);
        menuItem.setOnMenuItemClickListener(this::onProfileMenuItemClick);
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    private SupportMapFragment findGoogleMapFragment() {
        return (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapContentView);
    }

    private void resumeMap(GoogleMap map) {
        Disposable disposable = RxDatabaseReference.childAddedEvents(client.posListRef)
                .map(BattyboostClient.POS_MAPPER)
                .subscribe(optional -> {
                    Pos pos = optional.value;
                    Marker marker = map.addMarker(toMarkerOptions(pos));
                    marker.setTag(pos);
                    markerMap.put(pos.id, marker);
                });
        compositeDisposable.add(disposable);
        disposable = RxDatabaseReference.childRemovedEvents(client.partnersRef)
                .map(BattyboostClient.POS_MAPPER)
                .subscribe(optional -> markerMap.remove(optional.value.id).remove());
        compositeDisposable.add(disposable);
        map.setOnMarkerClickListener(marker -> {
            Object tag = marker.getTag();
            //noinspection unchecked
            Pair<String, Pos> pair = (Pair<String, Pos>) tag;
            //noinspection ConstantConditions
            onPosClick(pair.first, pair.second);
            return true;
        });
    }

    private static MarkerOptions toMarkerOptions(Pos pos) {
        return new MarkerOptions().position(new LatLng(pos.latitude, pos.longitude)).title(pos.name);
    }

    private void onPosClick(String posId, Pos pos) {
        Toast.makeText(getView().getContext(), pos.name, Toast.LENGTH_SHORT).show();
    }

    private boolean onProfileMenuItemClick(MenuItem menuItem) {
        router.showSettings(this);
        return true;
    }

    public static class ViewModel extends BaseObservable implements Serializable {}
}
