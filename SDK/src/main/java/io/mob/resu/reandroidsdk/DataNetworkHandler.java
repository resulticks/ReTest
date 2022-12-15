package io.mob.resu.reandroidsdk;

import static io.mob.resu.reandroidsdk.AppConstants.CAMPAIGN_BLAST_API;
import static io.mob.resu.reandroidsdk.AppConstants.CUSTOM_EVENT;
import static io.mob.resu.reandroidsdk.AppConstants.FORM_API;
import static io.mob.resu.reandroidsdk.AppConstants.FirstBaseUrl;
import static io.mob.resu.reandroidsdk.AppConstants.LOCATION_TRACKING;
import static io.mob.resu.reandroidsdk.AppConstants.SCREEN_TRACKING;
import static io.mob.resu.reandroidsdk.AppConstants.SDK_TOKEN_UPDATE;
import static io.mob.resu.reandroidsdk.AppConstants.baseUrl;
import static io.mob.resu.reandroidsdk.AppConstants.deviceId;
import static io.mob.resu.reandroidsdk.AppConstants.isLogin;
import static io.mob.resu.reandroidsdk.AppConstants.licence;
import static io.mob.resu.reandroidsdk.AppConstants.retryCount;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;
import io.mob.resu.reandroidsdk.error.Log;

class DataNetworkHandler implements IResponseListener, APIInterface {

    IDeepLinkInterface deepLinkInterface;
    String formId = "";
    IGetQRLinkDetail iGetQRLinkDetail;
    private Context context;
    private ArrayList<MData> dbScreen;
    private ArrayList<MData> dbCampaign;
    private ArrayList<MData> dbEvents;

    public static DataNetworkHandler getInstance() {
        return new DataNetworkHandler();
    }

    @Override
    public void apiBrandFormSubmission(Context context, JSONObject jsonObject) {
        try {
            this.context = context;
            formId = jsonObject.optString("formId");
            new DataExchanger(AppConstants.Brand_FORM_API, jsonObject.toString(), this, AppConstants.SDK_BRAND_OWN_FORM).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * API Server Communication
     */
    private void apiResponseHandler(String response, int flag, String campaignBlastId) {
        try {

            JSONObject jsonObject;
            switch (flag) {
                case AppConstants.SDK_API_KEY:
                case AppConstants.SDK_BRAND_OWN_FORM:
                case AppConstants.SDK_NOTIFICATION_VIEWED:
                case AppConstants.SDK_SCREEN_TACKING:
                case AppConstants.SDK_EVENTS:
                case AppConstants.SDK_USER_REGISTER:
                case SDK_TOKEN_UPDATE:
                    try {
                        new BackgroundProcess(response, flag).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, flag);
                    } catch (Exception e) {
                    }
                    break;
                case AppConstants.SDK_CAMPAIGN_DETAILS:
                    new DataClear(context, new ArrayList<MData>(), response, flag).execute();
                    break;
                case AppConstants.SDK_CAMPAIGN_DETAILS_USER:
                    if (deepLinkInterface != null) {
                        if (SharedPref.getInstance().getBooleanValue(context, AppConstants.reApiParamIsNewUser)) {
                            deepLinkInterface.onInstallDataReceived(deepLinkDataParse(response, true));
                        } else if (SharedPref.getInstance().getBooleanValue(context, AppConstants.reDeepLinkParamIsViaDeepLinkingLauncher)) {
                            deepLinkInterface.onDeepLinkData(deepLinkDataParse(response, false));
                        }
                        jsonObject = new JSONObject(response);
                        if (jsonObject.has("MobileFriendlyUrl")) {
                            SharedPref.getInstance().setSharedValue(context, AppConstants.reSharedMobileFriendlyUrl, jsonObject.optString("MobileFriendlyUrl"));
                        }
                    } else {
                        if (iGetQRLinkDetail != null) {
                            iGetQRLinkDetail.onSmartLinkDetails(smartLinkDataParse(response));
                        }
                    }
                    break;

                case AppConstants.SDK_NOTIFICATION_AMPLIFIER:
                    notificationAmplifier(context, response);
                    //insert(AppConstants.SDK_USER_REGISTER);
                    break;


                case AppConstants.SDK_RULES:
                    try {
                        if (!TextUtils.isEmpty(response)) {
                            try {
                                SharedPref.getInstance().setSharedValue(context, "localRules", response);
                                Log.e("Rules got ", "Updated");
                                AppRuleListener.getInstance().findingRules();
                                AppRuleListener.getInstance().processEntryRules(context);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case AppConstants.SDK_GET_FIELD_TRACK:
                    try {


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;


                case AppConstants.SDK_CAMPAIGN_BLAST:
                    // Log.e("CampaignBlastAPI Sucess", response);
                    SharedPref.getInstance().setSharedValue(context, campaignBlastId, campaignBlastId);
                    blastNotification(response);

                    break;
                case AppConstants.SDK_CAROUSEL:
                    //  Log.e("SDK_CAROUSEL Sucess", response);
                    blastCarouselNotification(response);

                    break;

                case AppConstants.SDK_FORM_DATA:

                    break;

                case AppConstants.SDK_GET_AUTH:
                    jsonObject = new JSONObject(response);
                    break;
                case AppConstants.SDK_GET_MOBILE_CONFIG:
                    try {
                        SharedPref.getInstance().setSharedValue(context, "mobileConfig", response);
                        new Util().setConfig(context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case AppConstants.SDK_GET_LAST_VISIT:
                    try {
                        SharedPref.getInstance().setSharedValue(context, AppConstants.reResumeJourney, response);
                        JSONObject jsonObject1 = new JSONObject(response);
                        AppConstants.resumeJourney = jsonObject1;

                        if (jsonObject1.has("data")) {
                            ReAndroidSDK.getInstance(context).onTrackEvent("Resume Journey");
                            Log.e("Event triggered", "Resume Journey" + !Util.isAppIsInBackground(context));
                        } else {
                            Log.e("Event Not triggered", "Resume Journey" + !Util.isAppIsInBackground(context));
                        }

                        /*if (ReAndroidSDK.journey != null) {
                            showResumeJourney((Activity) context, jsonObject1);
                        }*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void blastNotification(final String response) {

        final int SPLASH_TIME_OUT = 2000;
        try {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String notification = jsonObject.optString("payloadJsonData");
                        if (notification != null) {
                            jsonObject = new JSONObject(notification);
                            jsonObject = jsonObject.getJSONObject("data");
                            Bundle bundle = getBundle(jsonObject);
                            if (context != null) {
                                ReAndroidSDK.getInstance(context).onReceivedCampaign(bundle);
                                // new NotificationHelper(context).handleDataMessage(bundle);
                            }
                        }
                    } catch (Exception eq) {

                    }
                }
            }, SPLASH_TIME_OUT);


        } catch (Exception e) {

        }
    }

    private void blastCarouselNotification(String response) {
        try {

            Log.e("blastCarouselNotification", "" + response);

            JSONObject jsonObject = new JSONObject(response);
            jsonObject = jsonObject.getJSONObject("data");
            Bundle bundle = getBundle(jsonObject);
            if (context != null) {
                ReAndroidSDK.getInstance(context).onReceivedCampaign(bundle);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void storeContentInjection() {
        try {
            String webData = "";

            ArrayList<JSONObject> jsonObjects = new ArrayList<>();
            JSONArray jsonArray1 = new JSONArray(jsonObjects.toString());
            licence = true;
            //new DataBase(context).insertContentPublisherBulk(jsonArray1, DataBase.Table.REGISTER_VIEWS_TABLE);

        } catch (Exception e) {

        }
    }

    @Override
    public void apiCallUpdateLocation(Context context, JSONObject jsonObject) {
        this.context = context;
        try {
            if (LOCATION_TRACKING)
                new DataExchanger("locationUpdate", jsonObject.toString(), this, AppConstants.SDK_LOCATION_TACKING).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            else {
                Log.e("------********", "********--------");
                Log.e("Location Tracking", "Disabled");
                Log.e("------********", "********--------");

            }
        } catch (Exception e) {

        }

    }

    @Override
    public void apiCallUpdateEvents(Context context, String event) {
        this.context = context;
        try {
            if (CUSTOM_EVENT)
                new OfflineEventTrack(context, event, this).execute();
            else {
                Log.e("------********", "********--------");
                Log.e("Event Tracking", "Disabled");
                Log.e("------********", "********--------");
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void apiCallAPIKeyValidation(Context context, String deviceDetails) {
        try {
            this.context = context;
            new DataExchanger(FirstBaseUrl + "apiKeyValidation", deviceDetails, this, AppConstants.SDK_API_KEY).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @Override
    public void apiCallTokenUpdate(Context context, String deviceDetails) {
        try {
            this.context = context;
            JSONObject jsonObject = new JSONObject(deviceDetails);
            jsonObject.put(AppConstants.TENANT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_ID));
            jsonObject.put(AppConstants.BUSINESS_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.BUSINESS_SHORT_CODE));
            jsonObject.put(AppConstants.DEPARTMENT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.DEPARTMENT_ID));
            jsonObject.put(AppConstants.TENANT_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_SHORT_CODE));
            jsonObject.put(AppConstants.PASSPORT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.PASSPORT_ID));
            new DataExchanger(FirstBaseUrl + "mobileTokenUpdate", jsonObject.toString(), this, SDK_TOKEN_UPDATE).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void apiCallSDKRegistration(Context context, String userData) {
        this.context = context;
        try {
            isLogin = true;
            SharedPref.getInstance().setSharedValue(context, "sdkreg", userData);
            if (AppConstants.USER_REGISTER)
                new DataExchanger("sdkRegistration", userData, this, AppConstants.SDK_USER_REGISTER).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            else {
                Log.e("------********", "********--------");
                Log.e("User Register", "Disabled");
                Log.e("------********", "********--------");
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void apiCallNotificationAmplifier(Context context, String userData) {
        this.context = context;
        try {
            String apiMethodName = "notificationAmpilifierNew";
            if (baseUrl.contains("sdk.resu.io"))
                apiMethodName = "notificationAmbilifer";
            new DataExchanger(apiMethodName, userData, this, AppConstants.SDK_NOTIFICATION_AMPLIFIER).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        } catch (Exception e) {

        }
    }

    @Override
    public void apiCallAppConversionTracking(Context context) {
        try {
            this.context = context;
            if (!TextUtils.isEmpty(SharedPref.getInstance().getStringValue(context, AppConstants.reSharedMobileFriendlyUrl))) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("appId", SharedPref.getInstance().getStringValue(context, AppConstants.reSharedAPIKey));
                jsonObject.put("mobileFriendlyUrl", SharedPref.getInstance().getStringValue(context, AppConstants.reSharedMobileFriendlyUrl));
                jsonObject.put("userId", SharedPref.getInstance().getStringValue(context, AppConstants.reSharedUserId));
                jsonObject.put("deviceId", SharedPref.getInstance().getStringValue(context, AppConstants.reSharedDatabaseDeviceId));
                jsonObject.put(AppConstants.TENANT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_ID));
                jsonObject.put(AppConstants.BUSINESS_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.BUSINESS_SHORT_CODE));
                jsonObject.put(AppConstants.DEPARTMENT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.DEPARTMENT_ID));
                jsonObject.put(AppConstants.TENANT_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_SHORT_CODE));
                jsonObject.put(AppConstants.PASSPORT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.PASSPORT_ID));


                new DataExchanger("appConversionTracking", jsonObject.toString(), this, AppConstants.SDK_CAMPAIGN_CONVERSION).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            } else {
                Log.e("mobileFriendlyUrl", "Is empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void apiCallAppConversionTracking(Context context, JSONObject data) {
        try {
            if (!TextUtils.isEmpty(SharedPref.getInstance().getStringValue(context, AppConstants.reSharedMobileFriendlyUrl))) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data", data.toString());
                jsonObject.put("appId", SharedPref.getInstance().getStringValue(context, AppConstants.reSharedAPIKey));
                jsonObject.put("mobileFriendlyUrl", SharedPref.getInstance().getStringValue(context, AppConstants.reSharedMobileFriendlyUrl));
                jsonObject.put("userId", SharedPref.getInstance().getStringValue(context, AppConstants.reSharedUserId));
                jsonObject.put("deviceId", SharedPref.getInstance().getStringValue(context, AppConstants.reSharedDatabaseDeviceId));
                jsonObject.put(AppConstants.TENANT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_ID));
                jsonObject.put(AppConstants.BUSINESS_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.BUSINESS_SHORT_CODE));
                jsonObject.put(AppConstants.DEPARTMENT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.DEPARTMENT_ID));
                jsonObject.put(AppConstants.TENANT_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_SHORT_CODE));
                jsonObject.put(AppConstants.PASSPORT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.PASSPORT_ID));
                new DataExchanger("appConversionTracking", jsonObject.toString(), this, AppConstants.SDK_CAMPAIGN_CONVERSION).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            } else {
                Log.e("mobileFriendlyUrl", "Is empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void apiCallGetCapturedFields(Context context) {
        try {
            this.context = context;
            MDeviceData mDeviceData = new MDeviceData(context);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("appId", mDeviceData.getAppId());
            jsonObject.put(AppConstants.TENANT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_ID));
            jsonObject.put(AppConstants.BUSINESS_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.BUSINESS_SHORT_CODE));
            jsonObject.put(AppConstants.DEPARTMENT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.DEPARTMENT_ID));
            jsonObject.put(AppConstants.TENANT_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_SHORT_CODE));
            jsonObject.put(AppConstants.PASSPORT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.PASSPORT_ID));
            new DataExchanger("GetCapturedFields", jsonObject.toString(), this, AppConstants.SDK_GET_FIELD_TRACK).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void apiCallCampaignBlastAPI(Context context, JSONObject jsonObject, String blastId) {
        try {
            this.context = context;
            String val = SharedPref.getInstance().getStringValue(context, AppConstants.reSharedUserId).trim();
            String UserID = "";
            if (!TextUtils.isEmpty(val)) {
                UserID = new String(Base64.encode(val.getBytes(), Base64.DEFAULT));
                jsonObject.put("token", SharedPref.getInstance().getStringValue(context, "pushToken"));
                jsonObject.put("userid", UserID);
            } else {
                jsonObject.put("userid", "");
                jsonObject.put("token", "");
            }
            MDeviceData mDeviceData = new MDeviceData(context);
            jsonObject.put("appId", mDeviceData.getAppId());
            jsonObject.put("deviceId", mDeviceData.getDeviceId());
            jsonObject.put("osType", mDeviceData.getDeviceOs());
            jsonObject.put("sdkversion", AppConstants.SDK_VERSION);

            jsonObject.put(AppConstants.BUSINESS_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.BUSINESS_SHORT_CODE));
            jsonObject.put(AppConstants.DEPARTMENT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.DEPARTMENT_ID));
            jsonObject.put(AppConstants.TENANT_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_SHORT_CODE));
            jsonObject.put(AppConstants.PASSPORT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.PASSPORT_ID));

            new DataExchanger(CAMPAIGN_BLAST_API, jsonObject.toString(), this, AppConstants.SDK_CAMPAIGN_BLAST, blastId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void apiCallGetCarouselNotification(Context context, JSONObject jsonObject) {
        try {
            this.context = context;
            jsonObject.put(AppConstants.APP_ID, SharedPref.getInstance().getStringValue(context, AppConstants.reSharedAPIKey));
            jsonObject.put(AppConstants.TENANT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_ID));
            jsonObject.put(AppConstants.BUSINESS_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.BUSINESS_SHORT_CODE));
            jsonObject.put(AppConstants.DEPARTMENT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.DEPARTMENT_ID));
            jsonObject.put(AppConstants.TENANT_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_SHORT_CODE));
            jsonObject.put(AppConstants.PASSPORT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.PASSPORT_ID));
            String Url = FirstBaseUrl + jsonObject.getString("url");
            new DataExchanger(Url, this, AppConstants.SDK_CAROUSEL).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void apiCallGetSDKRules(Context context) {
        this.context = context;
        try {
            MDeviceData mDeviceData = new MDeviceData(context);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("appId", mDeviceData.getAppId());
            jsonObject.put(AppConstants.TENANT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_ID));
            jsonObject.put(AppConstants.BUSINESS_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.BUSINESS_SHORT_CODE));
            jsonObject.put(AppConstants.DEPARTMENT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.DEPARTMENT_ID));
            jsonObject.put(AppConstants.TENANT_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_SHORT_CODE));
            jsonObject.put(AppConstants.PASSPORT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.PASSPORT_ID));

            new DataExchanger("GetSDKRules", jsonObject.toString(), this, AppConstants.SDK_RULES).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void apiCallGetPushAmplification(Context context) {
        this.context = context;
        try {
            String jsonObject = Util.getNotificationAmplifier(context).toString();
            new DataExchanger("notificationAmpilifierNew", jsonObject, this, AppConstants.SDK_NOTIFICATION_AMPLIFIER).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        } catch (Exception e) {

        }
    }

    @Override
    public void apiFormDataCapture(Context context, JSONObject jsonObject) {
        this.context = context;
        if (jsonObject != null) {
            try {
                jsonObject.put(AppConstants.APP_ID, SharedPref.getInstance().getStringValue(context, AppConstants.reSharedAPIKey));
                jsonObject.put(AppConstants.TENANT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_ID));
                jsonObject.put(AppConstants.BUSINESS_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.BUSINESS_SHORT_CODE));
                jsonObject.put(AppConstants.DEPARTMENT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.DEPARTMENT_ID));
                jsonObject.put(AppConstants.TENANT_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_SHORT_CODE));
                jsonObject.put(AppConstants.PASSPORT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.PASSPORT_ID));
                new DataExchanger(FORM_API, jsonObject.toString(), this, AppConstants.SDK_FORM_DATA).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void apiScreenTracking(Context context, String screenTracking, ArrayList<MData> dbScreen) {
        this.context = context;
        this.dbScreen = dbScreen;
        if (screenTracking != null && SCREEN_TRACKING)
            new DataExchanger("screenTracking", screenTracking, this, AppConstants.SDK_SCREEN_TACKING).execute();
        else {
            Log.e("------********", "********--------");
            if (screenTracking == null) {
                Log.e("Screen Tracking ", "No values");
            } else {
                Log.e("Screen Tracking ", "Disabled");
            }
            Log.e("------********", "********--------");
        }
    }

    @Override
    public void apiEventTracking(Context context, String eventTracking, ArrayList<MData> dbEvents) {
        this.dbEvents = dbEvents;
        this.context = context;
        if (eventTracking != null && CUSTOM_EVENT)
            new DataExchanger("userEventTracking", eventTracking, DataNetworkHandler.this, AppConstants.SDK_EVENTS).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        else {
            Log.e("------********", "********--------");
            Log.e("Custom Event Tracking ", "Disabled");
            Log.e("------********", "********--------");
        }

    }

    @Override
    public void apiCallCampaignTracking(Context context, String id, String campaignObj, ArrayList<MData> dbCampaign) {
        try {
            this.dbCampaign = dbCampaign;
            this.context = context;

           /* if (TextUtils.isEmpty(id) || id.equalsIgnoreCase("null"))
                return;*/

            if (new Util().hasNetworkConnection(context)) {
                new DataExchanger("campaignTracking", campaignObj, this, AppConstants.SDK_NOTIFICATION_VIEWED).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            }

        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
    }

    @Override
    public void apiCallCampaignTracking(Context context, BroadcastReceiver.PendingResult pendingResult, String id, String campaignObj, ArrayList<MData> dbCampaign) {
        try {
            this.dbCampaign = dbCampaign;
            this.context = context;

           /* if (TextUtils.isEmpty(id) || id.equalsIgnoreCase("null"))
                return;*/

            if (new Util().hasNetworkConnection(context)) {
                new DataExchanger(pendingResult, "campaignTracking", campaignObj, this, AppConstants.SDK_NOTIFICATION_VIEWED).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            }
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
    }

    @Override
    public void apiCallGetLastVisit(Context context, JSONObject jsonObject) {
        this.context = context;
        new DataExchanger("GetLastVisit", jsonObject.toString(), this, AppConstants.SDK_GET_LAST_VISIT).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
    }

    @Override
    public void apiCallSetLastVisit(Context context, BroadcastReceiver.PendingResult pendingResult, JSONObject jsonObject) {
        this.context = context;
        new DataExchanger(pendingResult, "SetLastVisit", jsonObject.toString(), this, AppConstants.SDK_SET_LAST_VISIT).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
    }

    @Override
    public void apiGetConfig(Context context, JSONObject jsonObject) {
        this.context = context;
        new DataExchanger("getMobileConfig", jsonObject.toString(), this, AppConstants.SDK_GET_MOBILE_CONFIG).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
    }

    @Override
    public void apiGetAuth(Context context, JSONObject jsonObject) {
        this.context = context;
        new DataExchanger("getauth", jsonObject.toString(), this, AppConstants.SDK_GET_AUTH).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
    }

    @Override
    public void apiCallSmartLink(Context context, String url) {
        this.context = context;
        new DataExchanger(url).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
    }

    public void getCampaignDetails(Context context, IDeepLinkInterface deepLinkInterface, IGetQRLinkDetail iGetQRLinkDetail) {

        try {
            this.context = context;
            this.deepLinkInterface = deepLinkInterface;
            this.iGetQRLinkDetail = iGetQRLinkDetail;
            String id = SharedPref.getInstance().getStringValue(context, AppConstants.reSharedCampaignId);
            if (TextUtils.isEmpty(id) || id.equalsIgnoreCase("null"))
                return;
            if (new Util().hasNetworkConnection(context)) {
           /* if (!TextUtils.isEmpty(baseUrl) && baseUrl.equalsIgnoreCase("https://bizsdk.resulticks.net/Home/"))
                new DataExchanger("https://resu.io/GCM/GetSmartCodeDetail?smartCode=" + id, "", this, AppConstants.SDK_CAMPAIGN_DETAILS_USER).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            else if (!TextUtils.isEmpty(baseUrl) && baseUrl.equalsIgnoreCase("https://bizsdk.resulticks.net/Home/"))
                new DataExchanger(FirstBaseUrl + "getSmartCodeDetails?smartCode=" + id, "", this, AppConstants.SDK_CAMPAIGN_DETAILS_USER).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            else*/
                new DataExchanger("https://resu.io/GCM/GetSmartCodeDetail?smartCode=" + id, "", this, AppConstants.SDK_CAMPAIGN_DETAILS_USER).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void onSuccess(String response, int flag, String campaignBlastId) {
        apiResponseHandler(response, flag, campaignBlastId);
    }

    @Override
    public void onFailure(Throwable throwable, int flag) {
        try {
            onCheckOverflow(flag);
            onError(flag, "Something went wrong. Please try again");
            new BackgroundOverFlow(flag).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, flag);
        } catch (Exception e) {

        }

    }

    @Override
    public void showDialog(String response, int flag) {
        apiResponseHandler(response, flag, "");
    }

    @Override
    public void showErrorDialog(String errorResponse, int flag) {
        try {
            onCheckOverflow(flag);
            onError(flag, errorResponse);
            new BackgroundOverFlow(flag).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, flag);
        } catch (Exception e) {

        }

    }

    @Override
    public void showInternalServerErrorDialog(String errorResponse, int flag) {
        try {
            onCheckOverflow(flag);
            onError(flag, errorResponse);
            new BackgroundOverFlow(flag).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, flag);
        } catch (Exception e) {

        }

        Log.e("Internal server error", "" + flag);
    }

    @Override
    public void logOut(int flag) {
        try {
            onCheckOverflow(flag);
            onError(flag, "Something went wrong. Please try again");
            new BackgroundOverFlow(flag).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, flag);
        } catch (Exception e) {

        }

    }

    @Override
    public Context getContext() {
        return context;
    }

    private void onError(int flag, String message) {
        try {
            switch (flag) {
                case AppConstants.SDK_CAMPAIGN_DETAILS_USER:
                    if (iGetQRLinkDetail != null) {
                        iGetQRLinkDetail.onError(message);
                    }
                    break;

                case AppConstants.SDK_USER_REGISTER:
                    String val = SharedPref.getInstance().getStringValue(context, "sdkreg");
                    retryCount = retryCount + 1;
                    if (!TextUtils.isEmpty(val) && retryCount <= 3) {
                        apiCallSDKRegistration(context, val);
                    }
                    break;

            }
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
    }

    private void insert(int flag) {
        try {
            switch (flag) {
                case AppConstants.SDK_SCREEN_TACKING:
                    if (dbScreen.size() < 5) {
                        new DataBase(context).insertScreenDataBulk(dbScreen, DataBase.Table.SCREENS_TABLE);
                    }
                    break;
                case AppConstants.SDK_EVENTS:
                    try {
                        if (dbEvents.size() < 5) {
                            new DataBase(context).insertScreenDataBulk(dbEvents, DataBase.Table.EVENT_TABLE);
                        }
                    } catch (Exception e) {
                        ExceptionTracker.track(e);
                    }
                    break;
                case AppConstants.SDK_USER_REGISTER:
                    String val = SharedPref.getInstance().getStringValue(context, "sdkreg");
                    retryCount = retryCount + 1;
                    if (!TextUtils.isEmpty(val) && retryCount <= 3) {
                        apiCallSDKRegistration(context, val);
                    }
                    break;
                case AppConstants.SDK_NOTIFICATION_VIEWED:
                    try {
                        if (dbCampaign.size() < 5) {
                            new DataBase(context).insertScreenDataBulk(dbEvents, DataBase.Table.CAMPAIGN_TABLE);
                        }
                    } catch (Exception e) {
                        ExceptionTracker.track(e);
                    }
                    break;

            }
        } catch (Exception e) {

        }

    }

    private void onCheckOverflow(int flag) {

        try {
            switch (flag) {
                case AppConstants.SDK_NOTIFICATION_VIEWED:
                    if (dbCampaign.size() > 25) {
                        apiResponseHandler("", flag, "");
                    }
                    break;
                case AppConstants.SDK_SCREEN_TACKING:
                    if (dbScreen.size() > 25) {
                        apiResponseHandler("", flag, "");
                    }
                    break;
                case AppConstants.SDK_EVENTS:
                    if (dbEvents.size() > 25) {
                        apiResponseHandler("", flag, "");
                    }
                    break;

            }
        } catch (Exception e) {

        }
    }

    @NonNull
    private Bundle getBundle(JSONObject map) {
        Bundle intent = new Bundle();
        try {
            Iterator<String> parameters = map.keys();
            JSONObject jsonObject = new JSONObject();
            while (parameters.hasNext()) {
                String key = parameters.next();
                intent.putString("" + key, "" + map.getString(key));
                jsonObject.put("" + key, "" + map.getString(key));

            }
            Log.e("Notification data ", jsonObject.toString());

            return intent;
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
        return intent;

    }

    public void notificationAmplifier(Context context, String notification) {
        try {
            JSONObject object;
            Bundle bundle = new Bundle();
            object = new JSONObject(notification);
            JSONArray notifications = object.optJSONArray("notifications");
            ArrayList<Bundle> notificationBundle = new ArrayList<>();
            if (notifications != null && notifications.length() > 0) {
                for (int i = 0; i < notifications.length(); i++) {
                    JSONObject jsonObject = notifications.getJSONObject(i).getJSONObject("data");
                    if (jsonObject.has("resul")) {
                        String ps = jsonObject.optString("resul");
                        int length = ps.length();
                        ps = ps.substring(4, length);
                        Log.e("value1", ps);
                        byte[] tmp2 = Base64.decode(ps, Base64.DEFAULT);
                        String val2 = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            val2 = new String(tmp2, StandardCharsets.UTF_8);
                        }
                        Log.e("value", val2);
                        jsonObject = new JSONObject(val2);

                        Iterator<String> stringSet = jsonObject.keys();
                        while (stringSet.hasNext()) {
                            String key = stringSet.next();
                            String value = jsonObject.getString(key);
                            bundle.putString(key, value);
                        }
                    } else {
                        bundle = getBundle(jsonObject);
                    }

                   /* try {
                        new OfflineCampaignTrack(context, jsonObject.getString("id"), AppConstants.NOTIFICATION_RECEIVED, "Notification received", false, null, null, DataNetworkHandler.getInstance()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }*/
                    /*if (new DataBase(context).isNotificationExist(jsonObject.getString("id"), DataBase.Table.NOTIFICATION_TABLE))
                        ReAndroidSDK.getInstance(context).onReceivedCampaign(bundle);
*/
                    try {
                        notificationBundle.add(bundle);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                new DBCheck(notificationBundle).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            Log.e("Pending notification", e.getMessage());
        }
    }

    /**
     * Finding the devices
     *
     * @param context
     * @return
     */
    private String isTablet(Context context) {
        try {

            boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
            boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
            if (xlarge || large)
                return "Android Tab";
            else
                return "Android Phone";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Android Phone";
    }

    private String deepLinkDataParse(String response, boolean isNewInstall) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            //jsonObject= jsonObject.getJSONObject("smartUrlDetail");
            JSONObject object = new JSONObject();
            if (jsonObject.has("CampaignAppStoreUrl")) {
                JSONArray jsonArray = new JSONArray(jsonObject.getString("CampaignAppStoreUrl"));
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (isTablet(context).equalsIgnoreCase(jsonArray.getJSONObject(i).optString("PhoneType"))) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            object.put("activityName", jsonObject1.optString("AppScreen"));
                            object.put("fragmentName", jsonObject1.optString("Section"));
                            object.put("MobileFriendlyUrl", jsonObject1.optString("MobileFriendlyUrl"));
                            object.put("deepLink", true);
                            object.put("newInstall", isNewInstall);
                        }
                    }
                    object.put("customParams", jsonObject.optString("customParams"));
                    object.put("MobileFriendlyUrl", jsonObject.optString("MobileFriendlyUrl"));
                    SharedPref.getInstance().setSharedValue(context, AppConstants.reSharedReferral, object.toString());
                    return object.toString();
                }

            }
        } catch (Exception e) {
            Log.e("Deeplink Data Parse", e.getMessage());
        }

        return new JSONObject().toString();
    }

    private String smartLinkDataParse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject object = new JSONObject();
            if (jsonObject.has("CampaignAppStoreUrl")) {
                JSONArray jsonArray = new JSONArray(jsonObject.getString("CampaignAppStoreUrl"));
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (isTablet(context).equalsIgnoreCase(jsonArray.getJSONObject(i).optString("PhoneType"))) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            object.put("activityName", jsonObject1.optString("AppScreen"));
                            object.put("fragmentName", jsonObject1.optString("Section"));
                            object.put("MobileFriendlyUrl", jsonObject1.optString("MobileFriendlyUrl"));
                        }
                    }
                    object.put("MobileFriendlyUrl", jsonObject.optString("MobileFriendlyUrl"));
                    object.put("customParams", jsonObject.optString("customParams"));
                    SharedPref.getInstance().setSharedValue(context, AppConstants.reSharedReferral, object.toString());
                    Util.getCampaignDetails(context, jsonObject.optString("MobileFriendlyUrl"));
                    return object.toString();
                }
            }
        } catch (Exception e) {
            Log.e("Deeplink Data Parse", e.getMessage());
            iGetQRLinkDetail.onError("Something went wrong. Please try again!");
        }

        return new JSONObject().toString();
    }

    private class DBCheck extends AsyncTask<Boolean, Boolean, Boolean> {
        ArrayList<Bundle> bundles;
        ArrayList<Bundle> finalBundle = new ArrayList<>();
        String id;
        boolean isExist = false;

        DBCheck(ArrayList<Bundle> bundle) {
            this.bundles = bundle;
        }

        protected Boolean doInBackground(Boolean... urls) {
            try {
                finalBundle = new DataBase(context).isNotificationExist(bundles, DataBase.Table.NOTIFICATION_TABLE);
            } catch (Exception e) {
                isExist = false;
            }
            return isExist;
        }

        protected void onPostExecute(Boolean result) {
            /*try {
                if (finalBundle.size() > 0) {
                    for (Bundle b : finalBundle) {
                        b.putBoolean("isAMP",true);
                        ReAndroidSDK.getInstance(context).onReceivedCampaign(b);
                    }
                }
            } catch (Exception e) {

            }*/
            try {
                int arraySize = finalBundle.size();
                int count = 0;
                if (arraySize > 0) {
                    for (Bundle b : finalBundle) {
                        count = count + 1;
                        b.putBoolean("isAMP", true);
                        if (arraySize == count) {
                            b.putBoolean("isEND", true);
                        } else {
                            b.putBoolean("isEND", false);
                        }
                        ReAndroidSDK.getInstance(context).onReceivedCampaign(b);
                    }
                }
            } catch (Exception e) {

            }


        }
    }

    // Background Task
    private class BackgroundProcess extends AsyncTask<Integer, Integer, Integer> {

        String response = "{}";
        int flag;

        private BackgroundProcess(String response, int flag) {
            this.response = response;
            this.flag = flag;
        }


        @Override
        protected Integer doInBackground(Integer... strings) {
            try {
                JSONObject jsonObject;

                switch (flag) {
                    case AppConstants.SDK_NOTIFICATION_VIEWED:
                        if (context instanceof Activity)
                            AppLifecyclePresenter.getInstance().getCampaignData((Activity) context);
                        if (response.contains("true") || dbCampaign.size() > 25) {
                            new DataBase(context).deleteDataValue(dbCampaign, DataBase.Table.CAMPAIGN_TABLE);
                        }
                        break;
                    case AppConstants.SDK_SCREEN_TACKING:
                        try {
                            if (!response.contains("true")) {
                                insert(flag);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    case AppConstants.SDK_EVENTS:
                        try {
                            if (!response.contains("true")) {
                                insert(flag);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    case SDK_TOKEN_UPDATE:
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean("status")) {
                                Log.e("SDK_TOKEN_UPDATE", response);
                                SharedPref.getInstance().setSharedValue(context, "userToken", AppConstants.token);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;

                    case AppConstants.SDK_USER_REGISTER:
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean("status")) {
                                SharedPref.getInstance().setSharedValue(context, "sdkreg", "");
                                SharedPref.getInstance().setSharedValue(context, AppConstants.PASSPORT_ID, obj.optString(AppConstants.PASSPORT_ID));
                            } else {
                                insert(flag);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    case AppConstants.SDK_API_KEY:
                        try {
                            licence = true;
                            jsonObject = new JSONObject(response);
                            deviceId = Util.getDeviceId(context);
                            SharedPref.getInstance().setSharedValue(context, "deviceAppMapID", jsonObject.optString("deviceAppMapID", ""));
                            SharedPref.getInstance().setSharedValue(context, AppConstants.reSharedDatabaseDeviceId, deviceId);
                            SharedPref.getInstance().setSharedValue(context, "dynamicBaseUrl", jsonObject.optString("dynamicBaseUrl"));
                            SharedPref.getInstance().setSharedValue(context, "resulticksLicence", true);
                            SharedPref.getInstance().setSharedValue(context, AppConstants.TENANT_SHORT_CODE, jsonObject.optString(AppConstants.TENANT_SHORT_CODE));
                            SharedPref.getInstance().setSharedValue(context, AppConstants.BUSINESS_SHORT_CODE, jsonObject.optString(AppConstants.BUSINESS_SHORT_CODE));
                            SharedPref.getInstance().setSharedValue(context, AppConstants.TENANT_ID, jsonObject.optString(AppConstants.TENANT_ID));
                            SharedPref.getInstance().setSharedValue(context, AppConstants.DEPARTMENT_ID, jsonObject.optString(AppConstants.DEPARTMENT_ID));
                            //storeContentInjection();
                            jsonObject = new JSONObject(response);
                            new DataBase(context).deleteEventTable(DataBase.Table.REGISTER_EVENT_TABLE);
                            JSONArray jsonArray = jsonObject.getJSONArray("fieldCapture");
                            ArrayList<JSONObject> jsonObjects = new ArrayList<>();
                            JSONArray brandOwn = jsonObject.getJSONArray("brandFormFieldCapture");
                            try {
                                if (brandOwn != null) {
                                    for (int i = 0; i < brandOwn.length(); i++) {
                                        jsonObjects.add(brandOwn.getJSONObject(i));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                if (jsonArray != null) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        jsonObjects.add(jsonArray.getJSONObject(i));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            new DataBase(context).insertOrUpdateData(new JSONArray(jsonObjects));
                        } catch (Exception e) {

                        }
                        break;
                    case AppConstants.SDK_BRAND_OWN_FORM:
                        try {
                            new DataBase(context).deleteFormBrandFields(DataBase.Table.BRAND_OWN_FORM_TABLE, formId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            return 0;
        }

    }


    // Background Task
    private class BackgroundOverFlow extends AsyncTask<Integer, Integer, Integer> {
        int flag;

        private BackgroundOverFlow(int flag) {
            this.flag = flag;
        }


        @Override
        protected Integer doInBackground(Integer... strings) {
            try {
                insert(flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }

    }


}






