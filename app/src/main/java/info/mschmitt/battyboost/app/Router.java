package info.mschmitt.battyboost.app;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import info.mschmitt.battyboost.app.hub.HubFragment;
import info.mschmitt.battyboost.app.map.MapFragment;
import info.mschmitt.battyboost.app.profile.ProfileFragment;
import info.mschmitt.battyboost.app.schedule.ScheduleFragment;

/**
 * @author Matthias Schmitt
 */
public class Router {
    private static final String MAP_TAG = MapFragment.class.getSimpleName();
    private static final String SCHEDULE_TAG = ScheduleFragment.class.getSimpleName();
    private static final String PROFILE_TAG = ProfileFragment.class.getSimpleName();

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

    public void showProfile(HubFragment hubFragment) {
        FragmentManager fragmentManager = hubFragment.getChildFragmentManager();
        Fragment old = fragmentManager.findFragmentById(R.id.navigationContentView);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (old != null) {
            fragmentTransaction.detach(old);
        }
        Fragment fragment = fragmentManager.findFragmentByTag(PROFILE_TAG);
        if (fragment == null) {
            fragment = ProfileFragment.newInstance();
            fragmentTransaction.add(R.id.navigationContentView, fragment, PROFILE_TAG);
        } else {
            fragmentTransaction.attach(fragment);
        }
        fragmentTransaction.commitNow();
    }

    public void notifySignedOut(Fragment fragment) {
        View view = fragment.getParentFragment().getView();
        if (view != null) {Snackbar.make(view, "Signed out", Snackbar.LENGTH_SHORT).show();}
    }
}
