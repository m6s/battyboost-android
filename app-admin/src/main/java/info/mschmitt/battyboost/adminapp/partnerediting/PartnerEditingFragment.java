package info.mschmitt.battyboost.adminapp.partnerediting;

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
import info.mschmitt.battyboost.adminapp.databinding.PartnerEditingViewBinding;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.Partner;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class PartnerEditingFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final String ARG_PARTNER_KEY = "PARTNER_KEY";
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public boolean injected;
    private CompositeDisposable compositeDisposable;
    private String partnerKey;

    public static Fragment newInstance(String key) {
        PartnerEditingFragment fragment = new PartnerEditingFragment();
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
        super.onCreate(savedInstanceState);
        viewModel = savedInstanceState == null ? new ViewModel()
                : (ViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        Bundle args = getArguments();
        partnerKey = args.getString(ARG_PARTNER_KEY);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PartnerEditingViewBinding binding = PartnerEditingViewBinding.inflate(inflater, container, false);
        binding.setFragment(this);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Edit partner");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(0);
        actionBar.setHomeActionContentDescription(0);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
        if (viewModel.partner == null && partnerKey != null) {
            DatabaseReference reference = database.getReference("partners/" + partnerKey);
            Disposable disposable =
                    RxFirebaseDatabase.observeSingleValueEvent(reference, Partner.class).subscribe(this::setPartner);
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
        inflater.inflate(R.menu.partner_editing, menu);
        MenuItem saveMenuItem = menu.findItem(R.id.menu_item_save);
        saveMenuItem.setOnMenuItemClickListener(this::onSaveMenuItemClick);
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    private boolean onSaveMenuItemClick(MenuItem menuItem) {
        Partner partner = new Partner();
        partner.name = viewModel.name;
        partner.balanceCents = Integer.parseInt(viewModel.balanceCents);
        partner.adminId = viewModel.adminId;
        partner.posId = viewModel.posId;
        Disposable disposable;
        if (partnerKey == null) {
            disposable = client.addPartner(partner).subscribe(s -> router.goUp(this));
        } else {
            disposable = client.updatePartner(partnerKey, partner).subscribe(() -> router.goUp(this));
        }
        compositeDisposable.add(disposable);
//        Snackbar.make(getView(), partner.toString(), Snackbar.LENGTH_SHORT).show();
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

    public void onChangeAdminClick() {
    }

    public void onChangePosClick() {
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
