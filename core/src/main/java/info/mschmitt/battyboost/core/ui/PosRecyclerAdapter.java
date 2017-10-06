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
import info.mschmitt.battyboost.core.databinding.PosItemBinding;
import info.mschmitt.battyboost.core.entities.Pos;

/**
 * @author Matthias Schmitt
 */
public class PosRecyclerAdapter extends FirebaseRecyclerAdapter<Pos, PosRecyclerAdapter.PosHolder> {
    private final OnPosClickListener listener;

    public PosRecyclerAdapter(Query query, OnPosClickListener listener) {
        super(new FirebaseRecyclerOptions.Builder<Pos>().setQuery(query, Pos.class).build());
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(PosHolder viewHolder, int position, Pos pos) {
        if (pos.id == null) {
            pos.id = getRef(position).getKey();
        }
        viewHolder.listener = listener;
        viewHolder.pos = pos;
        viewHolder.notifyChange();
    }

    @Override
    public PosHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PosHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.pos_item, parent, false));
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

    public interface OnPosClickListener {
        void onPosClick(Pos pos);
    }

    public static class PosHolder extends RecyclerView.ViewHolder implements Observable {
        private final PropertyChangeRegistry propertyChangeRegistry = new PropertyChangeRegistry();
        @Bindable public Pos pos;
        private OnPosClickListener listener;

        public PosHolder(View itemView) {
            super(itemView);
            PosItemBinding binding = DataBindingUtil.bind(itemView);
            binding.setHolder(this);
        }

        public void onClick() {
            listener.onPosClick(pos);
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
