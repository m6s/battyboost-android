package info.mschmitt.battyboost.core.ui;

import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import info.mschmitt.battyboost.core.R;
import info.mschmitt.battyboost.core.databinding.BatteryItemBinding;
import info.mschmitt.battyboost.core.entities.Battery;

/**
 * @author Matthias Schmitt
 */
public class BatteryRecyclerAdapter extends FirebaseRecyclerAdapter<Battery, BatteryRecyclerAdapter.BatteryHolder> {
    private final OnBatteryClickListener listener;

    public BatteryRecyclerAdapter(Query ref, OnBatteryClickListener listener) {
        super(Battery.class, R.layout.battery_item, BatteryHolder.class, ref);
        this.listener = listener;
    }

    @Override
    protected void populateViewHolder(BatteryHolder viewHolder, Battery battery, int position) {
        if (battery.id == null) {
            battery.id = getRef(position).getKey();
        }
        viewHolder.listener = listener;
        viewHolder.battery = battery;
        viewHolder.notifyChange();
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
