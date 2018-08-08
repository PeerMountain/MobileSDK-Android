package com.peermountain.common.utils;

import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


/**
 * FragmentUtils
 *
 * @author Galeen
 * @since 9/11/13
 */
public class PmFragmentUtils {

    public static FragmentBuilder init(FragmentActivity activity) {
        return new FragmentBuilder(activity);
    }

    public static FragmentBuilder init(FragmentActivity activity,@IdRes int containerId) {
        return new FragmentBuilder(activity).containerId(containerId);
    }


    public static class FragmentBuilder {
        // TODO: 13.9.2017 г. add ability to set transition elements
        private final FragmentActivity activity;
        private int containerId;
        private boolean shouldAddToBackStack = true;
        private int enterAnimation = 0;
        private int exitAnimation = 0;
        private int popEnterAnimation = 0;
        private int popExitAnimation = 0;
        private int transition = FragmentTransaction.TRANSIT_FRAGMENT_FADE;
        private int transitionStyle = -1;
        private boolean justAddToBackStack = false;

        public FragmentBuilder(FragmentActivity activity) {
            this.activity = activity;
        }

        public FragmentBuilder containerId(int containerId) {
            this.containerId = containerId;
            return this;
        }

        public FragmentBuilder addToBackStack(boolean shouldAddToBackStack) {
            this.shouldAddToBackStack = shouldAddToBackStack;
            return this;
        }

        /**
         * This will only add/replace the fragment to the BackStack not going to process of creation.
         * It is helpful when you have to create flow of fragments at once and instead of creating the
         * fragment and replacing it with another will be just added to the BackStack for redirection.
         * If the fragment needs some spacial data you may wont to create it first and replaced it.
         *
         * @param justAddToBackStack default value is false
         * @return
         */
        public FragmentBuilder justAddToBackstack(boolean justAddToBackStack) {
            this.justAddToBackStack = justAddToBackStack;
            return this;
        }

        /**
         * If you set your own animation the transition is ignored!
         *
         * @param transition can be one of FragmentTransaction.TRANSIT_NONE,
         *                   TRANSIT_FRAGMENT_OPEN, TRANSIT_FRAGMENT_CLOSE, or TRANSIT_FRAGMENT_FADE
         *                   default is FragmentTransaction.TRANSIT_FRAGMENT_FADE
         * @return
         */
        public FragmentBuilder setTransition(int transition) {
            this.transition = transition;
            return this;
        }

        /**
         * If you set your own animation the transition is ignored!
         *
         * @param transitionStyle defined as a resource
         * @return
         */
        public FragmentBuilder setTransitionStyle(@StyleRes int transitionStyle) {
            this.transitionStyle = transitionStyle;
            return this;
        }

        public FragmentBuilder setAnimations(int enter, int exit, int popEnter, int popExit) {
            this.enterAnimation = enter;
            this.exitAnimation = exit;
            this.popEnterAnimation = popEnter;
            this.popExitAnimation = popExit;
            return this;
        }

        public void add(Fragment fragment, String tag) {
            FragmentTransaction ft = activity.getSupportFragmentManager()
                    .beginTransaction();
            mainActions(tag, ft);
            ft.add(containerId, fragment, tag);
            ft.commit();
        }

        private void mainActions(String tag, FragmentTransaction ft) {
            ft.setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation,
                    popExitAnimation);
            if (transitionStyle != -1) {
                ft.setTransitionStyle(transitionStyle);
            } else {
                ft.setTransition(transition);
            }
            if (shouldAddToBackStack) ft.addToBackStack(tag);
            if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                ft.setAllowOptimization(justAddToBackStack);
            }else{
                ft.setReorderingAllowed(justAddToBackStack);
            }
        }

        public void add(Fragment fragment) {
            add(fragment, fragment.getClass().getSimpleName());
        }

        public void replace(Fragment fragment, String tag) {
            FragmentTransaction ft =
                    activity.getSupportFragmentManager().beginTransaction();
            mainActions(tag, ft);
            ft.replace(containerId, fragment, tag);
            ft.commit();
        }

        public void replace(Fragment fragment) {
            replace(fragment, fragment.getClass().getSimpleName());
        }


        public void pop() {
            FragmentManager ft = activity.getSupportFragmentManager();
            ft.popBackStack();
        }

        public void remove(Fragment fragment) {
            FragmentManager ft = activity.getSupportFragmentManager();
            ft.beginTransaction().remove(fragment).commit();
        }

        public void popTo(String tag, boolean inclusive) {
            FragmentManager ft = activity.getSupportFragmentManager();
            ft.popBackStack(tag, inclusive ? FragmentManager.POP_BACK_STACK_INCLUSIVE : 0);
        }

        public void popImmediate() {
            FragmentManager ft = activity.getSupportFragmentManager();
            ft.popBackStackImmediate();
        }

        public void popToImmediate(String tag, boolean inclusive) {
            FragmentManager ft = activity.getSupportFragmentManager();
            Fragment fragment = ft.findFragmentByTag(tag);
            ft.popBackStackImmediate(fragment.getId(), inclusive ? FragmentManager.POP_BACK_STACK_INCLUSIVE : 0);
        }

        public Fragment find(String tag) {
            FragmentManager ft = activity.getSupportFragmentManager();
            return ft.findFragmentByTag(tag);
        }

        public void clearBackStack() {
            FragmentManager manager = activity.getSupportFragmentManager();
            if (manager.getBackStackEntryCount() > 0) {
                FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
                manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }
}
