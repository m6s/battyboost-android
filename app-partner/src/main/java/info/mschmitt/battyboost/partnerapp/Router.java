package info.mschmitt.battyboost.partnerapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import info.mschmitt.battyboost.core.entities.Battery;
import info.mschmitt.battyboost.core.entities.BusinessUser;
import info.mschmitt.battyboost.partnerapp.batteryscanner.BatteryScannerFragment;
import info.mschmitt.battyboost.partnerapp.batteryselection.BatterySelectionFragment;
import info.mschmitt.battyboost.partnerapp.checkout.CheckoutFragment;
import info.mschmitt.battyboost.partnerapp.guidedrental.GuidedRentalFragment;
import info.mschmitt.battyboost.partnerapp.rentalintro.RentalIntroFragment;
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

    public void showCreateTransaction(Fragment fragment) {
        FragmentActivity activity = fragment.getActivity();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentView, RentalIntroFragment.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(RentalIntroFragment.class.getName())
                .commit();
    }

    public void showStepper(Fragment fragment) {
        FragmentActivity activity = fragment.getActivity();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentView, GuidedRentalFragment.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
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
            return fragmentManager.popBackStackImmediate(RentalIntroFragment.class.getName(), 0);
        } else {
            return false;
        }
    }

    public void showTransactionList(CheckoutFragment fragment) {
        FragmentManager fragmentManager = fragment.getFragmentManager();
        fragmentManager.popBackStackImmediate(TransactionListFragment.class.getName(), 0);
    }

    public void dismiss(Fragment fragment) {
        fragment.getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    public void showBatterySelection(GuidedRentalFragment fragment, Battery battery) {
        FragmentActivity activity = fragment.getActivity();
        BatterySelectionFragment batterySelectionFragment = BatterySelectionFragment.newInstance(battery);
        batterySelectionFragment.setTargetFragment(fragment, 0);
        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.contentView, batterySelectionFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    public void showBatteryScanner(GuidedRentalFragment fragment, Battery battery) {
        FragmentActivity activity = fragment.getActivity();
        BatteryScannerFragment batterySelectionFragment = BatteryScannerFragment.newInstance(battery);
        batterySelectionFragment.setTargetFragment(fragment, 0);
        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.contentView, batterySelectionFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }
}
