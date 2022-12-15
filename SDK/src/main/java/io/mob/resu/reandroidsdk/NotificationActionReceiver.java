package io.mob.resu.reandroidsdk;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;
import io.mob.resu.reandroidsdk.error.Log;

import static io.mob.resu.reandroidsdk.Util.getLauncherActivityName;


public class NotificationActionReceiver extends BroadcastReceiver {

    int position;

    @Override
    public void onReceive(Context context, final Intent data) {
        try {
            if (data.getAction().equalsIgnoreCase("CarouselLeft")) {
                position = data.getIntExtra("position", 0);
                position = position - 1;
                ReRenterOtherPosition(context, data, position);
            } else if (data.getAction().equalsIgnoreCase("CarouselRight")) {
                position = data.getIntExtra("position", 0);
                position = position + 1;
                ReRenterOtherPosition(context, data, position);
            } else if (data.getAction().equalsIgnoreCase("notification_cancelled")) {
                new OfflineCampaignTrack(context, data.getExtras().getString(AppConstants.reApiParamId), "108", "notificationCancelled", false, null, null, DataNetworkHandler.getInstance()).execute();
                AppNotification.cancel(context, data.getExtras().getInt(AppConstants.reAppNotificationId));
            } else {
                Bundle bundles = data.getExtras();
                DismissWindow(context);
                String status = "";
                JSONArray finalCustomAction = new JSONArray(bundles.getString("customActions"));
                for (int i = 0; i < finalCustomAction.length(); i++) {
                    JSONObject jsonObject = finalCustomAction.getJSONObject(i);
                    if (jsonObject.getString("actionName").equalsIgnoreCase(bundles.getString("clickActionName"))) {
                        customActions(context, i, data);
                    }
                }
                AppNotification.cancel(context, data.getExtras().getInt(AppConstants.reAppNotificationId));
            }
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
    }

    private void ReRenterOtherPosition(Context context, Intent data, int position) {
        Intent intent;

        Bundle bundle = data.getExtras();
        try {
            intent = new Intent(context, Class.forName(data.getExtras().getString("navigationScreen").trim()));
        } catch (Exception e) {
            try {
                intent = new Intent(context, Class.forName(getLauncherActivityName(context)));
            } catch (Exception e1) {
                e1.printStackTrace();
                intent = new Intent();
            }
        }

        try {
            JSONArray jsonArray = new JSONArray(data.getStringExtra("carousel"));

            if (jsonArray.length() > position) {
                String imageUrl = jsonArray.getJSONObject(position).optString("url");
                bundle.putString("customActions",jsonArray.getJSONObject(position).optString("customActions"));
                intent.putExtras(bundle);
                if (!TextUtils.isEmpty(imageUrl)) {
                    new PictureStyleNotification(context, intent, imageUrl, position);
                } else {
                    new AppNotification().carouselNotification(context, intent, position, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void customActions(final Context context, final int action, final Intent intent) {
        try {
            Handler leftHandler = new Handler(Looper.getMainLooper());
            leftHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        new DataBase(context).markRead(intent.getExtras().getString("id"), DataBase.Table.NOTIFICATION_TABLE, true);
                        JSONArray finalCustomAction = new JSONArray(intent.getExtras().getString("customActions"));
                        Bundle bundle = intent.getExtras();
                        if (action < finalCustomAction.length()) {
                            JSONObject jsonObject = finalCustomAction.getJSONObject(action);
                            new OfflineCampaignTrack(context, intent.getExtras().getString(AppConstants.reApiParamId), jsonObject.optString("actionId"), jsonObject.optString("actionName"), false, null, null, DataNetworkHandler.getInstance()).execute();
                            switch (jsonObject.optString("actionType")) {
                                case "call":
                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    callIntent.setData(Uri.parse("tel:" + jsonObject.getString("url")));//change the number
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
                                case "maybelater":
                                    scheduleNotification(context, jsonObject.getString("duration"), intent.getExtras());
                                    break;
                                case "dismiss":
                                    break;
                                case "weburl":
                                    String url = jsonObject.getString("url");
                                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                        url = "http://" + url;
                                    }
                                    Intent sharingIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(sharingIntent);
                                    break;
                                case "share":
                                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TITLE, intent.getStringExtra("title"));
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, jsonObject.getString("url"));
                                    sendIntent.setType("text/plain");
                                    sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    Intent chooseIntent = Intent.createChooser(sendIntent, "Share");
                                    chooseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(chooseIntent);
                                    break;
                                case "smartlink":

                                    Intent intent1;
                                    try {
                                        intent1 = new Intent(context, Class.forName(jsonObject.getString("activityName").trim()));
                                    } catch (Exception e) {
                                        intent1 = new Intent(context, Class.forName(getLauncherActivityName(context)));
                                    }
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    bundle.putString("navigationScreen", jsonObject.getString("activityName"));
                                    bundle.putString("customParams", jsonObject.getString("customParams"));
                                    bundle.putString("category", jsonObject.getString("fragmentName"));
                                    bundle.putString("fragmentName", jsonObject.getString("fragmentName"));
                                    bundle.putString("MobileFriendlyUrl", jsonObject.getString("MobileFriendlyUrl"));
                                    intent1.putExtras(bundle);
                                    context.startActivity(intent1);
                                    break;
                                case "resumejourney":

                                    Intent intent2;
                                    try {
                                        intent2 = new Intent(context, Class.forName(intent.getExtras().getString("navigationScreen").trim()));
                                    } catch (Exception e) {
                                        intent2 = new Intent(context, Class.forName(getLauncherActivityName(context)));
                                    }
                                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    bundle.putString("resumeJourney", Util.getResumeJourneyData(context));
                                    intent2.putExtras(bundle);
                                    context.startActivity(intent2);
                                    break;




                                default:
                                    break;
                            }

                        }
                        AppNotification.cancel(context, intent.getExtras().getInt(AppConstants.reAppNotificationId));
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }


            });
        } catch (Exception e) {

        }


    }


    private void customAction(Context context, Intent intent) {

        try {
            Bundle bundle = intent.getExtras();
            String customAction = bundle.getString("customAction");
            JSONObject jsonObject = new JSONObject(customAction);
            Intent intent1;
            try {
                intent1 = new Intent(context, Class.forName(jsonObject.getString("activityName").trim()));
            } catch (Exception e) {
                intent1 = new Intent(context, Class.forName(getLauncherActivityName(context)));
            }
            bundle.putString("navigationScreen", jsonObject.getString("activityName"));
            bundle.putString("customParams", jsonObject.getString("customParams"));
            bundle.putString("category", jsonObject.getString("fragmentName"));
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtras(bundle);
            context.startActivity(intent1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void DismissWindow(Context context) {
        try {
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        } catch (Exception e) {
            Log.e("DismissWindow", "" + e.getMessage());
        }

    }

    public void scheduleNotification(Context context, String duration, Bundle bundle) {

        try {
            if (!duration.isEmpty()) {
                Intent notificationIntent = new Intent(context, ScheduleNotification.class);


                String interval;
                long delay = 0;

                if (duration.contains("Minute(s)")) {
                    interval = duration.replace("Minute(s)", "").trim();
                    delay = TimeUnit.MINUTES.toMillis(Integer.parseInt(interval));
                } else if (duration.contains("Hour(s)")) {
                    interval = duration.replace("Hour(s)", "").trim();
                    delay = TimeUnit.HOURS.toMillis(Integer.parseInt(interval));
                } else if (duration.contains("Day(s)")) {
                    interval = duration.replace("Day(s)", "").trim();
                    delay = TimeUnit.DAYS.toMillis(Integer.parseInt(interval));
                }

                Log.e("mili", "" + delay);
                // bundle.putString("duration", "");
                // bundle.putString("actionName", "");
                notificationIntent.putExtras(bundle);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, getRandom(), notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                Long futureInMillis = SystemClock.elapsedRealtime() + delay;
                Log.e("Time", "" + futureInMillis);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
                }
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


}
