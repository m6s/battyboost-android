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
import info.mschmitt.battyboost.core.databinding.PartnerItemBinding;
import info.mschmitt.battyboost.core.entities.Partner;

/**
 * @author Matthias Schmitt
 */
public class PartnersAdapter extends FirebaseRecyclerAdapter<Partner, PartnersAdapter.PartnerHolder> {
    private final OnPartnerClickListener listener;

    public PartnersAdapter(Query ref, OnPartnerClickListener listener) {
        super(Partner.class, R.layout.partner_item, PartnerHolder.class, ref);
        this.listener = listener;
    }

    @Override
    protected void populateViewHolder(PartnerHolder viewHolder, Partner partner, int position) {
        viewHolder.key = getRef(position).getKey();
        viewHolder.listener = listener;
        viewHolder.partner = partner;
        viewHolder.notifyChange();
    }

    public interface OnPartnerClickListener {
        void onPartnerClick(String key, Partner partner);
    }

    public static class PartnerHolder extends RecyclerView.ViewHolder implements Observable {
        private final PropertyChangeRegistry propertyChangeRegistry = new PropertyChangeRegistry();
        @Bindable public String key;
        @Bindable public Partner partner;
        private OnPartnerClickListener listener;

        public PartnerHolder(View itemView) {
            super(itemView);
            PartnerItemBinding binding = DataBindingUtil.bind(itemView);
            binding.setHolder(this);
        }

        public void onClick() {
            listener.onPartnerClick(key, partner);
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
