package info.mschmitt.battyboost.adminapp.userediting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.adminapp.R;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.adminapp.databinding.UserEditingViewBinding;
import info.mschmitt.battyboost.adminapp.user.UserViewModel;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.BusinessUser;
import info.mschmitt.battyboost.core.utils.ChecksumProcessor;
import info.mschmitt.battyboost.core.utils.RandomStringGenerator;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;

/**
 * @author Matthias Schmitt
 */
public class UserEditingFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final String ARG_USER = "USER";
    public UserViewModel viewModel;
    @Inject public Router router;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public boolean injected;
    private CompositeDisposable compositeDisposable;

    public static Fragment newInstance(BusinessUser user) {
        UserEditingFragment fragment = new UserEditingFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    public void onGenerateCodeClick() {
        RandomStringGenerator randomStringGenerator = new RandomStringGenerator(11);
        ChecksumProcessor checksumProcessor = new ChecksumProcessor();
        viewModel.user.qr = checksumProcessor.appendChecksum(randomStringGenerator.nextString());
        viewModel.user.notifyChange();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!injected) {
            throw new IllegalStateException("Not injected");
        }
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            viewModel = new UserViewModel();
            Bundle args = getArguments();
            viewModel.user = (BusinessUser) args.getSerializable(ARG_USER);
            if (viewModel.user == null) {
                viewModel.user = new BusinessUser();
            }
        } else {
            viewModel = (UserViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UserEditingViewBinding binding = UserEditingViewBinding.inflate(inflater, container, false);
        binding.setFragment(this);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Edit user");
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
        inflater.inflate(R.menu.user_editing, menu);
        MenuItem saveMenuItem = menu.findItem(R.id.menu_item_save);
        saveMenuItem.setOnMenuItemClickListener(this::onSaveMenuItemClick);
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    private boolean onSaveMenuItemClick(MenuItem menuItem) {
        Disposable disposable = client.updateUser(viewModel.user.id, viewModel.user).subscribe(() -> router.goUp(this));
        compositeDisposable.add(disposable);
        return true;
    }

    public void onSelectImageUrlClick() {
    }

    public void goUp() {
        router.goUp(this);
    }
}
