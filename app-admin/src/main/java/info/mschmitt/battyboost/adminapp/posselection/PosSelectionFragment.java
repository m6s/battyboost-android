package info.mschmitt.battyboost.adminapp.posselection;

import android.databinding.BaseObservable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.adminapp.databinding.PosSelectionViewBinding;
import info.mschmitt.battyboost.adminapp.drawer.DrawerFragment;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.Pos;
import info.mschmitt.battyboost.core.ui.PosRecyclerAdapter;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class PosSelectionFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public boolean injected;

    public static PosSelectionFragment newInstance() {
        return new PosSelectionFragment();
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
        PosSelectionViewBinding binding = PosSelectionViewBinding.inflate(inflater, container, false);
        PosRecyclerAdapter adapter = new PosRecyclerAdapter(database.getReference("pos"), this::onPosClick);
        binding.recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(binding.recyclerView.getContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecoration =
                new DividerItemDecoration(binding.recyclerView.getContext(), layoutManager.getOrientation());
        binding.recyclerView.addItemDecoration(itemDecoration);
        binding.setFragment(this);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Select POS");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(0);
        actionBar.setHomeActionContentDescription(0);
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

    private void onPosClick(Pos pos) {
        router.goBack(this);
        PosSelectionListener posSelectionListener = getPosSelectionListener();
        if (posSelectionListener != null) {
            posSelectionListener.onPosIdSelected(pos.id);
        }
    }

    @Nullable
    private PosSelectionListener getPosSelectionListener() {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            return (PosSelectionListener) targetFragment;
        }
        Fragment parentFragment = getParentFragment();
        return parentFragment instanceof DrawerFragment.DrawerListener ? (PosSelectionListener) parentFragment : null;
    }

    public void goUp() {
        router.goUp(this);
    }

    public <T extends Fragment & PosSelectionListener> void setTargetFragment(T targetFragment) {
        setTargetFragment(targetFragment, 0);
    }

    public interface PosSelectionListener {
        void onPosIdSelected(String posId);
    }

    public static class ViewModel extends BaseObservable implements Serializable {}
}
