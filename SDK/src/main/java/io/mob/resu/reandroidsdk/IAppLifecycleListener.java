package io.mob.resu.reandroidsdk;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;

import java.util.Calendar;


interface IAppLifecycleListener {

    void onSessionStop(Context context, Calendar start, Calendar end, String screenName, String subScreenName, String appCrash);

    void onSessionStartActivity(Activity mActivity, String screenName);

    void onSessionStartFragment(Activity mActivity, String screenName, Fragment fragment);

}

