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
import info.mschmitt.battyboost.core.databinding.UserItemBinding;
import info.mschmitt.battyboost.core.entities.BattyboostUser;

/**
 * @author Matthias Schmitt
 */
public class UserRecyclerAdapter extends FirebaseRecyclerAdapter<BattyboostUser, UserRecyclerAdapter.UserHolder> {
    private final OnUserClickListener listener;

    public UserRecyclerAdapter(Query query, OnUserClickListener listener) {
        super(new FirebaseRecyclerOptions.Builder<BattyboostUser>().setQuery(query, BattyboostUser.class).build());
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(UserHolder viewHolder, int position, BattyboostUser user) {
        if (user.id == null) {
            user.id = getRef(position).getKey();
        }
        viewHolder.listener = listener;
        viewHolder.user = user;
        viewHolder.notifyChange();
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false));
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

    public interface OnUserClickListener {
        void onUserClick(BattyboostUser user);
    }

    public static class UserHolder extends RecyclerView.ViewHolder implements Observable {
        private final PropertyChangeRegistry propertyChangeRegistry = new PropertyChangeRegistry();
        @Bindable public BattyboostUser user;
        private OnUserClickListener listener;

        public UserHolder(View itemView) {
            super(itemView);
            UserItemBinding binding = DataBindingUtil.bind(itemView);
            binding.setHolder(this);
        }

        public void onClick() {
            listener.onUserClick(user);
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
