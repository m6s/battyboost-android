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
import info.mschmitt.battyboost.core.databinding.TransactionItemBinding;
import info.mschmitt.battyboost.core.entities.BusinessTransaction;

/**
 * @author Matthias Schmitt
 */
public class TransactionRecyclerAdapter
        extends FirebaseRecyclerAdapter<BusinessTransaction, TransactionRecyclerAdapter.TransactionHolder> {
    private final OnTransactionClickListener listener;

    public TransactionRecyclerAdapter(Query ref, OnTransactionClickListener listener) {
        super(BusinessTransaction.class, R.layout.transaction_item, TransactionHolder.class, ref);
        this.listener = listener;
    }

    @Override
    protected void populateViewHolder(TransactionHolder viewHolder, BusinessTransaction transaction, int position) {
        viewHolder.key = getRef(position).getKey();
        viewHolder.listener = listener;
        viewHolder.transaction = transaction;
        viewHolder.notifyChange();
    }

    public interface OnTransactionClickListener {
        void onTransactionClick(String key, BusinessTransaction transaction);
    }

    public static class TransactionHolder extends RecyclerView.ViewHolder implements Observable {
        private final PropertyChangeRegistry propertyChangeRegistry = new PropertyChangeRegistry();
        @Bindable public String key;
        @Bindable public BusinessTransaction transaction;
        private OnTransactionClickListener listener;

        public TransactionHolder(View itemView) {
            super(itemView);
            TransactionItemBinding binding = DataBindingUtil.bind(itemView);
            binding.setHolder(this);
        }

        public void onClick() {
            listener.onTransactionClick(key, transaction);
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
