package info.mschmitt.battyboost.partnerapp.transactionlist;

import android.databinding.BaseObservable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.*;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.BusinessTransaction;
import info.mschmitt.battyboost.core.ui.TransactionRecyclerAdapter;
import info.mschmitt.battyboost.partnerapp.Cache;
import info.mschmitt.battyboost.partnerapp.R;
import info.mschmitt.battyboost.partnerapp.Router;
import info.mschmitt.battyboost.partnerapp.databinding.TransactionListViewBinding;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class TransactionListFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final int RC_SIGN_IN = 123;
    @Inject public Router router;
    @Inject public Cache cache;
    @Inject public FirebaseDatabase database;
    @Inject public BattyboostClient client;
    @Inject public AuthUI authUI;
    @Inject public boolean injected;
    private ViewModel viewModel;

    public static Fragment newInstance() {
        return new TransactionListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!injected) {
            throw new IllegalStateException("Not injected");
        }
        onPreCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void onPreCreate(Bundle savedInstanceState) {
        viewModel = savedInstanceState == null ? new ViewModel()
                : (ViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TransactionListViewBinding binding = TransactionListViewBinding.inflate(inflater, container, false);
        TransactionRecyclerAdapter adapter =
                new TransactionRecyclerAdapter(database.getReference("transactions"), this::onTransactionClick);
        binding.recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(binding.recyclerView.getContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecoration =
                new DividerItemDecoration(binding.recyclerView.getContext(), layoutManager.getOrientation());
        binding.recyclerView.addItemDecoration(itemDecoration);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Transactions");
        binding.setFragment(this);
        return binding.getRoot();
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_VIEW_MODEL, viewModel);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.transactionlist, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_settings);
        menuItem.setOnMenuItemClickListener(this::onSettingsMenuItemClick);
    }

    private void onTransactionClick(String key, BusinessTransaction transaction) {
//        router.showUser(this, key);
    }

    private boolean onSettingsMenuItemClick(MenuItem menuItem) {
//        router.showSettings(this);
        return true;
    }

    public void onAddClick() {
        router.showCart(this);
    }

    public void onSignInClick() {
        startActivityForResult(authUI.createSignInIntentBuilder().build(), RC_SIGN_IN);
    }

    public static class ViewModel extends BaseObservable implements Serializable {}
}
