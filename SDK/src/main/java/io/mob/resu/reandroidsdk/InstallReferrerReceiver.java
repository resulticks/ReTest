package io.mob.resu.reandroidsdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;

import org.json.JSONObject;

import java.net.URLDecoder;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;


/**
 * App New Install captures
 */
public class InstallReferrerReceiver extends BroadcastReceiver implements InstallReferrerStateListener {
    protected static final String TAG = InstallReferrerReceiver.class.getSimpleName();
    private final JSONObject referrerObject = new JSONObject();
    private InstallReferrerClient referrerClient;
    private Context context ;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            this.context=context;
            Log.e("InstallReferrerReceiver", "" + intent.getStringExtra("referrer"));
            String rawReferrerString = intent.getStringExtra("referrer");
            if (rawReferrerString != null) {
                try {
                    referrerObject.put(AppConstants.reApiParamIsNewUser, true);
                    referrerObject.put(AppConstants.reDeepLinkParamIsViaDeepLinkingLauncher, true);
                    rawReferrerString = URLDecoder.decode(rawReferrerString, "UTF-8");
                    //HashMap<String, String> referrerMap = new HashMap<>();
                    String[] referralParams = rawReferrerString.split("&");
                    for (String referrerParam : referralParams) {
                        String[] keyValue = referrerParam.split("=");
                        if (keyValue.length > 1) {
                            //Log.e("key", URLDecoder.decode(keyValue[0], "UTF-8"));
                           // Log.e("value", URLDecoder.decode(keyValue[1], "UTF-8"));
                            referrerObject.put(URLDecoder.decode(keyValue[0], "UTF-8"), URLDecoder.decode(keyValue[1], "UTF-8"));
                        }
                    }
                    SharedPref.getInstance().setSharedValue(context, AppConstants.reApiParamIsNewUser, true);
                    if (referrerObject.has(AppConstants.reDeepLinkParamReferralId) || referrerObject.has(AppConstants.sdkDeepLinkParamReferralId)) {
                        String value = referrerObject.optString(AppConstants.reDeepLinkParamReferralId, "");
                        if (TextUtils.isEmpty(value)) {
                            value = referrerObject.optString(AppConstants.sdkDeepLinkParamReferralId, "");
                        }
                        SharedPref.getInstance().setSharedValue(context, AppConstants.reSharedCampaignId, value);

                        getSmartLinkDetails(context, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {

        }

        try {
            if (intent.getAction() != null) {
                if (intent.getAction().equals(Intent.ACTION_PACKAGE_FIRST_LAUNCH)) {
                    referrerClient = InstallReferrerClient.newBuilder(context).build();
                    referrerClient.startConnection(this);
                    Log.e("InstallReferrerReceiver", "" + "");

                    Log.e("InstallReferrerReceiver", "" + "");

                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    void getSmartLinkDetails(final Context activity, String smartLink) {
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
                new DataNetworkHandler().apiCallSmartLink(activity, smartLink);
            } catch (Exception e) {
                ExceptionTracker.track(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onInstallReferrerSetupFinished(int responseCode) {
        switch (responseCode) {
            case InstallReferrerClient.InstallReferrerResponse.OK:
                Log.d(TAG, "Referrer Response.OK");
                try {
                    ReferrerDetails response = referrerClient.getInstallReferrer();
                    String rawReferrerString = response.getInstallReferrer();
                    Log.d(TAG, "Referrer " + rawReferrerString);

                    if (rawReferrerString != null) {
                        try {
                            referrerObject.put(AppConstants.reApiParamIsNewUser, true);
                            referrerObject.put(AppConstants.reDeepLinkParamIsViaDeepLinkingLauncher, true);
                            rawReferrerString = URLDecoder.decode(rawReferrerString, "UTF-8");
                            String[] referralParams = rawReferrerString.split("&");
                            for (String referrerParam : referralParams) {
                                String[] keyValue = referrerParam.split("=");
                                if (keyValue.length > 1) {
                                   // Log.e("key", URLDecoder.decode(keyValue[0], "UTF-8"));
                                   // Log.e("value", URLDecoder.decode(keyValue[1], "UTF-8"));
                                    referrerObject.put(URLDecoder.decode(keyValue[0], "UTF-8"), URLDecoder.decode(keyValue[1], "UTF-8"));
                                }
                            }

                            SharedPref.getInstance().setSharedValue(context, AppConstants.reApiParamIsNewUser, true);

                            if (referrerObject.has(AppConstants.reDeepLinkParamReferralId) || referrerObject.has(AppConstants.sdkDeepLinkParamReferralId)) {
                                String value = referrerObject.optString(AppConstants.reDeepLinkParamReferralId, "");
                                if (TextUtils.isEmpty(value)) {
                                    value = referrerObject.optString(AppConstants.sdkDeepLinkParamReferralId, "");
                                }
                                SharedPref.getInstance().setSharedValue(context, AppConstants.reSharedCampaignId, value);

                                getSmartLinkDetails(context, value);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    referrerClient.endConnection();
                } catch (Exception e) {
                    Log.e(TAG, "" + e.getMessage());
                }
                break;
            case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                Log.w(TAG, "Referrer Response.FEATURE_NOT_SUPPORTED");
                break;
            case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                Log.w(TAG, "Referrer Response.SERVICE_UNAVAILABLE");
                break;
            case InstallReferrerClient.InstallReferrerResponse.SERVICE_DISCONNECTED:
                Log.w(TAG, "Referrer Response.SERVICE_DISCONNECTED");
                break;
            case InstallReferrerClient.InstallReferrerResponse.DEVELOPER_ERROR:
                Log.w(TAG, "Referrer Response.DEVELOPER_ERROR");
                break;
        }
    }

    @Override
    public void onInstallReferrerServiceDisconnected() {
        Log.w(TAG, "Referrer onInstallReferrerServiceDisconnected()");
    }
}



