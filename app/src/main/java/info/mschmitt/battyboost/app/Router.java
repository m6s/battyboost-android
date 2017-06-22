package info.mschmitt.battyboost.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import info.mschmitt.battyboost.app.balance.BalanceFragment;
import info.mschmitt.battyboost.app.hub.HubFragment;
import info.mschmitt.battyboost.app.map.MapFragment;
import info.mschmitt.battyboost.app.photo.PhotoFragment;
import info.mschmitt.battyboost.app.schedule.ScheduleFragment;
import info.mschmitt.battyboost.app.settings.SettingsFragment;

/**
 * @author Matthias Schmitt
 */
public class Router {
    private static final String MAP_TAG = MapFragment.class.getSimpleName();
    private static final String SCHEDULE_TAG = ScheduleFragment.class.getSimpleName();
    private static final String BALANCE_TAG = BalanceFragment.class.getSimpleName();

    public void showHub(AppCompatActivity activity) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentView, HubFragment.newInstance())
                .commitNow();
    }

    public void showMap(HubFragment hubFragment) {
        FragmentManager fragmentManager = hubFragment.getChildFragmentManager();
        Fragment old = fragmentManager.findFragmentById(R.id.navigationContentView);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (old != null) {
            fragmentTransaction.detach(old);
        }
        Fragment fragment = fragmentManager.findFragmentByTag(MAP_TAG);
        if (fragment == null) {
            fragment = MapFragment.newInstance();
            fragmentTransaction.add(R.id.navigationContentView, fragment, MAP_TAG);
        } else {
            fragmentTransaction.attach(fragment);
        }
        fragmentTransaction.commitNow();
    }

    public void showSchedule(HubFragment hubFragment) {
        FragmentManager fragmentManager = hubFragment.getChildFragmentManager();
        Fragment old = fragmentManager.findFragmentById(R.id.navigationContentView);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (old != null) {
            fragmentTransaction.detach(old);
        }
        Fragment fragment = fragmentManager.findFragmentByTag(SCHEDULE_TAG);
        if (fragment == null) {
            fragment = ScheduleFragment.newInstance();
            fragmentTransaction.add(R.id.navigationContentView, fragment, SCHEDULE_TAG);
        } else {
            fragmentTransaction.attach(fragment);
        }
        fragmentTransaction.commitNow();
    }

    public void showBalance(HubFragment hubFragment) {
        FragmentManager fragmentManager = hubFragment.getChildFragmentManager();
        Fragment old = fragmentManager.findFragmentById(R.id.navigationContentView);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (old != null) {
            fragmentTransaction.detach(old);
        }
        Fragment fragment = fragmentManager.findFragmentByTag(BALANCE_TAG);
        if (fragment == null) {
            fragment = BalanceFragment.newInstance();
            fragmentTransaction.add(R.id.navigationContentView, fragment, BALANCE_TAG);
        } else {
            fragmentTransaction.attach(fragment);
        }
        fragmentTransaction.commitNow();
    }

    public void showSettings(Fragment fragment) {
        FragmentActivity activity = fragment.getActivity();
        activity.getSupportFragmentManager()
                .beginTransaction().replace(R.id.contentView, SettingsFragment.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    public void showPhoto(Fragment fragment) {
        FragmentActivity activity = fragment.getActivity();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.contentView, PhotoFragment.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
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
