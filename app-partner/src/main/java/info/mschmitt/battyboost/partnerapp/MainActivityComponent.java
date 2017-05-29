package info.mschmitt.battyboost.partnerapp;

/**
 * @author Matthias Schmitt
 */
public class MainActivityComponent {
    private final BattyboostPartnerApplicationComponent applicationComponent;

    public MainActivityComponent(BattyboostPartnerApplicationComponent applicationComponent) {
        this.applicationComponent = applicationComponent;
    }

    public void inject(MainActivity activity) {
        activity.client = applicationComponent.client;
        activity.database = applicationComponent.database;
        activity.injected = true;
    }
}
