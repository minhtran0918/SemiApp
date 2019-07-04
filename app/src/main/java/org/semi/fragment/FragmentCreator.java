package org.semi.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentCreator {
    private int container;
    private FragmentManager fragmentManager;
    private Fragment savedFragment;
    private List<Class<? extends Fragment>> classes;
    private List<Bundle> bundles;
    private boolean isStateLoss;
    private final static String TAG = "F";

    public FragmentCreator(int container, FragmentManager fragmentManager) {
        this.container = container;
        this.fragmentManager = fragmentManager;
        this.classes = new ArrayList<>();
        this.bundles = new ArrayList<>();
        this.isStateLoss = false;
    }

    public FragmentCreator add(Class<? extends Fragment> fragmentClass, Bundle...bundles) {
        classes.add(fragmentClass);
        if(bundles.length > 0) {
            this.bundles.addAll(Arrays.asList(bundles));
        }
        return this;
    }

    public Fragment setCurrentFragment(int index) {
        return setCurrentFragment(index, index);
    }

    public Fragment setCurrentFragmentNoArgs(int index) {
        if(index < 0 || index >= classes.size()) {
            return null;
        }
        final Class<? extends Fragment> fragmentClass = classes.get(index);
        final Fragment currentFragment = fragmentManager.findFragmentByTag(TAG);
        if(fragmentClass.isInstance(currentFragment)) {
            return savedFragment;
        }
        try {
            savedFragment = fragmentClass.newInstance();
            setFragment();
            return savedFragment;
        } catch (IllegalAccessException | InstantiationException e) {
            Log.w("my_error", e.getMessage());
        }
        return null;
    }

    public Fragment setCurrentFragment(int fragIndex, int argIndex) {
        if(fragIndex < 0 || fragIndex >= classes.size() || argIndex < 0 ||
                argIndex >= bundles.size()) {
            return null;
        }
        final Class<? extends Fragment> fragmentClass = classes.get(fragIndex);
        final Fragment currentFragment = fragmentManager.findFragmentByTag(TAG);
        if(fragmentClass.isInstance(currentFragment) &&
                currentFragment.getArguments() == bundles.get(argIndex)) {
            return savedFragment;
        }
        try {
            savedFragment = fragmentClass.newInstance();
            final Bundle bundle = bundles.get(argIndex);
            if(bundle != null) {
                savedFragment.setArguments(bundle);
            }
            setFragment();
            return savedFragment;
        } catch (IllegalAccessException | InstantiationException e) {
            Log.w("my_error", e.getMessage());
        }
        return null;
    }

    public Fragment getCurrentFragment() {
        return fragmentManager.findFragmentByTag(TAG);
    }

    public void recovery() {
        if(isStateLoss) {
            setFragment();
        }
    }

    private void setFragment() {
        if(savedFragment == null) {
            return;
        }
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(container, savedFragment, TAG);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        try {
            transaction.commit();
            isStateLoss = false;
        } catch (IllegalStateException exp) {
            isStateLoss = true;
        }
    }
}
