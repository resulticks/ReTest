package io.mob.resu.reandroidsdk;

import android.content.BroadcastReceiver;
import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;

public interface APIInterface {

    void apiScreenTracking(Context context, String ScreenTracking, ArrayList<MData> dbScreen);

    void apiEventTracking(Context context, String EventTracking, ArrayList<MData> dbEvents);

    void apiCallCampaignTracking(Context context, String id, String campaignTracking, ArrayList<MData> dbCampaign);

    void apiCallCampaignTracking(Context context, BroadcastReceiver.PendingResult pendingResult, String id, String campaignTracking, ArrayList<MData> dbCampaign);

    void apiCallUpdateLocation(Context context, JSONObject jsonObject);

    void apiCallUpdateEvents(Context context, String event);

    void apiCallAPIKeyValidation(Context context, String deviceDetails);

    void apiCallTokenUpdate(Context context, String deviceDetails);

    void apiCallSDKRegistration(Context context, String userData);

    void apiCallNotificationAmplifier(Context context, String userData);

    void apiCallGetSDKRules(Context context);

    void apiFormDataCapture(Context context, JSONObject jsonObject);

    void apiCallAppConversionTracking(Context context);

    void apiCallAppConversionTracking(Context context, JSONObject data);

    void apiCallGetPushAmplification(Context context);

    void apiCallGetCapturedFields(Context context);

    void apiCallCampaignBlastAPI(Context context, JSONObject jsonObject, String blastId);

    void apiCallGetCarouselNotification(Context context, JSONObject jsonObject);

    void apiCallSmartLink(Context context, String url);

    void apiCallGetLastVisit(Context context, JSONObject jsonObject);

    void apiCallSetLastVisit(Context context, BroadcastReceiver.PendingResult pendingResult, JSONObject jsonObject);

    void apiGetConfig(Context context, JSONObject jsonObject);

    void apiBrandFormSubmission(Context context, JSONObject jsonObject);

    void apiGetAuth(Context context, JSONObject jsonObject);


}
