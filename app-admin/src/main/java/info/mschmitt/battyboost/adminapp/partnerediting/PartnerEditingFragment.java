package info.mschmitt.battyboost.adminapp.partnerediting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.adminapp.R;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.adminapp.databinding.PartnerEditingViewBinding;
import info.mschmitt.battyboost.adminapp.partner.PartnerViewModel;
import info.mschmitt.battyboost.adminapp.posselection.PosSelectionFragment;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.Partner;
import info.mschmitt.battyboost.core.utils.firebase.RxDatabaseReference;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;

/**
 * @author Matthias Schmitt
 */
public class PartnerEditingFragment extends Fragment implements PosSelectionFragment.PosSelectionListener {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final String ARG_PARTNER_KEY = "PARTNER_KEY";
    public PartnerViewModel viewModel;
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
        viewModel = savedInstanceState == null ? new PartnerViewModel()
                : (PartnerViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        partnerKey = getArguments().getString(ARG_PARTNER_KEY);
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
        if (viewModel.partner == null) {
            if (partnerKey != null) {
                DatabaseReference reference = database.getReference("partners").child(partnerKey);
                Disposable disposable = RxDatabaseReference.valueEvents(reference).map(BattyboostClient.PARTNER_MAPPER)
                        .firstElement().subscribe(optional -> setPartner(optional.value));
                compositeDisposable.add(disposable);
            } else {
                setPartner(new Partner());
            }
        }
    }

    private void setPartner(Partner partner) {
        viewModel.partner = partner;
        viewModel.notifyChange();
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
        Disposable disposable;
        if (partnerKey == null) {
            disposable = client.addPartner(viewModel.partner).subscribe(s -> router.goUp(this));
        } else {
            disposable = client.updatePartner(partnerKey, viewModel.partner).subscribe(() -> router.goUp(this));
        }
        compositeDisposable.add(disposable);
        return true;
    }

    public void onChangeAdminClick() {
    }

    public void onChangePosClick() {
        router.showPosSelection(this, viewModel.partner.posId);
    }

    public void goUp() {
        router.goUp(this);
    }

    @Override
    public void onPosIdSelected(String posId) {
        viewModel.partner.posId = posId;
        viewModel.notifyChange();
    }
}
