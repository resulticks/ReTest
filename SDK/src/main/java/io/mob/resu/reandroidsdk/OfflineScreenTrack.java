package io.mob.resu.reandroidsdk;

import static io.mob.resu.reandroidsdk.AppConstants.CURRENT_ACTIVITY_NAME;
import static io.mob.resu.reandroidsdk.AppConstants.CURRENT_FRAGMENT_NAME;
import static io.mob.resu.reandroidsdk.AppConstants.appOpenTime;
import static io.mob.resu.reandroidsdk.AppConstants.isLogin;
import static io.mob.resu.reandroidsdk.AppConstants.lastDeviceData;
import static io.mob.resu.reandroidsdk.AppConstants.lastJourneyData;
import static io.mob.resu.reandroidsdk.AppConstants.pageStartTime;
import static io.mob.resu.reandroidsdk.AppConstants.screenViews;

import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;
import io.mob.resu.reandroidsdk.error.Log;

/**
 * Created by SDK on 08/02/18.
 */

public class OfflineScreenTrack extends AsyncTask<String, String, String> {
    private final APIInterface apiInterface;
    Context context;
    private ArrayList<MData> dbScreen;

    OfflineScreenTrack(Context context, APIInterface apiInterface) {
        this.context = context;
        this.apiInterface = apiInterface;
    }

    @Override
    protected void onPostExecute(String screenObject) {
        super.onPostExecute(screenObject);
        try {
            if (dbScreen.size() > 0)
                DataNetworkHandler.getInstance().apiScreenTracking(context, screenObject, dbScreen);
        } catch (Exception e) {
            Log.e("OfflineScreenTrack", "" + e.getMessage());
        }
    }

    @Override
    protected String doInBackground(String... params) {
        screenViews = screenViews + 1;
        JSONObject jsonObject = null;
        try {
            if (new Util().hasNetworkConnection(context)) {
                dbScreen = new DataBase(context).getData(DataBase.Table.SCREENS_TABLE);
                if (dbScreen.size() >= 3) {
                    ArrayList<JSONObject> screenArrayList = new ArrayList<>();
                    if (dbScreen != null && dbScreen.size() > 0) {
                        for (MData mData : dbScreen) {
                            JSONObject newobj = new JSONObject(mData.getValues());
                            screenArrayList.add(newobj);
                        }
                    }
                    try {
                        new DataBase(context).deleteData(dbScreen, DataBase.Table.SCREENS_TABLE);
                    } catch (Exception e) {
                        ExceptionTracker.track(e);
                    }
                    if (screenArrayList.size() > 0) {
                        String deviceType;
                        if (isTablet(context))
                            deviceType = "Android Tab";
                        else
                            deviceType = "Android Phone";
                        jsonObject = new JSONObject();
                        jsonObject.put("appId", SharedPref.getInstance().getStringValue(context, AppConstants.reSharedAPIKey));
                        jsonObject.put("deviceOs", "Android");
                        jsonObject.put("deviceType", deviceType);

                        try {
                            String url = SharedPref.getInstance().getStringValue(context, AppConstants.reSharedMobileFriendlyUrl);
                            if (url != null) {
                                if (!url.equalsIgnoreCase("null"))
                                    jsonObject.put("mobileFriendlyUrl", SharedPref.getInstance().getStringValue(context, AppConstants.reSharedMobileFriendlyUrl));
                                else
                                    jsonObject.put("mobileFriendlyUrl", "");

                            }
                        } catch (Exception e) {
                            jsonObject.put("mobileFriendlyUrl", "");
                        }

                        jsonObject.put("campaignId", SharedPref.getInstance().getStringValue(context, AppConstants.reSharedCampaignId));
                        jsonObject.put("userId", SharedPref.getInstance().getStringValue(context, AppConstants.reSharedUserId));
                        jsonObject.put("deviceId", SharedPref.getInstance().getStringValue(context, AppConstants.reSharedDatabaseDeviceId));
                        jsonObject.put(AppConstants.APP_ID, SharedPref.getInstance().getStringValue(context, AppConstants.reSharedAPIKey));
                        jsonObject.put(AppConstants.TENANT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_ID));
                        jsonObject.put(AppConstants.BUSINESS_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.BUSINESS_SHORT_CODE));
                        jsonObject.put(AppConstants.DEPARTMENT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.DEPARTMENT_ID));
                        jsonObject.put(AppConstants.TENANT_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_SHORT_CODE));
                        jsonObject.put(AppConstants.PASSPORT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.PASSPORT_ID));
                        jsonObject.put(AppConstants.CAMPAIGN_NAME, SharedPref.getInstance().getStringValue(context, AppConstants.CAMPAIGN_NAME));
                        jsonObject.put(AppConstants.CAMPAIGN_REFERRAL, SharedPref.getInstance().getStringValue(context, AppConstants.CAMPAIGN_REFERRAL));
                        jsonObject.put(AppConstants.CAMPAIGN_ID, SharedPref.getInstance().getStringValue(context, AppConstants.CAMPAIGN_ID));
                        jsonObject.put(AppConstants.CAMPAIGN_CHANNEL, SharedPref.getInstance().getStringValue(context, AppConstants.CAMPAIGN_CHANNEL));
                        jsonObject.put("screen", new JSONArray(screenArrayList));


                        getDeviceData(context, jsonObject);
                    }
                }
            }
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
        if (jsonObject != null)
            return jsonObject.toString();
        else return null;
    }

    boolean isTablet(Context context) {
        try {
            boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
            boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
            return (xlarge || large);
        } catch (Exception e) {
            return false;
        }
    }

    void getDeviceData(Context context, JSONObject jsonObject) {
        try {
            String passportID = SharedPref.getInstance().getStringValue(context, AppConstants.PASSPORT_ID);
            int visitorCount = SharedPref.getInstance().getIntValue(context, "visitorCount");


         /*   try {
                String data = SharedPref.getInstance().getStringValue(context, "deviceDatas");
                if (!TextUtils.isEmpty(data)) {
                    lastDeviceData = new JSONObject(data);
                }
            } catch (Exception es) {

            }*/


            // App details
            if (AppConstants.deviceData == null) {
                AppConstants.deviceData = new JSONObject();
                MDeviceData deviceData = new MDeviceData(context);
                AppConstants.deviceData.put("deviceType", deviceData.getDeviceType());
                AppConstants.deviceData.put("deviceModel", deviceData.getDeviceModel());
                AppConstants.deviceData.put("manufacture", deviceData.getDeviceManufacture());
                AppConstants.deviceData.put("networkProvider", Util.getMobileNetworkOperator(context));
                AppConstants.deviceData.put("language", Locale.getDefault().getDisplayLanguage());
                AppConstants.deviceData.put("timezone", Util.getTimezone());
                AppConstants.deviceData.put("screenResolution", Util.getScreenResolution(context));
                AppConstants.deviceData.put("appInstallDate", Util.getAppFirstInstallTime(context));
                AppConstants.deviceData.put("appUpdatedDate", Util.getAppUpdateTime(context));
                AppConstants.deviceData.put("appVersionCode", deviceData.getAppVersionCode());
                AppConstants.deviceData.put("osType", deviceData.getDeviceOs());
                AppConstants.deviceData.put("version", deviceData.getDeviceOsVersion());
                AppConstants.deviceData.put("deviceModel", deviceData.getDeviceModel());
                AppConstants.deviceData.put("ipAddress", Util.getIpAddress(context));
                AppConstants.deviceData.put("isNotificationEnabled", NotificationManagerCompat.from(context).areNotificationsEnabled());
                AppConstants.deviceData.put("advertiserId", SharedPref.getInstance().getStringValue(context, "AdvertiserID"));
                AppConstants.deviceData.put(AppConstants.BUSINESS_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.BUSINESS_SHORT_CODE));
                AppConstants.deviceData.put(AppConstants.DEPARTMENT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.DEPARTMENT_ID));
                AppConstants.deviceData.put(AppConstants.PASSPORT_ID, passportID);
                if (visitorCount == 1)
                    AppConstants.deviceData.put("visitorsType", "New visitor");
                else
                    AppConstants.deviceData.put("visitorsType", "Returning visitor");

                JSONObject object = new JSONObject();
                try {
                    if (lastDeviceData != null) {
                        Iterator<String> stringSet = AppConstants.deviceData.keys();
                        while (stringSet.hasNext()) {
                            String key = stringSet.next();
                            String value1 = AppConstants.deviceData.optString(key, "");
                            String value2 = lastDeviceData.optString(key, "");
                            if (!value1.equalsIgnoreCase(value2)) {
                                if (!TextUtils.isEmpty(value1))
                                    object.put(key, value1);
                            }
                        }
                    } else {
                        Iterator<String> stringSet = AppConstants.deviceData.keys();
                        while (stringSet.hasNext()) {
                            String key = stringSet.next();
                            String value1 = AppConstants.deviceData.optString(key, "");
                            if (!TextUtils.isEmpty(value1))
                                object.put(key, value1);
                        }
                    }
                } catch (Exception efw) {
                    efw.printStackTrace();
                }
                object.put("deviceId", deviceData.getDeviceId());
                object.put(AppConstants.APP_ID, SharedPref.getInstance().getStringValue(context, AppConstants.reSharedAPIKey));
                SharedPref.getInstance().setSharedValue(context, "deviceDatas", AppConstants.deviceData.toString());
                jsonObject.put("deviceAttribute", object);
            }

            // Journey Data
            JSONObject journeyAttribute = new JSONObject();
            if (passportID == null || TextUtils.isEmpty(passportID) || passportID.contains("T_")) {
                if (visitorCount == 1)
                    journeyAttribute.put("userType", "UnKnown");
                else
                    journeyAttribute.put("userType", "Known");

            } else {
                journeyAttribute.put("userType", "Identified");
            }

            journeyAttribute.put("sessionId", AppConstants.sessionId);
            journeyAttribute.put("latitude", SharedPref.getInstance().getStringValue(context, "latitude"));
            journeyAttribute.put("longitude", SharedPref.getInstance().getStringValue(context, "longitude"));
            journeyAttribute.put("lastTimeSpent", SharedPref.getInstance().getStringValue(context, "lastTimeSpent"));
            journeyAttribute.put("lastSessionTime", SharedPref.getInstance().getStringValue(context, "lastSessionTime"));
            Long sessionTime = Long.parseLong(Util.appSession(Util.getStringToUTC(appOpenTime), Util.getStringToUTC(Util.getCurrentUTC())));
            Long screenTime = Long.parseLong(Util.appSession(Util.getStringToUTC(pageStartTime), Util.getStringToUTC(Util.getCurrentUTC())));
            SharedPref.getInstance().setSharedValue(context, "lastTimeSpent", "" + screenTime);
            journeyAttribute.put("screenViews", AppConstants.screenViews);
            journeyAttribute.put("activityName", context.getClass().getSimpleName());
            journeyAttribute.put("fragmentName", CURRENT_FRAGMENT_NAME);
            journeyAttribute.put("lastVisitedActivityName", AppConstants.LAST_ACTIVITY_NAME);
            journeyAttribute.put("lastVisitedFragmentName", AppConstants.LAST_FRAGMENT_NAME);
            journeyAttribute.put("token", SharedPref.getInstance().getStringValue(context, "token"));
            journeyAttribute.put("appOpenedDate", Util.getStringToUTC(appOpenTime));
            journeyAttribute.put("appClosedDate", Util.getStringToUTC(Util.getCurrentUTC()));
            journeyAttribute.put("timeSpentOnScreen", screenTime);
            journeyAttribute.put("screenViews", AppConstants.screenViews);
            journeyAttribute.put("sessionDuration", sessionTime);
            journeyAttribute.put("login", isLogin);
            journeyAttribute.put(AppConstants.CITY, SharedPref.getInstance().getStringValue(context, "CurrentCityName"));
            journeyAttribute.put(AppConstants.STATE, SharedPref.getInstance().getStringValue(context, "CurrentStateName"));
            journeyAttribute.put(AppConstants.COUNTRY, SharedPref.getInstance().getStringValue(context, "CurrentCountryName"));
            journeyAttribute.put(AppConstants.CAMPAIGN_NAME, SharedPref.getInstance().getStringValue(context, AppConstants.CAMPAIGN_NAME));
            journeyAttribute.put(AppConstants.CAMPAIGN_REFERRAL, SharedPref.getInstance().getStringValue(context, AppConstants.CAMPAIGN_REFERRAL));
            journeyAttribute.put(AppConstants.CAMPAIGN_ID, SharedPref.getInstance().getStringValue(context, AppConstants.CAMPAIGN_ID));
            journeyAttribute.put(AppConstants.CAMPAIGN_CHANNEL, SharedPref.getInstance().getStringValue(context, AppConstants.CAMPAIGN_CHANNEL));
            journeyAttribute.put(AppConstants.BUSINESS_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.BUSINESS_SHORT_CODE));
            journeyAttribute.put(AppConstants.DEPARTMENT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.DEPARTMENT_ID));
            journeyAttribute.put(AppConstants.TENANT_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_SHORT_CODE));
            journeyAttribute.put(AppConstants.PASSPORT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.PASSPORT_ID));

            JSONObject jsonObject1 = new JSONObject();

            try {
                /*String val = SharedPref.getInstance().getStringValue(context, "journeyDatas");
                if (!TextUtils.isEmpty(val)) {
                    lastJourneyData = new JSONObject(val);
                }*/
                if (lastJourneyData != null) {
                    Iterator<String> stringSet = journeyAttribute.keys();
                    while (stringSet.hasNext()) {
                        String key = stringSet.next();
                        String value1 = journeyAttribute.optString(key, "");
                        String value2 = lastJourneyData.optString(key, "");
                        if (!value1.equalsIgnoreCase(value2)) {
                            if (!TextUtils.isEmpty(value1))
                                jsonObject1.put(key, value1);
                        }
                    }
                } else {
                    Iterator<String> stringSet = journeyAttribute.keys();
                    while (stringSet.hasNext()) {
                        String key = stringSet.next();
                        String value1 = journeyAttribute.optString(key, "");
                        if (!TextUtils.isEmpty(value1))
                            jsonObject1.put(key, value1);
                    }
                }
            } catch (Exception efd) {
                efd.printStackTrace();
            }
            jsonObject1.put(AppConstants.APP_ID, SharedPref.getInstance().getStringValue(context, AppConstants.reSharedAPIKey));
            jsonObject1.put("deviceId", Util.getDeviceId(context));
            jsonObject.put("journeyAttribute", jsonObject1);
            SharedPref.getInstance().setSharedValue(context, "journeyDatas", jsonObject1.toString());
            lastJourneyData = journeyAttribute;
            // Resume journey data
            // if (AppConstants.CRESUME_JOURNEY) {
            String deviceAppMapID = SharedPref.getInstance().getStringValue(context, "deviceAppMapID");
            JSONObject resumeJourney = new JSONObject();
            if (TextUtils.isEmpty(passportID))
                passportID = Util.getDeviceId(context);

            resumeJourney.put("id", deviceAppMapID + passportID);
            resumeJourney.put("source", "mobile");
            resumeJourney.put("activityName", CURRENT_ACTIVITY_NAME);
            resumeJourney.put("fragmentName", CURRENT_FRAGMENT_NAME);
            jsonObject.put("resumeJourney", resumeJourney);
            // }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
