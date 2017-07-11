package info.mschmitt.battyboost.partnerapp.rental;

import com.firebase.ui.auth.AuthUI;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.partnerapp.Cache;
import info.mschmitt.battyboost.partnerapp.Router;
import info.mschmitt.battyboost.partnerapp.rental.actions.RentalActionsComponent;
import info.mschmitt.battyboost.partnerapp.rental.actions.RentalActionsFragment;
import info.mschmitt.battyboost.partnerapp.rental.checkout.CheckoutComponent;
import info.mschmitt.battyboost.partnerapp.rental.checkout.CheckoutFragment;
import info.mschmitt.battyboost.partnerapp.rental.scanner.ScannerComponent;
import info.mschmitt.battyboost.partnerapp.rental.scanner.ScannerFragment;

/**
 * @author Matthias Schmitt
 */
public class RentalComponent {
    private final Router router;
    private final Cache cache;
    private final BattyboostClient client;
    private final AuthUI authUI;

    public RentalComponent(Router router, Cache cache, BattyboostClient client, AuthUI authUI) {
        this.router = router;
        this.cache = cache;
        this.client = client;
        this.authUI = authUI;
    }

    public void inject(RentalFragment fragment) {
        fragment.router = router;
        fragment.cache = cache;
        fragment.client = client;
        fragment.authUI = authUI;
        fragment.component = this;
        fragment.injected = true;
    }

    public ScannerComponent plus(ScannerFragment fragment) {
        return new ScannerComponent(router, cache, client, authUI);
    }

    public RentalActionsComponent plus(RentalActionsFragment fragment) {
        return new RentalActionsComponent(router, cache, client, authUI);
    }

    public CheckoutComponent plus(CheckoutFragment fragment) {
        return new CheckoutComponent(router, cache, client, authUI);
    }
}
