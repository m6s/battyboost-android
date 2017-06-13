package info.mschmitt.battyboost.core.utils;

import android.databinding.BindingAdapter;
import android.support.design.widget.BottomNavigationView;

/**
 * @author Matthias Schmitt
 */
public class BottomNavigationBindingAdapters {
    @BindingAdapter("onNavigationItemSelected")
    public static void setOnNavigationItemSelected(BottomNavigationView view,
                                                   BottomNavigationView.OnNavigationItemSelectedListener listener) {
        view.setOnNavigationItemSelectedListener(listener);
    }
}
