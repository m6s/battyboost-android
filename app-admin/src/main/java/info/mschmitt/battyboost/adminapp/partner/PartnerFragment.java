package info.mschmitt.battyboost.adminapp.partner;

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
import info.mschmitt.battyboost.adminapp.databinding.PartnerViewBinding;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.Partner;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class PartnerFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final String ARG_PARTNER_KEY = "PARTNER_KEY";
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public boolean injected;
    private CompositeDisposable compositeDisposable;

    public static Fragment newInstance(String key) {
        PartnerFragment fragment = new PartnerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARTNER_KEY, key);
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
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PartnerViewBinding binding = PartnerViewBinding.inflate(inflater, container, false);
        binding.setFragment(this);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Partner");
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
        String partnerKey = args.getString(ARG_PARTNER_KEY);
        DatabaseReference reference = database.getReference("partners/" + partnerKey);
        Disposable disposable =
                RxFirebaseDatabase.observeValueEvent(reference, Partner.class).subscribe(this::setPartner);
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
        MenuItem editMenuItem = menu.findItem(R.id.menu_item_edit);
        editMenuItem.setOnMenuItemClickListener(this::onEditMenuItemClick);
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    private boolean onEditMenuItemClick(MenuItem menuItem) {
        router.showPartnerEditing(this, getArguments().getString(ARG_PARTNER_KEY));
        return true;
    }

    public void setPartner(Partner partner) {
        viewModel.partner = partner;
        viewModel.name = partner.name;
        viewModel.balanceCents = String.valueOf(partner.balanceCents);
        viewModel.adminId = partner.adminId == null ? " " : partner.adminId;
        viewModel.posId = partner.posId == null ? " " : partner.posId;
        viewModel.notifyChange();
    }

    public void goUp() {
        router.goUp(this);
    }

    public static class ViewModel extends BaseObservable implements Serializable {
        @Bindable public String name;
        @Bindable public String balanceCents;
        @Bindable public String adminId;
        @Bindable public String posId;
        private Partner partner;
    }
}
