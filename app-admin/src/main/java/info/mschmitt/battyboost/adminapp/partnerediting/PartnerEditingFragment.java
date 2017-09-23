package info.mschmitt.battyboost.adminapp.partnerediting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.adminapp.R;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.adminapp.databinding.PartnerEditingViewBinding;
import info.mschmitt.battyboost.adminapp.partner.PartnerViewModel;
import info.mschmitt.battyboost.adminapp.posselection.PosSelectionFragment;
import info.mschmitt.battyboost.core.entities.Partner;
import info.mschmitt.battyboost.core.network.BattyboostClient;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;

/**
 * @author Matthias Schmitt
 */
public class PartnerEditingFragment extends Fragment implements PosSelectionFragment.PosSelectionListener {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final String ARG_PARTNER = "PARTNER";
    public PartnerViewModel viewModel;
    @Inject public Router router;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public boolean injected;
    private CompositeDisposable compositeDisposable;

    public static Fragment newInstance(Partner partner) {
        PartnerEditingFragment fragment = new PartnerEditingFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARTNER, partner);
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
            viewModel = new PartnerViewModel();
            Bundle args = getArguments();
            viewModel.partner = (Partner) args.getSerializable(ARG_PARTNER);
            if (viewModel.partner == null) {
                viewModel.partner = new Partner();
            }
        } else {
            viewModel = (PartnerViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        }
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
        if (viewModel.partner.id == null) {
            disposable = client.createPartner(viewModel.partner).subscribe(s -> router.goUp(this)); // TODO
        } else {
            disposable =
                    client.updatePartner(viewModel.partner.id, viewModel.partner).subscribe(() -> router.goUp(this));
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
