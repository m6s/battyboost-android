package info.mschmitt.battyboost.core.utils.firebase;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import io.reactivex.Completable;

/**
 * @author Matthias Schmitt
 */
public class RxGeoFire {
    public static Completable setLocation(GeoFire geoFire, String key, GeoLocation location) {
        return Completable.create(e -> geoFire.setLocation(key, location, (ignore, error) -> {
            if (error != null) {
                e.onError(new RxDatabaseError(error));
                return;
            }
            e.onComplete();
        }));
    }

    public static Completable removeLocation(GeoFire geoFire, String key) {
        return Completable.create(e -> geoFire.removeLocation(key, (ignore, error) -> {
            if (error != null) {
                e.onError(new RxDatabaseError(error));
                return;
            }
            e.onComplete();
        }));
    }
}
