package info.mschmitt.battyboost.app.map;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.database.*;
import info.mschmitt.battyboost.app.databinding.MapViewBinding;
import info.mschmitt.battyboost.core.BattyboostClient;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class MapFragment extends Fragment {
    private static final String STATE_KEY = "state";
    @Inject public BattyboostClient client;
    @Inject public FirebaseDatabase database;
    @Inject public boolean injected;
    public State state;

    public static Fragment newInstance() {
        return new MapFragment();
    }

    public void onClick() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!injected) {
            throw new IllegalStateException("Not injected");
        }
        state = savedInstanceState == null ? new State() : (State) savedInstanceState.getSerializable(STATE_KEY);
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DatabaseReference partnersRef = database.getReference("partners");
        partnersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        MapViewBinding binding = MapViewBinding.inflate(inflater, container, false);
        binding.setFragment(this);
        return binding.getRoot();
    }

    public static class State extends BaseObservable implements Serializable {
        @Bindable public String text;
    }
}
