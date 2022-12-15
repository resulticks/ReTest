package io.mob.resu.reandroidsdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.mob.resu.reandroidsdk.error.Log;

public class ReNotificationBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (action.equals("resu.io.NOTIFICATION")) {
                Log.e("New Notification : ", "Received");
                ReAndroidSDK.getInstance(context).onReceivedCampaign(intent.getExtras());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
