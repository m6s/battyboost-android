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
        activity.component = this;
        activity.applicationComponent = applicationComponent;
        activity.router = applicationComponent.router;
        activity.injected = true;
    }
}
