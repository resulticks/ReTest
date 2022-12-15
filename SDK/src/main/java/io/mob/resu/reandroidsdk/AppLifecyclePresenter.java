package io.mob.resu.reandroidsdk;

import static io.mob.resu.reandroidsdk.AppConstants.oldError;
import static io.mob.resu.reandroidsdk.AppConstants.sessionId;
import static io.mob.resu.reandroidsdk.Util.getAppCrashData;
import static io.mob.resu.reandroidsdk.Util.getCurrentUTC;
import static io.mob.resu.reandroidsdk.Util.getTime;
import static io.mob.resu.reandroidsdk.error.Util.isAppCompatActivity;
import static io.mob.resu.reandroidsdk.error.Util.isCheckBox;
import static io.mob.resu.reandroidsdk.error.Util.isEditText;
import static io.mob.resu.reandroidsdk.error.Util.isRadioButton;
import static io.mob.resu.reandroidsdk.error.Util.isRadioGroup;
import static io.mob.resu.reandroidsdk.error.Util.isRatingBar;
import static io.mob.resu.reandroidsdk.error.Util.isSeekBar;
import static io.mob.resu.reandroidsdk.error.Util.isSpinner;
import static io.mob.resu.reandroidsdk.error.Util.isSwitch;
import static io.mob.resu.reandroidsdk.error.Util.isTextView;
import static io.mob.resu.reandroidsdk.error.Util.isToggleButton;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;
import io.mob.resu.reandroidsdk.error.Log;

public class AppLifecyclePresenter implements IAppLifecycleListener {
    static FragmentLifecycleCallbacks fragmentLifecycleCallbacks;
    private static AppLifecyclePresenter trackerHelper;
    private final JSONObject referrerObject = new JSONObject();
    private final Handler handler = new Handler();
    String activityName;
    String fragmentName;
    Activity activity;
    String identifier = "_resulticks_link_unique_Id";
    String appID = "";
    /**
     * Field wise capture data Listener
     *
     * @param host
     */

    Activity mActivity;
    JSONObject mFormObj;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {

            try {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (isVaildForm())
                                new DataNetworkHandler().apiBrandFormSubmission(mActivity, mFormObj);
                            else
                                Log.e("invaild form", "form cancelled");
                        } catch (Exception e) {

                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    };
    private HashMap<String, Object> FiledTrackViews;

    public static AppLifecyclePresenter getInstance() {
        try {
            if (trackerHelper == null)
                return trackerHelper = new AppLifecyclePresenter();
            else
                return trackerHelper;
        } catch (Exception e) {
            return new AppLifecyclePresenter();
        }

    }

    /**
     * App lifecycle listener instantiate asserts
     *
     * @param activity
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    void instantiate(Activity activity) {
        try {

            if (Util.itHasFragment(activity)) {
                registerFragmentLifeCycle(activity);
            }
            try {
                Intent serviceIntent = new Intent(activity, AppKillService.class);
                activity.startService(serviceIntent);
            } catch (Exception e) {

            }
            showSplashNotification(activity);
            isDeepLinkingLaunch(activity);
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
    }

    /**
     * Notification via launch show the Splash, Banner, Serves, Rating Notification
     *
     * @param context
     */
    private void showSplashNotification(Activity context) {
        try {
            if (context.getIntent().getExtras() != null)

                if (context.getIntent().getExtras().containsKey(AppConstants.reApiParamNavigationScreen)) {
                    // React Native App Closed state
                    //if (AppConstants.isReactNative) {

                    //}
                    Bundle bundle = context.getIntent().getExtras();
                    Log.e("isDeepLinkingLaunch", "" + bundle.toString());
                    SharedPref.getInstance().setSharedValue(context, AppConstants.reNotificationViaLauncher, true);

                    // Show Notification
                    if (!bundle.containsKey("notificationViewed")) {
                        String pushType = "1";

                        if (bundle.containsKey("pushType"))
                            pushType = bundle.getString("pushType");

                        if (pushType.equalsIgnoreCase("2")) {
                            if (bundle.containsKey("clickActionId")) {
                                if (bundle.getString("clickActionId").equalsIgnoreCase("2"))
                                    scheduleNotification(context, context.getIntent().getExtras());
                            }
                        } else {
                            notifyPushNotification(context);
                        }
                        // }
                    }
                    SharedPref.getInstance().setSharedValue(context, AppConstants.reSharedCampaignId, bundle.getString(AppConstants.reApiParamId));
                    SharedPref.getInstance().setSharedValue(context, AppConstants.reSharedMobileFriendlyUrl, bundle.getString("MobileFriendlyUrl"));
                    AppNotification.cancel(context, bundle.getInt(AppConstants.reAppNotificationId));
                    Util.getCampaignDetails(context, bundle.getString("MobileFriendlyUrl"));
                }
            Bundle bundle = context.getIntent().getExtras();
            if (bundle != null) {
                try {
                    if (bundle.containsKey("clickActionId")) {
                        new OfflineCampaignTrack(context, bundle.getString("id"), bundle.getString("clickActionId"), "", false, null, null, DataNetworkHandler.getInstance()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                        if (bundle != null) {
                            try {
                                if (bundle.containsKey("clickActionId")) {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("clickActionUrl", bundle.getString("clickActionUrl", ""));
                                    jsonObject.put("clickActionType", bundle.getString("clickActionType", ""));
                                    jsonObject.put("clickActionId", bundle.getString("clickActionId"));
                                    jsonObject.put("title", bundle.getString("title"));
                                    SharedPref.getInstance().setSharedValue(activity, "actionData", jsonObject.toString());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        AppNotification.cancel(context, bundle.getInt(AppConstants.reAppNotificationId));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
    }

    public void scheduleNotification(Context context, Bundle bundle) {

        try {
            Intent notificationIntent = new Intent(context, ScheduleNotification.class);
            notificationIntent.putExtras(bundle);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, getRandom(), notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            Long futureInMillis = SystemClock.elapsedRealtime() + 3000;
            Log.e("Time", "" + futureInMillis);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
            }
        } catch (Exception e) {
            Log.e("scheduleNotification", "" + e.getMessage());
        }

    }

    public int getRandom() {
        long lowerLimit = 123L;
        long upperLimit = 234L;
        Random r = new Random();
        long number = lowerLimit + ((int) (r.nextDouble() * (upperLimit - lowerLimit)));
        return (int) number;
    }

    // React Native App Closed state
    private void notifyPushNotification(Activity context) {
        try {
            JSONObject obj = getIntent(context.getIntent().getExtras());
            if (obj != null)
                SharedPref.getInstance().setSharedValue(context, "notificationOpened", obj.toString());

            JSONObject referrerObject = new JSONObject();
            referrerObject.put(AppConstants.reDeepLinkParamIsNewInstall, false);
            referrerObject.put(AppConstants.reDeepLinkParamIsViaDeepLinkingLauncher, true);
            Object[] parameters = context.getIntent().getExtras().keySet().toArray();
            for (Object o : parameters) {
                String key = "" + o;
                String value = "" + context.getIntent().getExtras().get(key);
                referrerObject.put(key, value);
            }
            referrerObject.put("activityName", context.getIntent().getExtras().getString("navigationScreen"));
            referrerObject.put("fragmentName", context.getIntent().getExtras().getString("category"));
            SharedPref.getInstance().setSharedValue(context, AppConstants.reSharedReferral, referrerObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @NonNull
    private JSONObject getIntent(Bundle map) {
        JSONObject jsonObject = new JSONObject();
        try {

            Object[] parameters = map.keySet().toArray();
            jsonObject = new JSONObject();
            for (Object o : parameters) {
                String key = "" + o;
                String value = "" + map.get(key);
                // Log.e("key", "" + o);
                // Log.e("values", "" + map.get(key));
                jsonObject.put(key, value);
            }
            Log.e("Push Notification", "" + jsonObject);
            return jsonObject;
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
        return jsonObject;

    }

    /**
     * Notification via launch show the Splash, Banner, Serves, Rating Notification
     *
     * @param context
     */
    void getCampaignData(Activity context) {

        try {
            if (context.getIntent().getExtras() != null)
                if (context.getIntent().getExtras().containsKey(AppConstants.reApiParamNavigationScreen)) {
                    Bundle bundle = context.getIntent().getExtras();
                    SharedPref.getInstance().setSharedValue(context, AppConstants.reNotificationViaLauncher, true);
                    SharedPref.getInstance().setSharedValue(context, AppConstants.reSharedCampaignId, bundle.getString(AppConstants.reApiParamId));
                    SharedPref.getInstance().setSharedValue(context, AppConstants.reSharedMobileFriendlyUrl, bundle.getString("MobileFriendlyUrl"));
                }
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }

    }

    /**
     * Smart link app launch Listener
     *
     * @param context
     */
    private void isDeepLinkingLaunch(Activity context) {

        try {
            Log.e("isDeepLinkingLaunch", "" + context.getIntent().getData());
            Log.e("isDeepLinkingLaunch", "" + context.getIntent().getExtras());
            if (context.getIntent().getData() != null) {
                try {
                    referrerObject.put(AppConstants.reDeepLinkParamIsNewInstall, false);
                    referrerObject.put(AppConstants.reDeepLinkParamIsViaDeepLinkingLauncher, true);
                    Object[] parameters = context.getIntent().getExtras().keySet().toArray();
                    for (Object o : parameters) {
                        String key = "" + o;
                        String value = "" + context.getIntent().getExtras().get(key);
                        //Log.e("key", "" + o);
                        //Log.e("values", "" + context.getIntent().getExtras().get(key));
                        referrerObject.put(key, value);
                        //Log.e("key", "" + o);
                    }
                    referrerObject.put("activityName", context.getIntent().getExtras().getString("navigationScreen"));
                    referrerObject.put("fragmentName", context.getIntent().getExtras().getString("category"));
                    // Server update
                    if (referrerObject.has(AppConstants.reDeepLinkParamReferralId) || referrerObject.has(AppConstants.sdkDeepLinkParamReferralId)) {

                        String code = referrerObject.optString(AppConstants.reDeepLinkParamReferralId, "");
                        if (TextUtils.isEmpty(code))
                            code = referrerObject.optString(AppConstants.sdkDeepLinkParamReferralId, "");

                        if (!TextUtils.isEmpty(code) && code.contains("White")) {
                            android.util.Log.e("Device", "WhiteListed Sucess");
                            SharedPref.getInstance().setSharedValue(context, "IsWhiteListed", true);
                        }
                        campaignTracker(context, code);
                        SharedPref.getInstance().setSharedValue(context, AppConstants.reSharedCampaignId, code);
                    }
                    // SharedPref.getInstance().setSharedValue(context, AppConstants.resulticksSharedReferral), referrerObject.toString());
                    SharedPref.getInstance().setSharedValue(context, AppConstants.reDeepLinkParamIsViaDeepLinkingLauncher, true);


                } catch (Exception f) {
                    f.printStackTrace();
                }

                if (context.getIntent().getData().toString().contains("https://resu.io/")) {
                    getSmartLinkDetails(context.getIntent().getData().toString());
                    SharedPref.getInstance().setSharedValue(context, AppConstants.reDeepLinkParamIsViaDeepLinkingLauncher, true);
                }
            }


        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
    }

    void getSmartLinkDetails(String smartLink) {
        try {

            IGetQRLinkDetail iGetQRLinkDetail = new IGetQRLinkDetail() {
                @Override
                public void onSmartLinkDetails(String Data) {
                    try {
                        JSONObject jsonObject = new JSONObject(Data);
                        SharedPref.getInstance().setSharedValue(activity, AppConstants.reSharedMobileFriendlyUrl, jsonObject.getString("MobileFriendlyUrl"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {

                }
            };

            try {
                String shortCodes = smartLink.replace("http://resu.io/", "").replace("https://resu.io/", "").replace("/", "");
                SharedPref.getInstance().setSharedValue(activity, AppConstants.reSharedCampaignId, shortCodes);
                DataNetworkHandler.getInstance().getCampaignDetails(activity, null, iGetQRLinkDetail);
                DataNetworkHandler.getInstance().apiCallSmartLink(activity, smartLink);
            } catch (Exception e) {
                ExceptionTracker.track(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * Add fragment lifecycle register callbacks
     *
     * @param mActivity
     */
    private void registerFragmentLifeCycle(Activity mActivity) {
        // Fragment Screens
        try {
            if (isAppCompatActivity(mActivity)) {
                if (fragmentLifecycleCallbacks != null) {
                    FragmentLifecycleCallbacks.fragment = null;
                    FragmentLifecycleCallbacks.v = null;
                    FragmentLifecycleCallbacks.view = null;
                }
                FragmentManager manager = ((FragmentActivity) mActivity).getSupportFragmentManager();
                fragmentLifecycleCallbacks = new FragmentLifecycleCallbacks();
                manager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);
            }
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
    }

    /**
     * Custom Event Type: 1
     * <p>
     * eventName: i.e Product purchased
     * <p>
     * data:   { "productName:"Nexus 5","amount": "35,000" }
     *
     * @param context
     * @param data
     * @param eventName
     */
    void userEventTracking(Context context, JSONObject data, String eventName) {

        try {
            String timeStamp = getCurrentUTC();
            JSONObject eventObject = new JSONObject();
            eventObject.put("eventName", eventName);
            eventObject.put("data", data.toString());
            eventObject.put("timeStamp", timeStamp);
            eventObject.put("sessionId", sessionId);
            AppRuleListener.getInstance().processCustomEventRules(context, data, eventName);
            DataNetworkHandler.getInstance().apiCallUpdateEvents(context, eventObject.toString());
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }

    }

    /**
     * Custom Event Type: 2
     * <p>
     * eventName:  i.e NFC detected, QR Code Scanned
     *
     * @param context
     * @param eventName
     */
    void userEventTracking(Context context, String eventName) {

        try {

            String timeStamp = getCurrentUTC();
            JSONObject eventObject = new JSONObject();
            eventObject.put("eventName", eventName);
            eventObject.put("timeStamp", timeStamp);
            eventObject.put("sessionId", sessionId);
            AppRuleListener.getInstance().processCustomEventRules(context, null, eventName);
            DataNetworkHandler.getInstance().apiCallUpdateEvents(context, eventObject.toString());
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }

    }

    void formDataCapture(Context context, JSONObject formData) {
        try {
            DataNetworkHandler.getInstance().apiFormDataCapture(context, formData);
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
    }

    /**
     * Campaign Wise User engagement update
     *
     * @param context
     * @param id
     * @throws JSONException
     */
    void campaignTracker(Context context, String id) {

        try {
            new DataBase(context).markRead(id, DataBase.Table.NOTIFICATION_TABLE, true);
        } catch (Exception e) {

        }
        try {
            if (id != null)
                new OfflineCampaignTrack(context, id, AppConstants.NOTIFICATION_OPEN, "Opened", false, null, null, DataNetworkHandler.getInstance()).execute();
        } catch (Exception e) {

        }
    }

    /**
     * Screen Session Listener
     *
     * @param context
     * @param start
     * @param end
     * @param screenName
     * @param subScreenName
     * @param appCrash
     */
    @Override
    public void onSessionStop(Context context, Calendar start, Calendar end, String screenName, String subScreenName, String appCrash) {
        try {
            Log.e("ScreenName", screenName);
            JSONObject screenActivities = new JSONObject();
            screenActivities.put(AppConstants.reApiParamStartTime, getTime(start));
            screenActivities.put(AppConstants.reApiParamEndTime, getTime(end));
            screenActivities.put(AppConstants.reApiParamScreenName, screenName);
            screenActivities.put(AppConstants.reApiParamSubScreenName, subScreenName);
            screenActivities.put("isNotification", NotificationManagerCompat.from(context).areNotificationsEnabled());
            int visitorCount = SharedPref.getInstance().getIntValue(context, "visitorCount");
            screenActivities.put("visitorCount", visitorCount);
            screenActivities.put("sessionId", sessionId);
            getAppCrashData(context, appCrash, screenActivities);
            screenActivities.put(AppConstants.reApiParamErrorLog, new JSONArray(oldError));
            try {
                Activity activity = (FragmentActivity) context;
                AppConstants.screenData = "";
                Util.readScreenData(activity.getWindow().getDecorView().getRootView());
                //screenActivities.put("screenData", AppConstants.screenData);
            } catch (Exception er) {

            }
            new ScreenSessionRecord(context, screenActivities, FiledTrackViews).execute();
            FiledTrackViews = new HashMap<>();
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }

    }

    @Override
    public void onSessionStartActivity(Activity mActivity, String screenName) {
        try {
            if (Util.itHasFragment(mActivity)) {
                try {
                    /*int count = new DataBase(mActivity).getData(DataBase.Table.SCREENS_TABLE).size();
                    if (count >= 3) {*/
                    new OfflineScreenTrack(mActivity, DataNetworkHandler.getInstance()).execute();
                    //}
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
            setIdWiseTracking(mActivity, screenName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSessionStartFragment(Activity mActivity, String screenName, Fragment fragment) {
        try {

            try {
                /*int count = new DataBase(mActivity).getData(DataBase.Table.SCREENS_TABLE).size();
                if (count >= 3) {*/
                new OfflineScreenTrack(mActivity, DataNetworkHandler.getInstance()).execute();
                //}
            } catch (Exception f) {
                f.printStackTrace();
            }

            setIdWiseTracking(mActivity, screenName);
        } catch (Exception e) {

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
            this.activity = activity;
            new EnableFieldCapture(activity, screenName).execute();
        } catch (Exception e) {

        }

    }

    void fieldWiseDataListener(View host) {

        try {
            String viewId;
            if (FiledTrackViews == null) {
                FiledTrackViews = new HashMap<>();
            }
            Object object = host.getTag();
            String tag = "";
            if (object != null) {
                if (object instanceof String)
                    tag = (String) object;
            }
            if (TextUtils.isEmpty(tag)) {
                Activity activity = getActivity(host);
                if (activity != null)
                    tag = activity.getClass().getSimpleName();
                else
                    tag = host.getContext().getClass().getSimpleName();
            }


            if (AppConstants.isReactNative) {
                viewId = "" + host.getId();
                FiledTrackViews.put("" + viewId, host);
            } else {
                String[] id = host.getResources().getResourceName(host.getId()).split("/");
                viewId = id[1];
                FiledTrackViews.put(id[1] + "/" + tag, host);
            }

            mActivity = getActivity(host);

            try {
                ArrayList<JSONObject> list = new DataBase(host.getContext()).getFieldData(DataBase.Table.REGISTER_EVENT_TABLE, viewId, tag);
                for (JSONObject jsonObject : list) {
                    if (jsonObject.has("formId")) {
                        if (jsonObject.optBoolean("markAsSubmit", false)) {
                            String tenantId = SharedPref.getInstance().getStringValue(host.getContext(), AppConstants.TENANT_ID).replace("cust_", "").replace("camp_", "").replace("resulsdk_", "").replace("rpt_", "");
                            String formId = jsonObject.getString("formId");
                            getFieldData(host.getContext());
                            ArrayList<JSONObject> jsonObjects = new DataBase(host.getContext()).getFormData(DataBase.Table.BRAND_OWN_FORM_TABLE, formId);
                            JSONObject formSubObject = new JSONObject();
                            formSubObject.put("formId", formId);
                            formSubObject.put("APIKey", tenantId);
                            formSubObject.put("SourceUrl", tag);
                            formSubObject.put("pagereferrerurl", "");
                            formSubObject.put("pagetitle", "");
                            formSubObject.put("rid", SharedPref.getInstance().getStringValue(host.getContext(), AppConstants.PASSPORT_ID));
                            formSubObject.put("cid", SharedPref.getInstance().getStringValue(host.getContext(), AppConstants.CAMPAIGN_ID));
                            formSubObject.put("formData", new JSONArray(jsonObjects));
                            Log.e("Brand Form Data", formSubObject.toString());
                            mFormObj = formSubObject;
                            handler.removeCallbacks(runnable);
                            handler.postDelayed(runnable, 2000);
                        }
                    }
                }
            } catch (Exception e) {

            }

        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
    }

    boolean isVaildForm() {
        try {
            JSONArray jsonArray = mFormObj.getJSONArray("formData");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (Boolean.parseBoolean(jsonObject.optString("requiredfield"))) {
                    String value = jsonObject.optString("fieldvalue");
                    if (TextUtils.isEmpty(value) || value.equalsIgnoreCase("null"))
                        return false;
                }
            }
        } catch (Exception e) {

        }

        return true;
    }

    private Activity getActivity(View v) {
        try {
            Context context = v.getContext();
            while (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
                context = ((ContextWrapper) context).getBaseContext();
            }
        } catch (Exception e) {

        }
        return null;
    }

    private void getFieldData(Context mActivity) throws Exception {
        try {
            String key = "";

            if (FiledTrackViews != null) {
                Log.e("EditText Record ", "" + FiledTrackViews.size());
                if (FiledTrackViews.size() > 0) {
                    ArrayList<JSONObject> capturedList = new ArrayList<>();

                    for (Map.Entry map : FiledTrackViews.entrySet()) {
                        key = "" + map.getKey();
                        View host = (View) map.getValue();
                        Object object = host.getTag();
                        String tag;
                        String viewId = "";
                        if (AppConstants.isReactNative) {
                            viewId = "" + host.getId();
                        } else {
                            String[] id = host.getResources().getResourceName(host.getId()).split("/");
                            viewId = id[1];
                        }

                        if (object instanceof String) {
                            tag = (String) object;
                            GetViewFieldList(new DataBase(mActivity).getFieldData(DataBase.Table.REGISTER_EVENT_TABLE, viewId, tag), capturedList, host, viewId);
                        } else {
                            Activity activity = getActivity(host);
                            if (activity != null) {
                                tag = activity.getClass().getSimpleName();
                            } else {
                                tag = host.getContext().getClass().getSimpleName();
                            }
                            GetViewFieldList(new DataBase(mActivity).getFieldData(DataBase.Table.REGISTER_EVENT_TABLE, viewId, tag), capturedList, host, viewId);
                        }

                    }

                    for (JSONObject jsonObject : capturedList) {
                        if (jsonObject.has("formId")) {
                            String fieldValue = jsonObject.getString("result");
                            String formId = jsonObject.optString("formId");
                            String fieldName = jsonObject.optString("fieldName");
                            String fieldType = jsonObject.optString("fieldType");
                            String requiredfield = jsonObject.optString("requiredfield");
                            String screenName = jsonObject.optString("screenName");
                            String viewId = jsonObject.optString("identifier");
                            new DataBase(mActivity).insertDataBrandWon(DataBase.Table.BRAND_OWN_FORM_TABLE, screenName, viewId, formId, fieldName, fieldValue, fieldType, requiredfield);
                            FiledTrackViews.remove(viewId + "/" + screenName);
                        }
                    }
                }

                // Cordova relative
                if (AppConstants.isCordova) {
                    ArrayList<JSONObject> fieldTrack = new ArrayList<>();
                    ArrayList<JSONObject> brandFormField = new ArrayList<>();
                    for (int i = 0; i > AppConstants.hybridFieldTrack.length(); i++) {
                        JSONObject jsonObject = AppConstants.hybridFieldTrack.getJSONObject(i);
                        if (jsonObject.has("formId")) {
                            String fieldValue = jsonObject.getString("result");
                            String formId = jsonObject.optString("formId");
                            String fieldName = jsonObject.optString("fieldName");
                            String fieldType = jsonObject.optString("fieldType");
                            String screenName = jsonObject.optString("screenName");
                            String requiredfield = jsonObject.optString("requiredfield");
                            String viewId = jsonObject.optString("identifier");
                            new DataBase(mActivity).insertDataBrandWon(DataBase.Table.BRAND_OWN_FORM_TABLE, screenName, viewId, formId, fieldName, fieldValue, fieldType, requiredfield);
                            FiledTrackViews.remove(viewId + "/" + screenName);
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void GetViewFieldList(ArrayList<JSONObject> fieldData, ArrayList<JSONObject> capturedList, View host, String viewId) throws JSONException {

        try {
            if (fieldData != null && fieldData.size() > 0) {
                for (JSONObject jsonObject : fieldData) {
                    jsonObject.put("viewId", viewId);
                    if (jsonObject.getString("identifier").contains(viewId)) {
                        String result;
                        if (isEditText(host)) {
                            result = "" + ((EditText) host).getText();
                        } else if (isTextView(host)) {
                            result = "" + ((TextView) host).getText();
                        } else if (isSwitch(host)) {
                            result = "" + ((Switch) host).isChecked();
                        } else if (isRatingBar(host)) {
                            result = "" + ((RatingBar) host).getRating();
                        } else if (isSeekBar(host)) {
                            result = "" + ((SeekBar) host).getProgress();
                        } else if (isRadioButton(host)) {
                            result = "" + ((RadioButton) host).isChecked();
                        } else if (isRadioGroup(host)) {
                            int rating = ((RadioGroup) host).getCheckedRadioButtonId();
                            RadioButton radioButton = host.findViewById(rating);
                            result = "" + radioButton.isChecked();
                        } else if (isCheckBox(host)) {
                            result = "" + ((CheckBox) host).isChecked();
                        } else if (isSpinner(host)) {
                            result = "" + ((Spinner) host).getSelectedItem().toString();
                            if (AppConstants.isReactNative) {
                                result = getReactSpinnerValue(result);
                            }
                        } else if (isToggleButton(host)) {
                            result = "" + ((ToggleButton) host).isChecked();
                        } else {
                            result = "Clicked";
                        }

                        if (jsonObject.getString("captureType").equalsIgnoreCase("value"))
                            jsonObject.put("result", result);
                        else if (jsonObject.getString("captureType").equalsIgnoreCase("length"))
                            jsonObject.put("result", result.length());
                        else if (jsonObject.getString("captureType").equalsIgnoreCase("Click"))
                            jsonObject.put("result", "Clicked");
                    }
                    capturedList.add(jsonObject);
                }
            }
        } catch (Exception e) {

        }
    }

    private String getReactSpinnerValue(String val) {
        String result = "";
        try {
            JSONObject obj = new JSONObject(val);
            String nativeMap = obj.getString("NativeMap");
            obj = new JSONObject(nativeMap);
            result = obj.getString("label");
        } catch (Exception e) {
            Log.e("Parsing Exception :", e.getMessage());
        }
        return result;
    }

    /**
     * Background Thread enabled
     */
    private class Init extends AsyncTask<String, String, String> {

        Activity activity;

        Init(Activity activity) {
            this.activity = activity;
        }

        protected String doInBackground(String... urls) {
            if (Util.itHasFragment(activity)) {
                registerFragmentLifeCycle(activity);
            }
            showSplashNotification(activity);
            isDeepLinkingLaunch(activity);
            return "";
        }

    }


}
