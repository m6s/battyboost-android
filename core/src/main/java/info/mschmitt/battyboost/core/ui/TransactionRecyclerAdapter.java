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
import info.mschmitt.battyboost.core.databinding.TransactionItemBinding;
import info.mschmitt.battyboost.core.entities.BattyboostTransaction;

/**
 * @author Matthias Schmitt
 */
public class TransactionRecyclerAdapter
        extends FirebaseRecyclerAdapter<BattyboostTransaction, TransactionRecyclerAdapter.TransactionHolder> {
    private final OnTransactionClickListener listener;

    public TransactionRecyclerAdapter(Query query, OnTransactionClickListener listener) {
        super(new FirebaseRecyclerOptions.Builder<BattyboostTransaction>().setQuery(query, BattyboostTransaction.class)
                .build());
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(TransactionHolder viewHolder, int position, BattyboostTransaction transaction) {
        if (transaction.id == null) {
            transaction.id = getRef(position).getKey();
        }
        viewHolder.listener = listener;
        viewHolder.transaction = transaction;
        viewHolder.notifyChange();
    }

    @Override
    public TransactionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TransactionHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false));
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
