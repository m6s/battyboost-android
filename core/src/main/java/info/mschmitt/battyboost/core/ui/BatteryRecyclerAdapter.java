package info.mschmitt.battyboost.core.ui;

import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;
import info.mschmitt.battyboost.core.R;
import info.mschmitt.battyboost.core.databinding.BatteryItemBinding;
import info.mschmitt.battyboost.core.entities.Battery;

/**
 * @author Matthias Schmitt
 */
public class BatteryRecyclerAdapter extends FirebaseRecyclerAdapter<Battery, BatteryRecyclerAdapter.BatteryHolder> {
    private final OnBatteryClickListener listener;

    public BatteryRecyclerAdapter(Query query, OnBatteryClickListener listener) {
        super(new FirebaseRecyclerOptions.Builder<Battery>().setQuery(query, Battery.class).build());
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(BatteryHolder batteryHolder, int i, Battery battery) {
        if (battery.id == null) {
            battery.id = getRef(i).getKey();
        }
        batteryHolder.listener = listener;
        batteryHolder.battery = battery;
        batteryHolder.notifyChange();
    }

    @Override
    public BatteryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BatteryHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.battery_item, parent, false));
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        startListening();
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        stopListening();
        super.unregisterAdapterDataObserver(observer);
    }

    public interface OnBatteryClickListener {
        void onBatteryClick(Battery battery);
    }

    public static class BatteryHolder extends RecyclerView.ViewHolder implements Observable {
        private final PropertyChangeRegistry propertyChangeRegistry = new PropertyChangeRegistry();
        @Bindable public Battery battery;
        private OnBatteryClickListener listener;

        public BatteryHolder(View itemView) {
            super(itemView);
            BatteryItemBinding binding = DataBindingUtil.bind(itemView);
            binding.setHolder(this);
        }

        public void onClick() {
            listener.onBatteryClick(battery);
        }

        @Override
        public void addOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
            propertyChangeRegistry.add(onPropertyChangedCallback);
        }

        @Override
        public void removeOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
            propertyChangeRegistry.remove(onPropertyChangedCallback);
        }

        public void notifyChange() {
            propertyChangeRegistry.notifyCallbacks(this, 0, null);
        }
    }
}
