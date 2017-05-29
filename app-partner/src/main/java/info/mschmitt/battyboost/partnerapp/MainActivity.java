package info.mschmitt.battyboost.partnerapp;

import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.google.firebase.database.FirebaseDatabase;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.partnerapp.databinding.MainActivityBinding;

import javax.inject.Inject;

/**
 * @author Matthias Schmitt
 */
public class MainActivity extends AppCompatActivity implements Observable {
    private final PropertyChangeRegistry propertyChangeRegistry = new PropertyChangeRegistry();
    @Bindable public String text;
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public boolean injected;

    public void onClick() {
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        propertyChangeRegistry.add(onPropertyChangedCallback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        propertyChangeRegistry.remove(onPropertyChangedCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BattyboostPartnerApplication application = (BattyboostPartnerApplication) getApplication();
        application.onAttachActivity(this);
        super.onCreate(savedInstanceState);
        MainActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        binding.setActivity(this);
    }
}
