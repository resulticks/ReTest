package io.mob.resu.reandroidsdk;

import static android.content.Context.BATTERY_SERVICE;
import static io.mob.resu.reandroidsdk.Util.getCurrentUTC;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;
import io.mob.resu.reandroidsdk.error.Log;

/**
 * Created by SDK on 08/02/18.
 */

public class OfflineCampaignTrack extends AsyncTask<String, String, String> {
    Context context;
    String id;
    String status;
    boolean isNewInstall;
    String rating;
    String comments;
    APIInterface apiInterface;
    String actionName;
    BroadcastReceiver.PendingResult pendingResult;
    private ArrayList<MData> dbCampaign;
    boolean isEnd = false;
    boolean isAMP = false;

    OfflineCampaignTrack(Context context, BroadcastReceiver.PendingResult pendingResult, String id, String status, String actionName, boolean isNewInstall, String rating, String comments, APIInterface apiInterface) {
        this.pendingResult = pendingResult;
        this.context = context;
        this.id = id;
        this.status = status;
        this.isNewInstall = isNewInstall;
        this.rating = rating;
        this.comments = comments;
        this.apiInterface = apiInterface;
        this.actionName = actionName;

    }

    OfflineCampaignTrack(Context context, String id, String status, String actionName, boolean isNewInstall, String rating, String comments, APIInterface apiInterface) {
        this.context = context;
        this.id = id;
        this.status = status;
        this.isNewInstall = isNewInstall;
        this.rating = rating;
        this.comments = comments;
        this.apiInterface = apiInterface;
        this.actionName = actionName;
    }
    OfflineCampaignTrack(Context context, String id, String status, boolean isAMP, Boolean isEnd, APIInterface apiInterface) {
        this.context = context;
        this.id = id;
        this.status = status;
        this.isAMP = isAMP;
        this.isEnd = isEnd;
        this.apiInterface = apiInterface;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.e("OfflineCampaignTrack", "Called");
        Log.e("OfflineCampaignTrack", "Called");
        Log.e("OfflineCampaignTrack", "Called");

        /*try {
            if (s != null) {
                if (new Util().hasNetworkConnection(context)) {
                    apiInterface.apiCallCampaignTracking(context, pendingResult, id, s, dbCampaign);

                    String eventName = Util.getEventName(status);
                    if (!TextUtils.isEmpty(eventName))
                        AppLifecyclePresenter.getInstance().userEventTracking(context, eventName);
                }
            }

        } catch (Exception e) {
            Log.e("OfflineCampaignTrack", "" + e.getMessage());
        }
*/
        try {
            if (!isAMP || isEnd) {

                Log.e("isAMP & isEnd ",  " isAMP " + isAMP + " isEnd " + isEnd );

                if (s != null) {
                    if (new Util().hasNetworkConnection(context)) {
                        apiInterface.apiCallCampaignTracking(context, pendingResult, id, s, dbCampaign);

                        String eventName = Util.getEventName(status);
                        if (!TextUtils.isEmpty(eventName))
                            AppLifecyclePresenter.getInstance().userEventTracking(context, eventName);
                    }
                }
            } else {
                Log.e("isAMP & isEnd ",  " isAMP " + isAMP + " isEnd " + isEnd );

            }

        } catch (Exception e) {
            Log.e("OfflineCampaignTrack", "" + e.getMessage());
        }
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            insertLocalDatabase();
            return getCampaignFromLocalDataBase(context);
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
        return null;
    }

    private void insertLocalDatabase() throws JSONException {
        try {
            if (id != null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(AppConstants.reApiParamId, id);
                jsonObject.put(AppConstants.reApiParamIsNewUser, isNewInstall);
                jsonObject.put(AppConstants.reApiParamStatus, status);
                jsonObject.put(AppConstants.APP_ID, SharedPref.getInstance().getStringValue(context, AppConstants.reSharedAPIKey));
                jsonObject.put(AppConstants.TENANT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_ID));
                jsonObject.put(AppConstants.BUSINESS_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.BUSINESS_SHORT_CODE));
                jsonObject.put(AppConstants.DEPARTMENT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.DEPARTMENT_ID));
                jsonObject.put(AppConstants.TENANT_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_SHORT_CODE));
                jsonObject.put(AppConstants.PASSPORT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.PASSPORT_ID));
                if (rating != null)
                    jsonObject.put(AppConstants.reApiParamRating, rating);
                if (comments != null)
                    jsonObject.put(AppConstants.reApiParamComments, comments);
                jsonObject.put(AppConstants.reApiParamTimeStamp, getCurrentUTC());
                // Add Entry
                getAppState(jsonObject);
                new DataBase(context).insertData(jsonObject.toString(), DataBase.Table.CAMPAIGN_TABLE);
            }
        } catch (Exception e) {

        }

    }

    void getAppState(JSONObject jsonObject) {

        try {

            if (Util.isAppIsInBackground(context))
                jsonObject.put(AppConstants.reAppMode, "Background");
            else
                jsonObject.put(AppConstants.reAppMode, "Foreground");

            jsonObject.put(AppConstants.reDeviceOs, "Android");
            jsonObject.put(AppConstants.reOsVersion, Build.VERSION.RELEASE);
            jsonObject.put(AppConstants.reAppVersion, Util.getAppVersionName(context));
            jsonObject.put(AppConstants.reIsNotificationEnabled, NotificationManagerCompat.from(context).areNotificationsEnabled());

            // Power Save mode
            try {
                PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    boolean powerSaveMode = powerManager.isPowerSaveMode();
                    jsonObject.put(AppConstants.reIsBatterySavingsModeEnabled, powerSaveMode);
                }
            } catch (Exception e) {
            }

            // DND
            try {
                int mode = Settings.Global.getInt(context.getContentResolver(), "zen_mode");
                if (mode > 0)
                    jsonObject.put(AppConstants.reIsDoNotDisturbModeEnabled, true);
                else
                    jsonObject.put(AppConstants.reIsDoNotDisturbModeEnabled, false);
            } catch (Exception ex) {

            }

            // Battery
            try {
                BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    jsonObject.put(AppConstants.reBatteryStatus, "" + bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) + "%");
                }
            } catch (Exception eq) {
            }
            // Wifi
            try {
                ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (mWifi.isConnected()) {
                    // Do whatever
                    jsonObject.put(AppConstants.reNetWorkType, "Wifi");
                } else {
                    boolean mobileDataEnabled = false; // Assume disabled
                    try {
                        Class cmClass = Class.forName(connManager.getClass().getName());
                        Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
                        method.setAccessible(true); // Make the method callable
                        // get the setting for "mobile data"
                        mobileDataEnabled = (Boolean) method.invoke(connManager);
                        if (mobileDataEnabled)
                            jsonObject.put(AppConstants.reNetWorkType, "Mobile Data");
                    } catch (Exception ex) {
                        // TODO do whatever error handling you want here
                    }
                }

            } catch (Exception e) {
            }

        } catch (Exception e) {
        }
    }

    @NonNull
    private String getCampaignFromLocalDataBase(Context context) {
        JSONObject campaignObj = new JSONObject();
        try {
            dbCampaign = new DataBase(context).getData(DataBase.Table.CAMPAIGN_TABLE);

            campaignObj.put("appId", SharedPref.getInstance().getStringValue(context, AppConstants.reSharedAPIKey));
            ArrayList<JSONObject> campaigns = new ArrayList<>();

            if (dbCampaign != null && dbCampaign.size() > 0) {
                for (MData mData : dbCampaign) {
                    String s = mData.getValues();
                    JSONObject jsonObject1 = new JSONObject(s);
                    campaigns.add(jsonObject1);
                }
            }
            if (campaigns != null && campaigns.size() > 0) {
                campaignObj.put("campaigns", new JSONArray(campaigns));
                return campaignObj.toString();
            }

        } catch (Exception e) {
            ExceptionTracker.track(e);
            return null;
        }
        return null;
    }
}
