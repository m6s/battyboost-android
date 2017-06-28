package info.mschmitt.battyboost.adminapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import info.mschmitt.battyboost.adminapp.batterylist.BatteryListFragment;
import info.mschmitt.battyboost.adminapp.drawer.DrawerFragment;
import info.mschmitt.battyboost.adminapp.hub.HubFragment;
import info.mschmitt.battyboost.adminapp.partner.PartnerFragment;
import info.mschmitt.battyboost.adminapp.partnerediting.PartnerEditingFragment;
import info.mschmitt.battyboost.adminapp.partnerlist.PartnerListFragment;
import info.mschmitt.battyboost.adminapp.pos.PosFragment;
import info.mschmitt.battyboost.adminapp.posediting.PosEditingFragment;
import info.mschmitt.battyboost.adminapp.poslist.PosListFragment;
import info.mschmitt.battyboost.adminapp.posselection.PosSelectionFragment;
import info.mschmitt.battyboost.adminapp.user.UserFragment;
import info.mschmitt.battyboost.adminapp.userediting.UserEditingFragment;
import info.mschmitt.battyboost.adminapp.userlist.UserListFragment;
import info.mschmitt.battyboost.core.entities.BusinessUser;
import info.mschmitt.battyboost.core.entities.Partner;
import info.mschmitt.battyboost.core.entities.Pos;

/**
 * @author Matthias Schmitt
 */
public class Router {
    private static final String PARTNER_LIST_TAG = PartnerListFragment.class.getSimpleName();
    private static final String POS_LIST_TAG = PosListFragment.class.getSimpleName();
    private static final String USER_LIST_TAG = UserListFragment.class.getSimpleName();
    private static final String BATTERY_LIST_TAG = BatteryListFragment.class.getSimpleName();

    public void showHub(AppCompatActivity activity) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentView, HubFragment.newInstance())
                .commitNow();
    }

    public void showTopicList(HubFragment hubFragment) {
        FragmentManager fragmentManager = hubFragment.getChildFragmentManager();
        DrawerFragment drawerFragment = DrawerFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.drawerContentView, drawerFragment).commitNow();
    }

    public void showPartnerList(HubFragment hubFragment) {
        FragmentManager fragmentManager = hubFragment.getChildFragmentManager();
        Fragment old = fragmentManager.findFragmentById(R.id.detailsContentView);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (old != null) {
            fragmentTransaction.detach(old);
        }
        Fragment fragment = fragmentManager.findFragmentByTag(PARTNER_LIST_TAG);
        if (fragment == null) {
            fragment = PartnerListFragment.newInstance();
            fragmentTransaction.add(R.id.detailsContentView, fragment, PARTNER_LIST_TAG);
        } else {
            fragmentTransaction.attach(fragment);
        }
        fragmentTransaction.commitNow();
    }

    public void showPartner(Fragment fragment, String partnerId) {
        FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.contentView, PartnerFragment.newInstance(partnerId))
                .addToBackStack(null)
                .commit();
    }

    public void showPartnerEditing(Fragment fragment, Partner partner) {
        FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.contentView, PartnerEditingFragment.newInstance(partner))
                .addToBackStack(null)
                .commit();
    }

    public void showPosList(HubFragment hubFragment) {
        FragmentManager fragmentManager = hubFragment.getChildFragmentManager();
        Fragment old = fragmentManager.findFragmentById(R.id.detailsContentView);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (old != null) {
            fragmentTransaction.detach(old);
        }
        Fragment fragment = fragmentManager.findFragmentByTag(POS_LIST_TAG);
        if (fragment == null) {
            fragment = PosListFragment.newInstance();
            fragmentTransaction.add(R.id.detailsContentView, fragment, POS_LIST_TAG);
        } else {
            fragmentTransaction.attach(fragment);
        }
        fragmentTransaction.commitNow();
    }

    public void showPos(Fragment fragment, String posId) {
        FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.contentView, PosFragment.newInstance(posId))
                .addToBackStack(null)
                .commit();
    }

    public void showPosEditing(Fragment fragment, Pos pos) {
        FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.contentView, PosEditingFragment.newInstance(pos))
                .addToBackStack(null)
                .commit();
    }

    public <T extends Fragment & PosSelectionFragment.PosSelectionListener> void showPosSelection(T fragment,
                                                                                                  String key) {
        FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
        PosSelectionFragment posSelectionFragment = PosSelectionFragment.newInstance();
        posSelectionFragment.setTargetFragment(fragment);
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.contentView, posSelectionFragment).addToBackStack(null).commit();
    }

    public void showUserList(HubFragment hubFragment) {
        FragmentManager fragmentManager = hubFragment.getChildFragmentManager();
        Fragment old = fragmentManager.findFragmentById(R.id.detailsContentView);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (old != null) {
            fragmentTransaction.detach(old);
        }
        Fragment fragment = fragmentManager.findFragmentByTag(USER_LIST_TAG);
        if (fragment == null) {
            fragment = UserListFragment.newInstance();
            fragmentTransaction.add(R.id.detailsContentView, fragment, USER_LIST_TAG);
        } else {
            fragmentTransaction.attach(fragment);
        }
        fragmentTransaction.commitNow();
    }

    public void showBatteryList(HubFragment hubFragment) {
        FragmentManager fragmentManager = hubFragment.getChildFragmentManager();
        Fragment old = fragmentManager.findFragmentById(R.id.detailsContentView);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (old != null) {
            fragmentTransaction.detach(old);
        }
        Fragment fragment = fragmentManager.findFragmentByTag(BATTERY_LIST_TAG);
        if (fragment == null) {
            fragment = BatteryListFragment.newInstance();
            fragmentTransaction.add(R.id.detailsContentView, fragment, BATTERY_LIST_TAG);
        } else {
            fragmentTransaction.attach(fragment);
        }
        fragmentTransaction.commitNow();
    }

    public void showUser(Fragment fragment, String userId) {
        FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.contentView, UserFragment.newInstance(userId))
                .addToBackStack(null)
                .commit();
    }

    public void showUserEditing(Fragment fragment, BusinessUser user) {
        FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.contentView, UserEditingFragment.newInstance(user))
                .addToBackStack(null)
                .commit();
    }

    public void goUp(Fragment fragment) {
        fragment.getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    public void goBack(Fragment fragment) {
        fragment.getFragmentManager().popBackStackImmediate();
    }
}
