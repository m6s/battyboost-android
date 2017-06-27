package info.mschmitt.battyboost.adminapp.hub;

import android.content.res.Configuration;
import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import info.mschmitt.battyboost.adminapp.R;
import info.mschmitt.battyboost.adminapp.Router;
import info.mschmitt.battyboost.adminapp.databinding.HubViewBinding;
import info.mschmitt.battyboost.adminapp.drawer.DrawerFragment;
import info.mschmitt.battyboost.adminapp.partnerlist.PartnerListFragment;
import info.mschmitt.battyboost.adminapp.poslist.PosListFragment;
import info.mschmitt.battyboost.adminapp.userlist.UserListFragment;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.WeakHashMap;

/**
 * @author Matthias Schmitt
 */
public class HubFragment extends Fragment implements DrawerFragment.DrawerListener {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private final WeakHashMap<Fragment, Void> injectedFragments = new WeakHashMap<>();
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public HubComponent component;
    @Inject public boolean injected;

    public static Fragment newInstance() {
        return new HubFragment();
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        if (injectedFragments.containsKey(childFragment)) {
            return;
        }
        if (childFragment instanceof DrawerFragment) {
            DrawerFragment drawerFragment = (DrawerFragment) childFragment;
            component.plus(drawerFragment).inject(drawerFragment);
        } else if (childFragment instanceof PartnerListFragment) {
            PartnerListFragment partnerListFragment = (PartnerListFragment) childFragment;
            component.plus(partnerListFragment).inject(partnerListFragment);
        } else if (childFragment instanceof PosListFragment) {
            PosListFragment posListFragment = (PosListFragment) childFragment;
            component.plus(posListFragment).inject(posListFragment);
        } else if (childFragment instanceof UserListFragment) {
            UserListFragment userListFragment = (UserListFragment) childFragment;
            component.plus(userListFragment).inject(userListFragment);
        }
        injectedFragments.put(childFragment, null);
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
        HubViewBinding binding = HubViewBinding.inflate(inflater, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(getActivity(), binding.drawerLayout, R.string.drawer_open,
                        R.string.drawer_close);
        binding.setToggle(toggle);
        toggle.syncState();
        if (getChildFragmentManager().findFragmentById(R.id.drawerContentView) == null) {
            router.showTopicList(this);
            router.showPartnerList(this);
        }
        updateActionBar();
        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_VIEW_MODEL, viewModel);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getBinding().getToggle().onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getBinding().getToggle().onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private HubViewBinding getBinding() {
        return DataBindingUtil.getBinding(getView());
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    private void updateActionBar() {
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.detailsContentView);
        ActionBar actionBar = getSupportActionBar();
        if (fragment instanceof PartnerListFragment) {
            actionBar.setTitle("Partners");
        } else if (fragment instanceof PosListFragment) {
            actionBar.setTitle("POS");
        } else if (fragment instanceof UserListFragment) {
            actionBar.setTitle("Users");
        } else {
            throw new AssertionError();
        }
    }

    @Override
    public void onDrawerItemSelected(DrawerFragment sender, DrawerFragment.DrawerItem drawerItem) {
        getBinding().drawerLayout.closeDrawers();
        switch (drawerItem) {
            case PARTNER_LIST:
                router.showPartnerList(this);
                break;
            case POS_LIST:
                router.showPosList(this);
                break;
            case USER_LIST:
                router.showUserList(this);
                break;
            default:
                throw new AssertionError();
        }
        updateActionBar();
    }

    public static class ViewModel extends BaseObservable implements Serializable {}
}
