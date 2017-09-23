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
import info.mschmitt.battyboost.core.entities.BattyboostTransaction;

/**
 * @author Matthias Schmitt
 */
public class TransactionRecyclerAdapter
        extends FirebaseRecyclerAdapter<BattyboostTransaction, TransactionRecyclerAdapter.TransactionHolder> {
    private final OnTransactionClickListener listener;

    public TransactionRecyclerAdapter(Query ref, OnTransactionClickListener listener) {
        super(BattyboostTransaction.class, R.layout.transaction_item, TransactionHolder.class, ref);
        this.listener = listener;
    }

    @Override
    protected void populateViewHolder(TransactionHolder viewHolder, BattyboostTransaction transaction, int position) {
        if (transaction.id == null) {
            transaction.id = getRef(position).getKey();
        }
        viewHolder.listener = listener;
        viewHolder.transaction = transaction;
        viewHolder.notifyChange();
    }

    public interface OnTransactionClickListener {
        void onTransactionClick(BattyboostTransaction transaction);
    }

    public static class TransactionHolder extends RecyclerView.ViewHolder implements Observable {
        private final PropertyChangeRegistry propertyChangeRegistry = new PropertyChangeRegistry();
        @Bindable public BattyboostTransaction transaction;
        private OnTransactionClickListener listener;

        public TransactionHolder(View itemView) {
            super(itemView);
            TransactionItemBinding binding = DataBindingUtil.bind(itemView);
            binding.setHolder(this);
        }

        public void onClick() {
            listener.onTransactionClick(transaction);
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
