package info.mschmitt.battyboost.adminapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import info.mschmitt.battyboost.adminapp.hub.HubFragment;
import info.mschmitt.battyboost.adminapp.partner.PartnerFragment;
import info.mschmitt.battyboost.adminapp.partnerediting.PartnerEditingFragment;
import info.mschmitt.battyboost.adminapp.partnerlist.PartnerListFragment;
import info.mschmitt.battyboost.adminapp.pos.PosFragment;
import info.mschmitt.battyboost.adminapp.posediting.PosEditingFragment;
import info.mschmitt.battyboost.adminapp.poslist.PosListFragment;
import info.mschmitt.battyboost.adminapp.topiclist.TopicListFragment;

/**
 * @author Matthias Schmitt
 */
public class Router {
    public void showHub(AppCompatActivity activity) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentView, HubFragment.newInstance())
                .commitNow();
    }

    public void showTopicList(Fragment fragment) {
        FragmentManager fragmentManager = fragment.getChildFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.drawerContentView, TopicListFragment.newInstance()).commitNow();
    }

    public void showPartnerList(Fragment fragment) {
        FragmentManager fragmentManager = fragment.getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.detailsContentView, PartnerListFragment.newInstance())
                .commitNow();
    }

    public void showPartner(Fragment fragment, String key) {
        FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.contentView, PartnerFragment.newInstance(key))
                .addToBackStack(null)
                .commit();
    }

    public void showPartnerEditing(Fragment fragment, String key) {
        FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.contentView, PartnerEditingFragment.newInstance(key))
                .addToBackStack(null)
                .commit();
    }

    public void showPosList(Fragment fragment) {
        FragmentManager fragmentManager = fragment.getChildFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.detailsContentView, PosListFragment.newInstance()).commitNow();
    }

    public void showPos(Fragment fragment, String key) {
        FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.contentView, PosFragment.newInstance(key))
                .addToBackStack(null)
                .commit();
    }

    public void showPosEditing(Fragment fragment, String key) {
        FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.contentView, PosEditingFragment.newInstance(key))
                .addToBackStack(null)
                .commit();
    }

    public void goUp(Fragment fragment) {
        fragment.getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    public boolean goBack(MainActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        return fragmentManager.popBackStackImmediate();
    }
}
