package info.mschmitt.battyboost.adminapp.poslist;

import android.databinding.BaseObservable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.adminapp.databinding.PosListViewBinding;
import info.mschmitt.battyboost.core.entities.Pos;
import info.mschmitt.battyboost.core.network.BattyboostClient;
import info.mschmitt.battyboost.core.ui.PosRecyclerAdapter;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class PosListFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public boolean injected;

    public static Fragment newInstance() {
        return new PosListFragment();
    }

    public void onAddClick() {
        router.showPosEditing(this, null);
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
        PosListViewBinding binding = PosListViewBinding.inflate(inflater, container, false);
        PosRecyclerAdapter adapter = new PosRecyclerAdapter(client.posListRef, this::onPosClick);
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

    private void onPosClick(Pos pos) {
        router.showPos(this, pos.id);
    }

    public static class ViewModel extends BaseObservable implements Serializable {}
}
