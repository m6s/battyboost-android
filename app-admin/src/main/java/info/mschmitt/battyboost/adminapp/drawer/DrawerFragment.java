package info.mschmitt.battyboost.adminapp.drawer;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.adminapp.databinding.DrawerViewBinding;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class DrawerFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public boolean injected;

    public static DrawerFragment newInstance() {
        return new DrawerFragment();
    }

    public void onPartnersClick() {
        setSelectedItem(DrawerItem.PARTNER_LIST);
        DrawerListener drawerListener = getDrawerListener();
        if (drawerListener != null) {
            drawerListener.onDrawerItemSelected(this, DrawerItem.PARTNER_LIST);
        }
    }

    public void setSelectedItem(DrawerItem drawerItem) {
        viewModel.drawerItem = drawerItem;
        viewModel.notifyChange();
    }

    @Nullable
    private DrawerListener getDrawerListener() {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            return (DrawerListener) targetFragment;
        }
        Fragment parentFragment = getParentFragment();
        return parentFragment instanceof DrawerListener ? (DrawerListener) parentFragment : null;
    }

    public void onPosClick() {
        setSelectedItem(DrawerItem.POS_LIST);
        DrawerListener drawerListener = getDrawerListener();
        if (drawerListener != null) {
            drawerListener.onDrawerItemSelected(this, DrawerItem.POS_LIST);
        }
    }

    public void onUsersClick() {
        setSelectedItem(DrawerItem.USER_LIST);
        DrawerListener drawerListener = getDrawerListener();
        if (drawerListener != null) {
            drawerListener.onDrawerItemSelected(this, DrawerItem.USER_LIST);
        }
    }

    public void onBatteriesClick() {
        setSelectedItem(DrawerItem.BATTERY_LIST);
        DrawerListener drawerListener = getDrawerListener();
        if (drawerListener != null) {
            drawerListener.onDrawerItemSelected(this, DrawerItem.BATTERY_LIST);
        }
    }

    @Override
    public void setTargetFragment(Fragment fragment, int requestCode) {
        if (!(fragment instanceof DrawerListener)) {
            throw new IllegalArgumentException();
        }
        super.setTargetFragment(fragment, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!injected) {
            throw new IllegalStateException("Not injected");
        }
        super.onCreate(savedInstanceState);
        viewModel = savedInstanceState == null ? new ViewModel()
                : (ViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DrawerViewBinding binding = DrawerViewBinding.inflate(inflater, container, false);
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_VIEW_MODEL, viewModel);
    }

    public enum DrawerItem {
        PARTNER_LIST, POS_LIST, USER_LIST, BATTERY_LIST
    }

    public interface DrawerListener {
        void onDrawerItemSelected(DrawerFragment sender, DrawerItem drawerItem);
    }

    public static class ViewModel extends BaseObservable implements Serializable {
        @Bindable public DrawerItem drawerItem = DrawerItem.PARTNER_LIST;
    }
}
