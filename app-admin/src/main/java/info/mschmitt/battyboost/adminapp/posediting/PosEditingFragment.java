package info.mschmitt.battyboost.adminapp.posediting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.adminapp.R;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.adminapp.databinding.PosEditingViewBinding;
import info.mschmitt.battyboost.adminapp.pos.PosViewModel;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.Pos;
import info.mschmitt.battyboost.core.utils.firebase.RxDatabaseReference;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;

/**
 * @author Matthias Schmitt
 */
public class PosEditingFragment extends Fragment {
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
        PosEditingFragment fragment = new PosEditingFragment();
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
        Bundle args = getArguments();
        posKey = args.getString(ARG_POS_KEY);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PosEditingViewBinding binding = PosEditingViewBinding.inflate(inflater, container, false);
        binding.setFragment(this);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Edit pos");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(0);
        actionBar.setHomeActionContentDescription(0);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
        if (viewModel.pos == null) {
            if (posKey != null) {
                DatabaseReference reference = database.getReference("pos").child(posKey);
                Disposable disposable = RxDatabaseReference.valueEvents(reference)
                        .filter(DataSnapshot::exists)
                        .map(dataSnapshot -> dataSnapshot.getValue(Pos.class))
                        .firstElement()
                        .subscribe(this::setPos);
                compositeDisposable.add(disposable);
            } else {
                setPos(new Pos());
            }
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
        inflater.inflate(R.menu.pos_editing, menu);
        MenuItem saveMenuItem = menu.findItem(R.id.menu_item_save);
        saveMenuItem.setOnMenuItemClickListener(this::onSaveMenuItemClick);
    }

    private void setPos(Pos pos) {
        viewModel.pos = pos;
        viewModel.notifyChange();
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    private boolean onSaveMenuItemClick(MenuItem menuItem) {
        Disposable disposable;
        if (posKey == null) {
            disposable = client.addPos(viewModel.pos).subscribe(s -> router.goUp(this));
        } else {
            disposable = client.updatePos(posKey, viewModel.pos).subscribe(() -> router.goUp(this));
        }
        compositeDisposable.add(disposable);
        return true;
    }

    public void onSelectImageUrlClick() {
    }

    public void goUp() {
        router.goUp(this);
    }
}