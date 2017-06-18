package info.mschmitt.battyboost.app;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
    public void showHub(AppCompatActivity activity) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentView, HubFragment.newInstance())
                .commitNow();
    }

    public void showMap(HubFragment fragment) {
        FragmentManager fragmentManager = fragment.getChildFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.navigationContentView, MapFragment.newInstance()).commitNow();
    }

    public void showSchedule(HubFragment fragment) {
        FragmentManager fragmentManager = fragment.getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.navigationContentView, ScheduleFragment.newInstance())
                .commitNow();
    }

    public void showProfile(HubFragment fragment) {
        FragmentManager fragmentManager = fragment.getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.navigationContentView, ProfileFragment.newInstance())
                .commitNow();
    }

    public void notifySignedOut(Fragment fragment) {
        View view = fragment.getParentFragment().getView();
//        View view = fragment.getView();
        if (view != null) {Snackbar.make(view, "Signed out", Snackbar.LENGTH_SHORT).show();}
    }
}
