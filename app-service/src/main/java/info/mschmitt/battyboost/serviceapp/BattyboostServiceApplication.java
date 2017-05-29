package info.mschmitt.battyboost.serviceapp;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import javax.inject.Inject;

/**
 * @author Matthias Schmitt
 */
public class BattyboostServiceApplication extends Application {
    @Inject public BattyboostServiceApplicationComponent applicationComponent;
    @Inject public boolean injected;

    public void onAttachActivity(MainActivity activity) {
        applicationComponent.plus(activity).inject(activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        new BattyboostServiceApplicationComponent(this).inject(this);
    }
}
