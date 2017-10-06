package info.mschmitt.battyboost.core.ui;

import android.databinding.*;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import info.mschmitt.androidsupport.databinding.RecyclerViewAdapterOnListChangedCallback;
import info.mschmitt.battyboost.core.R;
import info.mschmitt.battyboost.core.databinding.PartnerItemBinding;
import info.mschmitt.battyboost.core.entities.Partner;

/**
 * @author Matthias Schmitt
 */
public class PartnerRecyclerAdapter extends RecyclerView.Adapter<PartnerRecyclerAdapter.PartnerHolder> {
    private final ObservableList<Partner> partners;
    private final OnPartnerClickListener listener;
    private final RecyclerViewAdapterOnListChangedCallback<Partner> callback =
            new RecyclerViewAdapterOnListChangedCallback<>(this);

    public PartnerRecyclerAdapter(ObservableList<Partner> partners, OnPartnerClickListener listener) {
        this.partners = partners;
        this.listener = listener;
    }

    @Override
    public PartnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PartnerHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.partner_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PartnerHolder holder, int position) {
        Partner partner = partners.get(position);
        holder.listener = listener;
        holder.partner = partner;
        holder.notifyChange();
    }

    @Override
    public int getItemCount() {
        return partners.size();
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        partners.addOnListChangedCallback(callback);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        partners.removeOnListChangedCallback(callback);
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
