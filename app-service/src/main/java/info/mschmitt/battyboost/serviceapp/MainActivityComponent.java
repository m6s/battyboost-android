package info.mschmitt.battyboost.serviceapp;

/**
 * @author Matthias Schmitt
 */
public class MainActivityComponent {
    private final BattyboostServiceApplicationComponent applicationComponent;

    public MainActivityComponent(BattyboostServiceApplicationComponent applicationComponent) {
        this.applicationComponent = applicationComponent;
    }

    public void inject(MainActivity activity) {
        activity.client = applicationComponent.client;
        activity.database = applicationComponent.database;
        activity.injected = true;
    }
}
