package io.mob.resu.reandroidsdk;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import io.flutter.Log;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.mob.resu.reandroidsdk.error.ExceptionTracker;

public class ResulticksChannel {
    private static final String CHANNEL = "resulticks_channel";
    private HashMap params;
    private static Context context;
    private static Activity activity;
    String OldScreenName = null;
    String newScreenName = null;
    private Calendar oldCalendar = Calendar.getInstance();
    private Calendar sCalendar = Calendar.getInstance();

    public void configureFlutterEngine(@NonNull final FlutterEngine flutterEngine, final Context context, Activity flutterActivity) {
        activity = flutterActivity;
        this.context = context;
        ReAndroidSDK.getInstance(this.context).getCampaignData(new IDeepLinkInterface() {
            @Override
            public void onInstallDataReceived(String s) {

                Log.e("OnInstall Data Received",s);
                MethodChannel channel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(),CHANNEL);
                channel.invokeMethod("didReceiveSmartLink",s);
            }

            @Override
            public void onDeepLinkData(String s) {

                MethodChannel channel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(),CHANNEL);
                channel.invokeMethod("didReceiveSmartLink",s);
            }
        });

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(),CHANNEL).setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @Override
            public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
                switch (call.method) {
                    case "sdkRegistration" :
                        if(call.arguments != null) {
                            params = (HashMap) call.arguments;
                        }
                        userRegisterParam(params);
                        break;
                    case "customEvent":
                        if(call.arguments != null) {
                            params = (HashMap) call.arguments;
                        }
                        customEvent(params);
                        break;
                    case "locationUpdate":
                        if(call.arguments != null) {
                            params = (HashMap) call.arguments;
                        }
                        locationUpdate(params);
                        break;
                    case "getNotificationList":
                        ArrayList notificationList = notificationList();
                        Log.e("Android Notification List",notificationList.toString());
                        Gson gson = new Gson();
                        String jsonString = gson.toJson(notificationList);
                        result.success(jsonString);
                        break;
                    case "deleteNotification":
                        if(call.arguments != null) {
                            params = (HashMap) call.arguments;
                        }
                        deleteNotificationByObj(params);
                        break;
                    case "deleteNotificationByNotificationId":
                        String notificationId = (String) call.arguments;
                        ReAndroidSDK.getInstance(context).deleteNotificationByNotificationId(notificationId);
                        break;
                    case "deleteNotificationByCampaignId":
                        String campaignId = (String) call.arguments;
                        ReAndroidSDK.getInstance(context).deleteNotificationByCampaignId(campaignId);
                        break;
                    case "getUnReadNotificationCount":
                        result.success(ReAndroidSDK.getInstance(context).getUnReadNotificationCount());
                        break;
                    case "getReadNotificationCount":
                        result.success(ReAndroidSDK.getInstance(context).getReadNotificationCount());
                        break;
                    case "readNotification":
                        String rCampaignId = (String) call.arguments;
                        ReAndroidSDK.getInstance(context).readNotification(rCampaignId);
                        break;
                    case "unReadNotification":
                        String unCampaignId = (String) call.arguments;
                        ReAndroidSDK.getInstance(context).unReadNotification(unCampaignId);
                        break;
                    case "notificationCTAClicked":
                        if(call.arguments != null) {
                            params = (HashMap) call.arguments;
                        }
                        notificationCTAClicked(params);
                        break;
                    case "appConversion":
                        appConversation();
                        break;
                    case "formDataCapture":
                        if(call.arguments != null) {
                            params = (HashMap) call.arguments;
                        }
                        formDataCapture(params);
                        break;
                    case "screenTracking":
                        String screenName = (String) call.arguments;
                        screenTracking(screenName);
                        OldScreenName = newScreenName;
                        newScreenName = screenName;
                        break;
                }
            }
        });

//        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(),CHANNEL).setMethodCallHandler((call, result) -> {
//
//
//        });
    }
    private void screenTracking(String screenName) {

        try {

            if (sCalendar == null)
                sCalendar = Calendar.getInstance();

            oldCalendar = sCalendar;
            sCalendar = Calendar.getInstance();

            if (OldScreenName != null) {
                AppLifecyclePresenter.getInstance().onSessionStop(activity, oldCalendar, sCalendar, OldScreenName, null, null);
                AppLifecyclePresenter.getInstance().onSessionStartFragment(activity, OldScreenName, null);
            }
            if (newScreenName == null)
                newScreenName = screenName;

        } catch (Exception e) {
            io.mob.resu.reandroidsdk.error.Log.e("screenTracking Exception: ", "" + e.getMessage());

        }
    }
    void userRegisterParam(HashMap param){
        Gson gson = new Gson();
        MRegisterUser regUser = new MRegisterUser();
        if(param.containsKey("token"))
            regUser.setDeviceToken((String) param.get("token"));
        if(param.containsKey("name"))
            regUser.setName((String) param.get("name"));
        if(param.containsKey("email"))
            regUser.setEmail((String) param.get("email"));
        if(param.containsKey("phone"))
            regUser.setPhone((String) param.get("phone"));
        if(param.containsKey("profileUrl"))
            regUser.setProfileUrl((String) param.get("profileUrl"));
        if(param.containsKey("age"))
            regUser.setAge((String) param.get("age"));
        if(param.containsKey("gender"))
            regUser.setGender((String) param.get("gender"));
        if(param.containsKey("userUniqueId"))
            regUser.setUserUniqueId((String) param.get("userUniqueId"));
        if(param.containsKey("dob"))
            regUser.setDob((String) param.get("dob"));
        if(param.containsKey("education"))
            regUser.setEducation((String) param.get("education"));
        if(param.containsKey("married"))
            regUser.setMarried((Boolean) param.get("married"));
        if(param.containsKey("employed"))
            regUser.setEmployed((Boolean) param.get("employed"));

        ReAndroidSDK.getInstance(this.context).onDeviceUserRegister(regUser);

    }
    void customEvent(HashMap params){
        if(params.containsKey("name")){
            String eventName = (String) params.get("name");
            if(params.containsKey("data")) {
                HashMap data = (HashMap) params.get("data");
                ReAndroidSDK.getInstance(this.context).onTrackEvent(data,eventName);
                return;
            }
            ReAndroidSDK.getInstance(this.context).onTrackEvent(eventName);
        }
    }
    void locationUpdate(HashMap params) {
        double lat = 0;
        double longitude = 0;
        if(params.containsKey("lat") && params.containsKey("long")){
            lat = (double) params.get("lat");
            longitude = (double) params.get("long");
        }
        else if(params.containsKey("latitude") && params.containsKey("longitude")) {
            lat = (double) params.get("latitude");
            longitude = (double) params.get("longitude");
        }
        if(lat != 0 && longitude != 0) {
            ReAndroidSDK.getInstance(this.context).onLocationUpdate(lat,longitude);
        }
    }
    ArrayList notificationList() {
        try {
            ArrayList notificationList = new ArrayList();
            ArrayList<RNotification> list = ReAndroidSDK.getInstance(this.context).getNotifications();
            return list;
        } catch (Exception var2) {
            ExceptionTracker.track(var2);
            return new ArrayList();
        }
    }
    void deleteNotificationByObj(HashMap params){
        if(!params.isEmpty()){
            Gson gson = new Gson();
            JSONObject json = new JSONObject(params);
            Log.e("Delete JSON",json.toString());
            ReAndroidSDK.getInstance(this.context).deleteNotificationByObject(json);
        }
    }

    void notificationCTAClicked(HashMap params){
        if(params.containsKey("campaignId") && params.containsKey("actionId")){
            String campaignId = (String) params.get("campaignId");
            String actionId = (String) params.get("actionId");
            ReAndroidSDK.getInstance(this.context).notificationCTAClicked(campaignId,actionId);
        }
    }
    void appConversation(){
        ReAndroidSDK.getInstance(this.context).appConversionTracking();
    }
    void formDataCapture(HashMap params) {
        if(params.containsKey("formid") && params.containsKey("apikey")){
            ReAndroidSDK.getInstance(this.context).formDataCapture(params);
        }

    }
}
