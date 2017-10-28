package info.mschmitt.battyboost.adminapp.transactionlist;

import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.adminapp.databinding.TransactionListViewBinding;
import info.mschmitt.battyboost.core.entities.BattyboostTransaction;
import info.mschmitt.battyboost.core.network.BattyboostClient;
import info.mschmitt.battyboost.core.ui.TransactionRecyclerAdapter;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class TransactionListFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public boolean injected;
    private TransactionRecyclerAdapter adapter;

    public static Fragment newInstance() {
        return new TransactionListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!injected) {
            throw new IllegalStateException("Not injected");
        }
        super.onCreate(savedInstanceState);
        viewModel = savedInstanceState == null ? new ViewModel()
                : (ViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        adapter = new TransactionRecyclerAdapter(client.transactionsRef, this::onTransactionClick);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TransactionListViewBinding binding = TransactionListViewBinding.inflate(inflater, container, false);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_VIEW_MODEL, viewModel);
    }

    @Override
    public void onDestroyView() {
        getBinding().recyclerView.setAdapter(null);
        super.onDestroyView();
    }

    private TransactionListViewBinding getBinding() {
        return DataBindingUtil.getBinding(getView());
    }

    private void onTransactionClick(BattyboostTransaction transaction) {
        router.showTransaction(this, transaction.id);
    }

    public static class ViewModel extends BaseObservable implements Serializable {}
}
