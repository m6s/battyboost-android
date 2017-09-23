package info.mschmitt.battyboost.adminapp.partner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.adminapp.R;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.adminapp.databinding.PartnerViewBinding;
import info.mschmitt.battyboost.core.entities.Partner;
import info.mschmitt.battyboost.core.network.BattyboostClient;
import info.mschmitt.firebasesupport.RxQuery;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;

/**
 * @author Matthias Schmitt
 */
public class PartnerFragment extends Fragment {
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
        super.onCreate(savedInstanceState);
        partnerKey = getArguments().getString(ARG_PARTNER_KEY);
        viewModel = savedInstanceState == null ? new PartnerViewModel()
                : (PartnerViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
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
        DatabaseReference reference = client.partnersRef.child(partnerKey);
        Disposable disposable = RxQuery.valueEvents(reference)
                .map(BattyboostClient.PARTNER_MAPPER)
                .subscribe(optional -> setPartner(optional.value));
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

    private void setPartner(Partner partner) {
        viewModel.partner = partner;
        viewModel.notifyChange();
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    private boolean onDeleteMenuItemClick(MenuItem menuItem) {
        Disposable disposable = client.deletePartner(partnerKey).subscribe(() -> router.goUp(this));
        compositeDisposable.add(disposable);
        return true;
    }

    private boolean onEditMenuItemClick(MenuItem menuItem) {
        if (viewModel.partner == null) {
            return false;
        }
        router.showPartnerEditing(this, viewModel.partner);
        return true;
    }

    public void goUp() {
        router.goUp(this);
    }
}
