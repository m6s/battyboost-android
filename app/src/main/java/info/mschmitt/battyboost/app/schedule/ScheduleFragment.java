package info.mschmitt.battyboost.app.schedule;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.app.databinding.ScheduleViewBinding;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.entities.Pos;
import info.mschmitt.battyboost.core.utils.firebase.RxAuth;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class ScheduleFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final int RC_SIGN_IN = 123;
    public ViewModel viewModel;
    @Inject public FirebaseDatabase database;
    @Inject public BattyboostClient client;
    @Inject public FirebaseAuth auth;
    @Inject public AuthUI authUI;
    @Inject public boolean injected;
    private CompositeDisposable compositeDisposable;

    public static Fragment newInstance() {
        return new ScheduleFragment();
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
        ScheduleViewBinding binding = ScheduleViewBinding.inflate(inflater, container, false);
//        PosRecyclerAdapter adapter = new PosRecyclerAdapter(database.getReference("pos"), this::onPosClick);
//        binding.recyclerView.setAdapter(adapter);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(binding.recyclerView.getContext());
//        binding.recyclerView.setLayoutManager(layoutManager);
//        DividerItemDecoration itemDecoration =
//                new DividerItemDecoration(binding.recyclerView.getContext(), layoutManager.getOrientation());
//        binding.recyclerView.addItemDecoration(itemDecoration);
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
        Disposable disposable = RxAuth.stateChanges(auth).subscribe(ignore -> {
            viewModel.signedIn = auth.getCurrentUser() != null;
            viewModel.notifyChange();
        });
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

    private void onPosClick(String posId, Pos pos) {
    }

    public void onSignInClick() {
        startActivityForResult(authUI.createSignInIntentBuilder().build(), RC_SIGN_IN);
    }

    public static class ViewModel extends BaseObservable implements Serializable {
        @Bindable public boolean signedIn;
        @Bindable public String text;
    }
}
