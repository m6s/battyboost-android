package info.mschmitt.battyboost.partnerapp.rental;

import android.databinding.BaseObservable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.ui.auth.AuthUI;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.partnerapp.Cache;
import info.mschmitt.battyboost.partnerapp.R;
import info.mschmitt.battyboost.partnerapp.Router;
import info.mschmitt.battyboost.partnerapp.databinding.RentalViewBinding;
import info.mschmitt.battyboost.partnerapp.rental.actions.RentalActionsFragment;
import info.mschmitt.battyboost.partnerapp.rental.checkout.CheckoutFragment;
import info.mschmitt.battyboost.partnerapp.rental.scanner.ScannerFragment;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.WeakHashMap;

/**
 * @author Matthias Schmitt
 */
public class RentalFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private final WeakHashMap<Fragment, Void> injectedFragments = new WeakHashMap<>();
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public Cache cache;
    @Inject public BattyboostClient client;
    @Inject public AuthUI authUI;
    @Inject public RentalComponent component;
    @Inject public boolean injected;

    public static Fragment newInstance() {
        return new RentalFragment();
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        if (injectedFragments.containsKey(childFragment)) {
            return;
        }
        if (childFragment instanceof ScannerFragment) {
            ScannerFragment drawerFragment = (ScannerFragment) childFragment;
            component.plus(drawerFragment).inject(drawerFragment);
        } else if (childFragment instanceof CheckoutFragment) {
            CheckoutFragment partnerListFragment = (CheckoutFragment) childFragment;
            component.plus(partnerListFragment).inject(partnerListFragment);
        } else if (childFragment instanceof RentalActionsFragment) {
            RentalActionsFragment posListFragment = (RentalActionsFragment) childFragment;
            component.plus(posListFragment).inject(posListFragment);
        }
        injectedFragments.put(childFragment, null);
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
        RentalViewBinding binding = RentalViewBinding.inflate(inflater, container, false);
        if (getChildFragmentManager().findFragmentById(R.id.rentalContentView) == null) {
            router.showScanner(this);
        }
        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_VIEW_MODEL, viewModel);
    }

    public static class ViewModel extends BaseObservable implements Serializable {}
}
