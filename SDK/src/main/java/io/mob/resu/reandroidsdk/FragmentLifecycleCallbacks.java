package io.mob.resu.reandroidsdk;

import static io.mob.resu.reandroidsdk.AppConstants.CURRENT_FRAGMENT_NAME;
import static io.mob.resu.reandroidsdk.AppConstants.LAST_VISITED_FRAGMENT;
import static io.mob.resu.reandroidsdk.AppConstants.newError;
import static io.mob.resu.reandroidsdk.error.Util.isDialogFragments;
import static io.mob.resu.reandroidsdk.error.Util.isViewGroup;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.Calendar;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;
import io.mob.resu.reandroidsdk.error.Log;


class FragmentLifecycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks {

    public static View view;
    public static String newScreenName = "";
    static Fragment fragment;
    static View v;
    private final Handler handler = new Handler();
    public String SessionScreenName = "";
    boolean isOldDialogFragment = false;
    WindowChangeListener changeListener;
    private boolean isDialogFragment = false;
    private WindowChangeListener OldChangeListener;
    private boolean isFlag = false;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.e("WindowChangeListener", "Called");
            isFlag = true;
        }
    };
    private Calendar sCalendar = Calendar.getInstance();
    private Calendar oldCalendar = Calendar.getInstance();
    private String oldScreenName = "";

    FragmentLifecycleCallbacks() {
        super();
    }

    @Override
    public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
        super.onFragmentViewCreated(fm, f, v, savedInstanceState);

        try {
            if (!isDialogFragments(f)) {
                this.fragment = f;
                this.v = v;
                OldChangeListener = changeListener;
                changeListener = new WindowChangeListener();
                if (OldChangeListener == null)
                    OldChangeListener = changeListener;
                v.getViewTreeObserver().addOnGlobalLayoutListener(changeListener);
                newScreenName = f.getClass().getSimpleName();

                if (!newScreenName.equalsIgnoreCase("RePagerDialog") && !newScreenName.equalsIgnoreCase("SupportRequestManagerFragment"))
                    CURRENT_FRAGMENT_NAME = newScreenName;
                SharedPref.getInstance().setSharedValue(f.getActivity(), AppConstants.CURRENT_FRAGMENT, newScreenName);
                AppConstants.oldError = newError;
                newError = new ArrayList<>();
                if (AppConstants.oldError == null)
                    AppConstants.oldError = new ArrayList<>();
                sCalendar = Calendar.getInstance();
                if (TextUtils.isEmpty(oldScreenName)) {
                    oldScreenName = newScreenName;
                } else {
                    AppConstants.LAST_FRAGMENT_NAME = oldScreenName;
                    SharedPref.getInstance().setSharedValue(f.getActivity(), LAST_VISITED_FRAGMENT, oldScreenName);
                    AppLifecyclePresenter.getInstance().onSessionStop(f.getActivity(), oldCalendar, Calendar.getInstance(), f.getActivity().getClass().getSimpleName(), oldScreenName, null);
                    oldScreenName = newScreenName;
                    oldCalendar = sCalendar;
                    AppLifecyclePresenter.getInstance().onSessionStartFragment(f.getActivity(), newScreenName, f);
                }
                EnabledContentPublisher(f.getActivity());
                try {
                    ReAndroidSDK.onPageChangeListener.onPageChanged(f.getActivity().getClass().getSimpleName(), newScreenName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void EnabledContentPublisher(final Activity mActivity) {
        try {
            ContentPublisherManager.getInstance().EnablePublisher(mActivity);
        } catch (Exception e) {

        }
    }


    @Override
    public void onFragmentStarted(FragmentManager fm, Fragment f) {
        super.onFragmentStarted(fm, f);
        try {
            if (!isDialogFragments(f)) {
                fragment = f;
                if (isDialogFragments(f)) {
                    isOldDialogFragment = isDialogFragment;
                    isDialogFragment = true;
                    DialogFragment dialogFragment = (DialogFragment) f;
                    view = dialogFragment.getDialog().getWindow().getDecorView();
                    v = view;
                } else {
                    view = null;
                    v = f.getView();
                    isOldDialogFragment = isDialogFragment;
                    isDialogFragment = false;
                }
                SessionTimer.getInstance().startTimer(f.getActivity());
                setScreenNameTag(v, 0, f);
                setIdWiseTracking(fragment.getActivity(), fragment.getClass().getSimpleName());
            }
            /*AppRuleListener.getInstance().Init(f.getActivity());*/
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }


    }

    @Override
    public void onFragmentStopped(FragmentManager fm, Fragment f) {
        super.onFragmentStopped(fm, f);

        try {
            if (!isDialogFragments(f)) {
                isOldDialogFragment = false;
                if (v != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        v.getViewTreeObserver().removeOnGlobalLayoutListener(OldChangeListener);
                    } else {
                        v.getViewTreeObserver().removeGlobalOnLayoutListener(OldChangeListener);
                    }
                }
                Log.e("onFragmentStopped", f.getClass().getSimpleName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFragmentActivityCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        super.onFragmentActivityCreated(fm, f, savedInstanceState);

    }

    @Override
    public void onFragmentResumed(FragmentManager fm, Fragment f) {
        super.onFragmentResumed(fm, f);
        try {
            if (!isDialogFragments(f)) {
                sCalendar = Calendar.getInstance();
                oldCalendar = Calendar.getInstance();
            }
        } catch (Exception e) {

        }
    }

    private void setScreenNameTag(View view, int indent, Fragment fragment) {

        try {
            if (fragment.getClass().getSimpleName() != null && view != null) {
                view.setTag(fragment.getClass().getSimpleName());
                if (isViewGroup(view)) {
                    ViewGroup vg = (ViewGroup) view;
                    for (int i = 0; i < vg.getChildCount(); i++) {
                        setScreenNameTag(vg.getChildAt(i), indent++, fragment);
                    }
                }
            }

        } catch (Exception e) {
            ExceptionTracker.track(e);
        }

    }

    /**
     * Each Screen Field capture list wise Adding field capture listener
     *
     * @param activity
     * @param screenName
     */
    private void setIdWiseTracking(Activity activity, String screenName) {
        try {
           // new EnableFieldCapture(activity, screenName).execute();
        } catch (Exception e) {

        }
    }

    private class WindowChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            try {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 1000);
                if (isFlag) {
                    isFlag = false;
                    view = fragment.getView();
                    setScreenNameTag(view, 0, fragment);
                    setIdWiseTracking(fragment.getActivity(), fragment.getClass().getSimpleName());
                }
            } catch (Exception e) {

            }

        }
    }


}
