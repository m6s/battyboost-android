package info.mschmitt.battyboost.app;

import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.google.firebase.database.*;
import info.mschmitt.battyboost.app.databinding.MainActivityBinding;
import info.mschmitt.battyboost.core.BattyboostClient;

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
        BattyboostApplication application = (BattyboostApplication) getApplication();
        application.onAttachActivity(this);
        super.onCreate(savedInstanceState);
        MainActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        binding.setActivity(this);
        DatabaseReference partnersRef = database.getReference("partners");
        partnersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
