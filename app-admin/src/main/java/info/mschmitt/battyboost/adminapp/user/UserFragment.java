package info.mschmitt.battyboost.adminapp.user;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import info.mschmitt.battyboost.adminapp.R;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.adminapp.databinding.UserViewBinding;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.BusinessUser;
import info.mschmitt.firebasesupport.RxQuery;
import info.mschmitt.zxingsupport.ZXingImageLoader;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;

/**
 * @author Matthias Schmitt
 */
public class UserFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final String ARG_USER_KEY = "USER_KEY";
    public UserViewModel viewModel;
    @Inject public Router router;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public boolean injected;
    private CompositeDisposable compositeDisposable;
    private String userKey;

    public static Fragment newInstance(String key) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!injected) {
            throw new IllegalStateException("Not injected");
        }
        viewModel = savedInstanceState == null ? new UserViewModel()
                : (UserViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        userKey = getArguments().getString(ARG_USER_KEY);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UserViewBinding binding = UserViewBinding.inflate(inflater, container, false);
        binding.setFragment(this);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("User");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(0);
        actionBar.setHomeActionContentDescription(0);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
        DatabaseReference reference = client.usersRef.child(userKey);
        Disposable disposable = RxQuery.valueEvents(reference)
                .filter(DataSnapshot::exists)
                .map(BattyboostClient.DATABASE_USER_MAPPER)
                .subscribe(optional -> setUser(optional.value));
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
        inflater.inflate(R.menu.user, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_edit);
        menuItem.setOnMenuItemClickListener(this::onEditMenuItemClick);
    }

    private void setUser(BusinessUser user) {
        if (user.qr != null) {
            Glide.with(this)
                    .using(ZXingImageLoader.create(BarcodeFormat.QR_CODE, ErrorCorrectionLevel.L, 0))
                    .load("https://battyboost.com/qr?" + user.qr)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(getBinding().qrImageView);
        }
        viewModel.user = user;
        viewModel.notifyChange();
    }

    private UserViewBinding getBinding() {
        return DataBindingUtil.getBinding(getView());
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    private boolean onEditMenuItemClick(MenuItem menuItem) {
        if (viewModel.user == null) {
            return false;
        }
        router.showUserEditing(this, viewModel.user);
        return true;
    }

    public void goUp() {
        router.goUp(this);
    }
}
