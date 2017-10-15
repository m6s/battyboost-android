package info.mschmitt.battyboost.adminapp.partnerlist;

import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.adminapp.databinding.PartnerListViewBinding;
import info.mschmitt.battyboost.core.entities.Partner;
import info.mschmitt.battyboost.core.network.BattyboostClient;
import info.mschmitt.battyboost.core.ui.PartnerRecyclerAdapter;
import info.mschmitt.firebasesupport.RxQuery;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * @author Matthias Schmitt
 */
public class PartnerListFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private final ObservableList<Partner> partners = new ObservableArrayList<>();
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public boolean injected;
    private PartnerRecyclerAdapter adapter;
    private CompositeDisposable compositeDisposable;

    public static Fragment newInstance() {
        return new PartnerListFragment();
    }

    public void onAddClick() {
        router.showPartnerEditing(this, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!injected) {
            throw new IllegalStateException("Not injected");
        }
        super.onCreate(savedInstanceState);
        viewModel = savedInstanceState == null ? new ViewModel()
                : (ViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PartnerListViewBinding binding = PartnerListViewBinding.inflate(inflater, container, false);
        adapter = new PartnerRecyclerAdapter(partners, this::onPartnerClick);
        binding.recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(binding.recyclerView.getContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecoration =
                new DividerItemDecoration(binding.recyclerView.getContext(), layoutManager.getOrientation());
        binding.recyclerView.addItemDecoration(itemDecoration);
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
        Disposable disposable = RxQuery.valueEvents(client.partnersRef)
                .map(dataSnapshot -> BattyboostClient.mapList(dataSnapshot, BattyboostClient::mapPartner))
                .subscribe(this::onPartnersChanged);
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
    public void onDestroyView() {
        getBinding().recyclerView.setAdapter(null);
        super.onDestroyView();
    }

    private PartnerListViewBinding getBinding() {
        return DataBindingUtil.getBinding(getView());
    }

    private void onPartnersChanged(List<Partner> partners) {
        this.partners.clear();
        this.partners.addAll(partners);
    }

    private void onPartnerClick(Partner partner) {
        router.showPartner(this, partner.id);
    }

    public static class ViewModel extends BaseObservable implements Serializable {}
}
