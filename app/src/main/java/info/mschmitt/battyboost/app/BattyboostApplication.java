package info.mschmitt.battyboost.app;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import javax.inject.Inject;

/**
 * @author Matthias Schmitt
 */
public class BattyboostApplication extends Application {
    @Inject public BattyboostApplicationComponent applicationComponent;
    @Inject public boolean injected;

    public void onAttachActivity(MainActivity activity) {
        applicationComponent.plus(activity).inject(activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        new BattyboostApplicationComponent(this).inject(this);
    }
}
