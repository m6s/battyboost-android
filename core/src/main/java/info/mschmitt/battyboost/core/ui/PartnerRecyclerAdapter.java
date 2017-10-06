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
import info.mschmitt.battyboost.core.databinding.PartnerItemBinding;
import info.mschmitt.battyboost.core.entities.Partner;

/**
 * @author Matthias Schmitt
 */
public class PartnerRecyclerAdapter extends FirebaseRecyclerAdapter<Partner, PartnerRecyclerAdapter.PartnerHolder> {
    private final OnPartnerClickListener listener;

    public PartnerRecyclerAdapter(Query query, OnPartnerClickListener listener) {
        super(new FirebaseRecyclerOptions.Builder<Partner>().setQuery(query, Partner.class).build());
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(PartnerHolder viewHolder, int position, Partner partner) {
        if (partner.id == null) {
            partner.id = getRef(position).getKey();
        }
        viewHolder.listener = listener;
        viewHolder.partner = partner;
        viewHolder.notifyChange();
    }

    @Override
    public PartnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PartnerHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.partner_item, parent, false));
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

    public interface OnPartnerClickListener {
        void onPartnerClick(Partner partner);
    }

    public static class PartnerHolder extends RecyclerView.ViewHolder implements Observable {
        private final PropertyChangeRegistry propertyChangeRegistry = new PropertyChangeRegistry();
        @Bindable public Partner partner;
        private OnPartnerClickListener listener;

        public PartnerHolder(View itemView) {
            super(itemView);
            PartnerItemBinding binding = DataBindingUtil.bind(itemView);
            binding.setHolder(this);
        }

        public void onClick() {
            listener.onPartnerClick(partner);
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
