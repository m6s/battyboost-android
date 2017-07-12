package info.mschmitt.battyboost.partnerapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import info.mschmitt.battyboost.core.entities.Battery;
import info.mschmitt.battyboost.core.entities.BusinessUser;
import info.mschmitt.battyboost.partnerapp.checkout.CheckoutFragment;
import info.mschmitt.battyboost.partnerapp.rentalactions.RentalActionsFragment;
import info.mschmitt.battyboost.partnerapp.scanner.ScannerFragment;
import info.mschmitt.battyboost.partnerapp.transactionlist.TransactionListFragment;

/**
 * @author Matthias Schmitt
 */
public class Router {
    public void showTransactionList(AppCompatActivity activity) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentView, TransactionListFragment.newInstance())
                .addToBackStack(TransactionListFragment.class.getName())
                .commit();
    }

    public void showScanner(Fragment fragment) {
        FragmentActivity activity = fragment.getActivity();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentView, ScannerFragment.newInstance())
                .addToBackStack(ScannerFragment.class.getName())
                .commit();
    }

    public void showCheckout(Fragment fragment, BusinessUser user, Battery battery) {
        FragmentActivity activity = fragment.getActivity();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentView, CheckoutFragment.newInstance(user, battery))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    public void goUp(Fragment fragment) {
        fragment.getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    public boolean goBack(MainActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.contentView);
        if (fragment instanceof CheckoutFragment) {
            return fragmentManager.popBackStackImmediate(ScannerFragment.class.getName(), 0);
        } else {
            return false;
        }
    }

    public void showTransactionList(CheckoutFragment fragment) {
        FragmentManager fragmentManager = fragment.getFragmentManager();
        fragmentManager.popBackStackImmediate(TransactionListFragment.class.getName(), 0);
    }

    public void showRentalActions(ScannerFragment fragment, String qr) {
        FragmentActivity activity = fragment.getActivity();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentView, RentalActionsFragment.newInstance(qr))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    public void dismiss(RentalActionsFragment fragment) {
        FragmentActivity activity = fragment.getActivity();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.popBackStack(ScannerFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
