package info.mschmitt.battyboost.partnerapp;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import info.mschmitt.battyboost.partnerapp.transactionlist.TransactionListFragment;

/**
 * @author Matthias Schmitt
 */
public class Router {
    public void showTransactionList(AppCompatActivity activity) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentView, TransactionListFragment.newInstance())
                .commitNow();
    }

    public void goUp(Fragment fragment) {
        fragment.getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    public void goBack(Fragment fragment) {
        fragment.getFragmentManager().popBackStackImmediate();
    }
}
