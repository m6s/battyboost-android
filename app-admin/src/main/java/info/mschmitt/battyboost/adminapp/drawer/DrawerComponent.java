package info.mschmitt.battyboost.adminapp.drawer;

import info.mschmitt.battyboost.adminapp.Router;

/**
 * @author Matthias Schmitt
 */
public class DrawerComponent {
    private final Router router;

    public DrawerComponent(Router router) {
        this.router = router;
    }

    public void inject(DrawerFragment fragment) {
        fragment.router = router;
        fragment.injected = true;
    }
}
