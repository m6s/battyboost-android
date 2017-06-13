package info.mschmitt.battyboost.app;

/**
 * @author Matthias Schmitt
 */
public class MainActivityComponent {
    private final BattyboostApplicationComponent applicationComponent;

    public MainActivityComponent(BattyboostApplicationComponent applicationComponent) {
        this.applicationComponent = applicationComponent;
    }

    public void inject(MainActivity activity) {
        activity.component = this;
        activity.applicationComponent = applicationComponent;
        activity.router = applicationComponent.router;
        activity.injected = true;
    }
}
