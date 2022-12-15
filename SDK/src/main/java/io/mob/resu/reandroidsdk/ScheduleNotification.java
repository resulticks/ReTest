package io.mob.resu.reandroidsdk;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Base64;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;
import io.mob.resu.reandroidsdk.error.Log;
import okhttp3.OkHttpClient;

import static io.mob.resu.reandroidsdk.AppConstants.baseUrl;


public class ScheduleNotification extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        try {
            if (intent.hasExtra("title")) {
                ReAndroidSDK.getInstance(context).onReceivedCampaign(intent.getExtras());
            } else {
                //schedulePushAmplication(context, intent.getExtras());

            }
        } catch (Exception e) {

        }
    }

    public void schedulePushAmplication(Context context, Bundle bundle) {
        try {
            /*int duration = 5;
            if (bundle.containsKey("timeDelay")) {
                if (Util.isAppIsInBackground(context))
                    duration = bundle.getInt("timeDelay", 15);
                else
                    duration = bundle.getInt("timeDelay", 3);
                Intent notificationIntent = new Intent(context, ScheduleNotification.class);
                long delay = 0;
                delay = TimeUnit.MINUTES.toMillis(duration);
                notificationIntent.putExtras(bundle);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1000, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                Long futureInMillis = SystemClock.elapsedRealtime() + delay;
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
                }
            }
            new DataNetworkHandler().apiCallGetPushAmplification(context);*/

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



}
