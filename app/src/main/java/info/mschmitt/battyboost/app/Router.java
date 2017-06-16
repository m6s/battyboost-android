package info.mschmitt.battyboost.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import info.mschmitt.battyboost.app.hub.HubFragment;
import info.mschmitt.battyboost.app.map.MapFragment;
import info.mschmitt.battyboost.app.profile.ProfileFragment;
import info.mschmitt.battyboost.app.schedule.ScheduleFragment;

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

    public void showMap(Fragment fragment) {
        FragmentManager fragmentManager = fragment.getChildFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.navigationContentView, MapFragment.newInstance()).commitNow();
    }

    public void showSchedule(Fragment fragment) {
        FragmentManager fragmentManager = fragment.getChildFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.navigationContentView, ScheduleFragment.newInstance())
                .commitNow();
    }

    public void showProfile(Fragment fragment) {
        FragmentManager fragmentManager = fragment.getChildFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.navigationContentView, ProfileFragment.newInstance())
                .commitNow();
    }
}
