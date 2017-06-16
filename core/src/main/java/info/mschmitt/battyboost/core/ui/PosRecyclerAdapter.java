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
import info.mschmitt.battyboost.core.databinding.PosItemBinding;
import info.mschmitt.battyboost.core.entities.Pos;

/**
 * @author Matthias Schmitt
 */
public class PosRecyclerAdapter extends FirebaseRecyclerAdapter<Pos, PosRecyclerAdapter.PosHolder> {
    private final OnPosClickListener listener;

    public PosRecyclerAdapter(Query ref, OnPosClickListener listener) {
        super(Pos.class, R.layout.pos_item, PosHolder.class, ref);
        this.listener = listener;
    }

    @Override
    protected void populateViewHolder(PosHolder viewHolder, Pos pos, int position) {
        viewHolder.key = getRef(position).getKey();
        viewHolder.listener = listener;
        viewHolder.pos = pos;
        viewHolder.notifyChange();
    }

    public interface OnPosClickListener {
        void onPosClick(String key, Pos pos);
    }

    public static class PosHolder extends RecyclerView.ViewHolder implements Observable {
        private final PropertyChangeRegistry propertyChangeRegistry = new PropertyChangeRegistry();
        @Bindable public String key;
        @Bindable public Pos pos;
        private OnPosClickListener listener;

        public PosHolder(View itemView) {
            super(itemView);
            PosItemBinding binding = DataBindingUtil.bind(itemView);
            binding.setHolder(this);
        }

        public void onClick() {
            listener.onPosClick(key, pos);
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
