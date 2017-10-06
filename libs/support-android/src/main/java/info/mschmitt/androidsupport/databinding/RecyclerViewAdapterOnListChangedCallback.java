package info.mschmitt.androidsupport.databinding;

import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;

/**
 * @author Matthias Schmitt
 */
public class RecyclerViewAdapterOnListChangedCallback<T>
        extends ObservableList.OnListChangedCallback<ObservableList<T>> {
    private final RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter;

    public RecyclerViewAdapterOnListChangedCallback(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onChanged(ObservableList<T> sender) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemRangeChanged(ObservableList<T> sender, int positionStart, int itemCount) {
        adapter.notifyItemRangeChanged(positionStart, itemCount);
    }

    @Override
    public void onItemRangeInserted(ObservableList<T> sender, int positionStart, int itemCount) {
        adapter.notifyItemRangeInserted(positionStart, itemCount);
    }

    @Override
    public void onItemRangeMoved(ObservableList<T> sender, int fromPosition, int toPosition, int itemCount) {
        if (itemCount == 1) {
            adapter.notifyItemMoved(fromPosition, toPosition);
        } else {
            onChanged(sender);
        }
    }

    @Override
    public void onItemRangeRemoved(ObservableList<T> sender, int positionStart, int itemCount) {
        adapter.notifyItemRangeRemoved(positionStart, itemCount);
    }
}
