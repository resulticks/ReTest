package io.mob.resu.reandroidsdk;

import static io.mob.resu.reandroidsdk.AppConstants.CURRENT_FRAGMENT_NAME;
import static io.mob.resu.reandroidsdk.AppConstants.appOpenTime;
import static io.mob.resu.reandroidsdk.AppConstants.isLogin;
import static io.mob.resu.reandroidsdk.AppConstants.pageStartTime;
import static io.mob.resu.reandroidsdk.AppRuleListener.sessionRules;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import io.mob.resu.reandroidsdk.error.Log;

public class SessionTimer {

    static SessionTimer sessionTimer;
    private final Handler lessThen3Sec = new Handler();
    private final Handler lessThen10Sec = new Handler();
    private final Handler moreThen10Sec = new Handler();
    private final Handler moreThen30Sec = new Handler();
    private final Handler moreThen60Sec = new Handler();
    Activity context;
    private final Runnable lessThen3SecTimer = new Runnable() {
        @Override
        public void run() {
            try {

                String s = SharedPref.getInstance().getStringValue(context, "actionData");
                Log.e("Click Data", s);
                Log.e("Click Data", s);
                Log.e("Click Data", s);

                if (!TextUtils.isEmpty(s)) {
                    try {
                        JSONObject bundle = new JSONObject(s);
                        switch (bundle.optString("clickActionType")) {
                            case "call":
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                callIntent.setData(Uri.parse("tel:" + bundle.getString("clickActionUrl")));//change the number
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    Toast.makeText(context, "Please allow Call permission", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                context.startActivity(callIntent);
                                break;
                            case "weburl":
                                String url = bundle.getString("clickActionUrl");
                                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                    url = "http://" + url;
                                }
                                Intent sharingIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(sharingIntent);
                                break;
                            case "share":
                                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TITLE, bundle.getString("title"));
                                sendIntent.putExtra(Intent.EXTRA_TEXT, bundle.getString("clickActionUrl"));
                                sendIntent.setType("text/plain");
                                sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                Intent chooseIntent = Intent.createChooser(sendIntent, "Share");
                                chooseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(chooseIntent);
                                break;
                            default:
                                break;
                        }

                        SharedPref.getInstance().setSharedValue(context, "actionData", "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private final Runnable lessThen10SecTimer = new Runnable() {
        @Override
        public void run() {
            try {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("Fragment Handler", "5");

                        if (!Util.isAppIsInBackground(context))
                            AppRuleListener.getInstance().processSessionRules(context, "Less than 10 secs");

                        setLastVisit(context, "5");
                    }
                });
            } catch (Exception e) {

            }
        }
    };
    private final Runnable moreThen10SecTimer = new Runnable() {
        @Override
        public void run() {
            try {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(" Fragment Handler", "15");
                    }
                });
                if (!Util.isAppIsInBackground(context))
                    AppRuleListener.getInstance().processSessionRules(context, "11 secs to 30 secs");

            } catch (Exception e) {

            }

        }
    };
    private final Runnable moreThen30SecTimer = new Runnable() {
        @Override
        public void run() {
            try {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(" Fragment Handler", "30");
                    }
                });
                if (!Util.isAppIsInBackground(context))
                    AppRuleListener.getInstance().processSessionRules(context, "31 secs to 60 secs");


            } catch (Exception e) {

            }
        }
    };
    private final Runnable moreThen60SecTimer = new Runnable() {
        @Override
        public void run() {
            try {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(" Fragment Handler", "60");
                    }
                });
                if (!Util.isAppIsInBackground(context))
                    AppRuleListener.getInstance().processSessionRules(context, "More than 60 secs");
                try {
                   /* int count = new DataBase(context).getData(DataBase.Table.SCREENS_TABLE).size();
                    if (count >= 3) {*/
                    new OfflineScreenTrack(context, DataNetworkHandler.getInstance()).execute();
                    // }
                } catch (Exception f) {
                    f.printStackTrace();
                }

            } catch (Exception e) {

            }
        }
    };

    public static SessionTimer getInstance() {

        if (sessionTimer == null) {
            sessionTimer = new SessionTimer();
        }
        return sessionTimer;
    }

    void startTimer(Activity context) {
        try {
            this.context = context;
            timer();
        } catch (Exception e) {

        }

    }

    void stopTimer() {
        try {
            lessThen3Sec.removeCallbacks(lessThen3SecTimer);
            lessThen10Sec.removeCallbacks(lessThen10SecTimer);
            moreThen10Sec.removeCallbacks(moreThen10SecTimer);
            moreThen30Sec.removeCallbacks(moreThen30SecTimer);
            moreThen60Sec.removeCallbacks(moreThen60SecTimer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void timer() {

        try {
            pageStartTime = Util.getCurrentUTC();
            lessThen3Sec.removeCallbacks(lessThen3SecTimer);
            lessThen10Sec.removeCallbacks(lessThen10SecTimer);
            moreThen10Sec.removeCallbacks(moreThen10SecTimer);
            moreThen30Sec.removeCallbacks(moreThen30SecTimer);
            moreThen60Sec.removeCallbacks(moreThen60SecTimer);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            sessionRules = new ArrayList<>();
            lessThen3Sec.postDelayed(lessThen3SecTimer, 1000);
            lessThen10Sec.postDelayed(lessThen10SecTimer, 7000);
            moreThen10Sec.postDelayed(moreThen10SecTimer, 15000);
            moreThen30Sec.postDelayed(moreThen30SecTimer, 33000);
            moreThen60Sec.postDelayed(moreThen60SecTimer, 61000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setLastVisit(Context activity, String secs) {
       /* if (CRESUME_JOURNEY) {
            try {
                Log.e("setLastVisit", "" + activity);
                String val = SharedPref.getInstance().getStringValue(activity, AppConstants.resulticksSharedUserId);
                String deviceAppMapID = SharedPref.getInstance().getStringValue(activity, "deviceAppMapID");
                String passportID = SharedPref.getInstance().getStringValue(activity, AppConstants.PASSPORT_ID);
                JSONObject jsonObject = new JSONObject();

                if (TextUtils.isEmpty(passportID))
                    passportID = Util.getDeviceId(activity);

                if (TextUtils.isEmpty(deviceAppMapID))
                    deviceAppMapID = SharedPref.getInstance().getStringValue(context, AppConstants.resulticksSharedAPIKey);

                jsonObject.put("id", deviceAppMapID + passportID);
                jsonObject.put("source", "mobile");
                jsonObject.put("screen", activity.getClass().getSimpleName());
                JSONObject data = new JSONObject();
                data.put("fragmentName", CURRENT_FRAGMENT_NAME);
                jsonObject.put("data", data);
                if (!TextUtils.isEmpty(passportID))
                    jsonObject.put("attributes", getDeviceData(activity, secs, passportID));

                new DataNetworkHandler().apiCallSetLastVisit(activity, null, jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }


    JSONObject getDeviceData(Context context, String secs, String passportId) {
        try {
            int visitorCount = SharedPref.getInstance().getIntValue(context, "visitorCount");
            if (AppConstants.deviceDetails == null) {
                MDeviceData deviceData = new MDeviceData(context);
                AppConstants.deviceDetails = new JSONObject();
                AppConstants.deviceDetails.put("passportId", passportId);
                AppConstants.deviceDetails.put("deviceId", deviceData.getDeviceId());
                AppConstants.deviceDetails.put("deviceType", deviceData.getDeviceType());
                AppConstants.deviceDetails.put("deviceModel", deviceData.getDeviceModel());
                AppConstants.deviceDetails.put("manufacture", deviceData.getDeviceManufacture());
                AppConstants.deviceDetails.put("networkProvider", Util.getMobileNetworkOperator(context));
                AppConstants.deviceDetails.put("language", Locale.getDefault().getDisplayLanguage());
                AppConstants.deviceDetails.put("timezone", Util.getTimezone());
                AppConstants.deviceDetails.put("screenResolution", Util.getScreenResolution(context));
                AppConstants.deviceDetails.put("appInstallDate", Util.getAppFirstInstallTime(context));
                AppConstants.deviceDetails.put("appUpdatedDate", Util.getAppUpdateTime(context));
                AppConstants.deviceDetails.put("appVersionCode", deviceData.getAppVersionCode());
                AppConstants.deviceDetails.put("osType", deviceData.getDeviceOs());
                AppConstants.deviceDetails.put("endPoint", "Mobile");
                AppConstants.deviceDetails.put("version", deviceData.getDeviceOsVersion());
                AppConstants.deviceDetails.put("deviceModel", deviceData.getDeviceModel());
                AppConstants.deviceDetails.put("ipAddress", Util.getIpAddress(context));
            }

            if (visitorCount == 1)
                AppConstants.deviceDetails.put("visitorsType", "New visitor");
            else
                AppConstants.deviceDetails.put("visitorsType", "Returning visitor");

            String userID = SharedPref.getInstance().getStringValue(context, AppConstants.PASSPORT_ID);

            if (userID == null || TextUtils.isEmpty(userID) || userID.contains("T_")) {
                if (visitorCount == 1)
                    AppConstants.deviceDetails.put("userType", "UnKnown");
                else
                    AppConstants.deviceDetails.put("userType", "Known");

            } else {
                AppConstants.deviceDetails.put("userType", "Identified");
            }

            AppConstants.deviceDetails.put("sessionId", AppConstants.sessionId);
            AppConstants.deviceDetails.put("latitude", SharedPref.getInstance().getStringValue(context, "latitude"));
            AppConstants.deviceDetails.put("longitude", SharedPref.getInstance().getStringValue(context, "longitude"));
            AppConstants.deviceDetails.put("lastTimeSpent", SharedPref.getInstance().getStringValue(context, "lastTimeSpent"));
            AppConstants.deviceDetails.put("lastSessionTime", SharedPref.getInstance().getStringValue(context, "lastSessionTime"));

            Long sessionTime = Long.parseLong(Util.appSession(Util.getStringToUTC(appOpenTime), Util.getStringToUTC(Util.getCurrentUTC())));
            Long screenTime = Long.parseLong(Util.appSession(Util.getStringToUTC(pageStartTime), Util.getStringToUTC(Util.getCurrentUTC())));
            SharedPref.getInstance().setSharedValue(context, "lastTimeSpent", "" + screenTime);

            AppConstants.deviceDetails.put("isNotificationEnabled", NotificationManagerCompat.from(context).areNotificationsEnabled());
            AppConstants.deviceDetails.put("screenViews", AppConstants.screenViews);
            AppConstants.deviceDetails.put("activityName", context.getClass().getSimpleName());
            AppConstants.deviceDetails.put("fragmentName", CURRENT_FRAGMENT_NAME);
            AppConstants.deviceDetails.put("lastVisitedActivityName", AppConstants.LAST_ACTIVITY_NAME);
            AppConstants.deviceDetails.put("lastVisitedFragmentName", AppConstants.LAST_FRAGMENT_NAME);
            AppConstants.deviceDetails.put("advertiserId", SharedPref.getInstance().getStringValue(context, "AdvertiserID"));
            AppConstants.deviceDetails.put("token", SharedPref.getInstance().getStringValue(context, "token"));
            AppConstants.deviceDetails.put("appOpenedDate", Util.getStringToUTC(appOpenTime));
            AppConstants.deviceDetails.put("appClosedDate", Util.getStringToUTC(Util.getCurrentUTC()));
            AppConstants.deviceDetails.put("timeSpentOnScreen", screenTime);
            AppConstants.deviceDetails.put("screenViews", AppConstants.screenViews);
            AppConstants.deviceDetails.put("sessionDuration", sessionTime);
            AppConstants.deviceDetails.put("Login", isLogin);
            AppConstants.deviceDetails.put(AppConstants.APP_ID, SharedPref.getInstance().getStringValue(context, AppConstants.reSharedAPIKey));
            AppConstants.deviceDetails.put(AppConstants.TENANT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_ID));
            AppConstants.deviceDetails.put(AppConstants.BUSINESS_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.BUSINESS_SHORT_CODE));
            AppConstants.deviceDetails.put(AppConstants.DEPARTMENT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.DEPARTMENT_ID));
            AppConstants.deviceDetails.put(AppConstants.TENANT_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_SHORT_CODE));
            AppConstants.deviceDetails.put(AppConstants.PASSPORT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.PASSPORT_ID));
            AppConstants.deviceDetails.put(AppConstants.CAMPAIGN_NAME, SharedPref.getInstance().getStringValue(context, AppConstants.CAMPAIGN_NAME));
            AppConstants.deviceDetails.put(AppConstants.CAMPAIGN_REFERRAL, SharedPref.getInstance().getStringValue(context, AppConstants.CAMPAIGN_REFERRAL));
            AppConstants.deviceDetails.put(AppConstants.CAMPAIGN_ID, SharedPref.getInstance().getStringValue(context, AppConstants.CAMPAIGN_ID));
            AppConstants.deviceDetails.put(AppConstants.CAMPAIGN_CHANNEL, SharedPref.getInstance().getStringValue(context, AppConstants.CAMPAIGN_CHANNEL));
            AppConstants.deviceDetails.put(AppConstants.CITY, SharedPref.getInstance().getStringValue(context, "CurrentCityName"));
            AppConstants.deviceDetails.put(AppConstants.STATE, SharedPref.getInstance().getStringValue(context, "CurrentStateName"));
            AppConstants.deviceDetails.put(AppConstants.COUNTRY, SharedPref.getInstance().getStringValue(context, "CurrentCountryName"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return AppConstants.deviceDetails;
    }


}
