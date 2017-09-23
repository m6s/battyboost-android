package info.mschmitt.battyboost.adminapp.pos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.adminapp.R;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.adminapp.databinding.PosViewBinding;
import info.mschmitt.battyboost.core.entities.Pos;
import info.mschmitt.battyboost.core.network.BattyboostClient;
import info.mschmitt.firebasesupport.RxQuery;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;

/**
 * @author Matthias Schmitt
 */
public class PosFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final String ARG_POS_KEY = "POS_KEY";
    public PosViewModel viewModel;
    @Inject public Router router;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public boolean injected;
    private CompositeDisposable compositeDisposable;
    private String posKey;

    public static Fragment newInstance(String key) {
        PosFragment fragment = new PosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POS_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!injected) {
            throw new IllegalStateException("Not injected");
        }
        super.onCreate(savedInstanceState);
        viewModel = savedInstanceState == null ? new PosViewModel()
                : (PosViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        posKey = getArguments().getString(ARG_POS_KEY);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PosViewBinding binding = PosViewBinding.inflate(inflater, container, false);
        binding.setFragment(this);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("POS");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(0);
        actionBar.setHomeActionContentDescription(0);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
        DatabaseReference reference = database.getReference("pos").child(posKey);
        Disposable disposable = RxQuery.valueEvents(reference)
                .map(BattyboostClient.POS_MAPPER)
                .subscribe(optional -> setPos(optional.value));
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
        inflater.inflate(R.menu.pos, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_edit);
        menuItem.setOnMenuItemClickListener(this::onEditMenuItemClick);
        menuItem = menu.findItem(R.id.menu_item_delete);
        menuItem.setOnMenuItemClickListener(this::onDeleteMenuItemClick);
    }

    private void setPos(Pos pos) {
        viewModel.pos = pos;
        viewModel.notifyChange();
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    private boolean onDeleteMenuItemClick(MenuItem menuItem) {
        Disposable disposable = client.deletePos(posKey).subscribe(() -> router.goUp(this));
        compositeDisposable.add(disposable);
        return true;
    }

    private boolean onEditMenuItemClick(MenuItem menuItem) {
        if (viewModel.pos == null) {
            return false;
        }
        router.showPosEditing(this, viewModel.pos);
        return true;
    }

    public void goUp() {
        router.goUp(this);
    }
}
