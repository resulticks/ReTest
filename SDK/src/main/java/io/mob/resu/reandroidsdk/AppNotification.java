package io.mob.resu.reandroidsdk;

import static android.content.Context.NOTIFICATION_SERVICE;
import static io.mob.resu.reandroidsdk.Util.getLauncherActivityName;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;


/**
 * Helper class for showing and canceling new message
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */

public class AppNotification {

    /**
     * The unique identifier for this type of notification.
     */
    private static int NOTIFICATION_ID = 0;
    int htmlCode = HtmlCompat.FROM_HTML_MODE_LEGACY;
    /**
     * Campaign push notification
     *
     * @param context
     * @param title
     * @param text
     * @param intent
     * @param bitmap
     */
    private NotificationManager notifyManager;

    /**
     * Cancels any notifications of this type previously shown using
     */
    public static void cancel(final Context context, int id) {
        try {
            final NotificationManager nm = (NotificationManager) context
                    .getSystemService(NOTIFICATION_SERVICE);
            nm.cancel(id);
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
    }

    public static void cancelAll(final Context context) {
        try {
            final NotificationManager nm = (NotificationManager) context
                    .getSystemService(NOTIFICATION_SERVICE);
            nm.cancelAll();
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
    }

    private void notify(NotificationManager notificationManager, final Notification notification) {
        try {
            notificationManager.notify(NOTIFICATION_ID, notification);
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
    }

    /**
     * Notification Button Action provider
     *
     * @param context
     * @param bundle
     * @param icon
     * @param
     * @return
     */
    private NotificationCompat.Action getActionIntent(Context context, Bundle bundle, int icon, JSONObject actionObject) {
        try {

            String actionName = actionObject.optString("actionName");
            String actionId = actionObject.optString("actionId");
            String actionType = actionObject.optString("actionType");
            String actionUrl = actionObject.optString("url");
            Intent actionIntent = new Intent(context, NotificationActionReceiver.class);
            PendingIntent pendingIntent;

            boolean isActivity = false;
            switch (actionType) {
                case "call":
                case "weburl":
                case "share":
                    // Approach
                    actionIntent = new Intent(context, Class.forName(getLauncherActivityName(context)));
                    bundle.remove("navigationScreen");
                    bundle.remove("customParams");
                    bundle.remove("category");
                    bundle.remove("fragmentName");
                    bundle.remove("MobileFriendlyUrl");
                    isActivity = true;
                    break;
                case "maybelater":
                case "dismiss":
                    // Existing Appproach
                    isActivity = false;
                    break;

                case "smartlink":
                    // Approach
                    try {
                        actionIntent = new Intent(context, Class.forName(actionObject.getString("activityName").trim()));
                    } catch (Exception e) {
                        actionIntent = new Intent(context, Class.forName(getLauncherActivityName(context)));
                    }
                    bundle.putString("navigationScreen", actionObject.getString("activityName"));
                    bundle.putString("customParams", actionObject.getString("customParams"));
                    bundle.putString("category", actionObject.getString("fragmentName"));
                    bundle.putString("fragmentName", actionObject.getString("fragmentName"));
                    bundle.putString("MobileFriendlyUrl", actionObject.getString("MobileFriendlyUrl"));
                    isActivity = true;
                    break;
                case "resumejourney":
                    try {
                        actionIntent = new Intent(context, Class.forName(bundle.getString("navigationScreen").trim()));
                    } catch (Exception e) {
                        actionIntent = new Intent(context, Class.forName(getLauncherActivityName(context)));
                    }
                    bundle.putString("resumeJourney", Util.getResumeJourneyData(context));
                    isActivity = true;
                    break;

                default:
                    break;
            }
            bundle.putString("clickActionName", actionName);
            bundle.putString("clickActionId", actionId);
            bundle.putString("clickActionUrl", actionUrl);
            bundle.putString("clickActionType", actionType);
            actionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            actionIntent.setAction(actionName);
            actionIntent.putExtras(bundle);

            if (isActivity)
                pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, actionIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            else
                pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_ID, actionIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);


            return new NotificationCompat.Action.Builder(icon, actionName, pendingIntent).build();
        } catch (Exception e) {
            return null;
        }
    }

    private PendingIntent getPendingIntent(Context context, Bundle bundle, int icon, JSONObject actionObject) {
        try {

            String actionName = actionObject.optString("actionName");
            String actionId = actionObject.optString("actionId");
            String actionType = actionObject.optString("actionType");
            String actionUrl = actionObject.optString("url");
            Intent actionIntent = new Intent(context, NotificationActionReceiver.class);
            PendingIntent pendingIntent;

            boolean isActivity = false;
            switch (actionType) {
                case "call":
                case "weburl":
                case "share":
                    // Approach
                    actionIntent = new Intent(context, Class.forName(getLauncherActivityName(context)));
                    bundle.remove("navigationScreen");
                    bundle.remove("customParams");
                    bundle.remove("category");
                    bundle.remove("fragmentName");
                    bundle.remove("MobileFriendlyUrl");
                    isActivity = true;
                    break;
                case "maybelater":
                case "dismiss":
                    // Existing Appproach
                    isActivity = false;
                    break;

                case "smartlink":
                    // Approach
                    try {
                        actionIntent = new Intent(context, Class.forName(actionObject.getString("activityName").trim()));
                    } catch (Exception e) {
                        actionIntent = new Intent(context, Class.forName(getLauncherActivityName(context)));
                    }
                    bundle.putString("navigationScreen", actionObject.getString("activityName"));
                    bundle.putString("customParams", actionObject.getString("customParams"));
                    bundle.putString("category", actionObject.getString("fragmentName"));
                    bundle.putString("fragmentName", actionObject.getString("fragmentName"));
                    bundle.putString("MobileFriendlyUrl", actionObject.getString("MobileFriendlyUrl"));
                    isActivity = true;
                    break;
                case "resumejourney":
                    try {
                        actionIntent = new Intent(context, Class.forName(bundle.getString("navigationScreen").trim()));
                    } catch (Exception e) {
                        actionIntent = new Intent(context, Class.forName(getLauncherActivityName(context)));
                    }
                    bundle.putString("resumeJourney", Util.getResumeJourneyData(context));
                    isActivity = true;
                    break;

                default:
                    break;
            }
            bundle.putString("clickActionName", actionName);
            bundle.putString("clickActionId", actionId);
            bundle.putString("clickActionUrl", actionUrl);
            bundle.putString("clickActionType", actionType);
            actionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            actionIntent.setAction(actionName);
            actionIntent.putExtras(bundle);

            if (isActivity)
                pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, actionIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            else
                pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_ID, actionIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntent;
        } catch (Exception e) {
            return null;
        }
    }

    private PendingIntent getCTAIntent(Context context, Bundle bundle, String action, String actionName) {
        try {
            Intent actionIntent = new Intent(context, NotificationActionReceiver.class);
            actionIntent.setAction(action);
            bundle.putString("clickActionName", actionName);
            actionIntent.putExtras(bundle);
            return PendingIntent.getBroadcast(context, NOTIFICATION_ID, actionIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        } catch (Exception e) {
            return null;
        }

    }

    private void getAppIcon(Context context, NotificationCompat.Builder builder) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int icon = Util.getAppIcon(context, true);
                int iconColor = Util.getAppIconColor(context);
                if (icon != 0) {
                    builder.setSmallIcon(icon);
                }
                if (iconColor != 0) {
                    String val = "#" + Integer.toHexString(ContextCompat.getColor(context, iconColor));
                    builder.setColor(Color.parseColor(val));
                    builder.setColorized(true);
                }
            } else {
                int icon = Util.getAppIcon(context, false);
                if (icon != 0) {
                    builder.setSmallIcon(icon);
                }
            }
            // Large icon
            int icon = Util.getAppIcon(context, false);
            if (icon != 0) {
                Bitmap myLogo = BitmapFactory.decodeResource(context.getResources(), icon);
                builder.setLargeIcon(myLogo);
            }
        } catch (Exception e) {
            Log.e("getIcon", "" + e.getMessage());
        }
    }

    public int getResources() {

        return 0;
    }

    private String createNotificationChannels(Context context, String ringtone) {

        String name = "";
        try {
            name = "Push Notification Custom" + ringtone.toUpperCase();

        } catch (Exception e) {
            e.printStackTrace();
            name = "";
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int resID = context.getResources().getIdentifier(ringtone, "raw", context.getPackageName());
                String path = "android.resource://" +
                        context.getApplicationContext().getPackageName() +
                        "/" + resID;

                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel mChannel = mNotificationManager.getNotificationChannel(ringtone);

                if (mChannel == null) {


                    Uri soundUri = Uri.parse(path);
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build();


                    String description = ringtone.toUpperCase();
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel channel = new NotificationChannel(name, "Push Notification Custom", importance);
                    channel.setDescription(description);
                    channel.enableVibration(true);
                    channel.setSound(soundUri, audioAttributes);
                    channel.enableVibration(true);
                    channel.enableLights(true);
                    channel.setShowBadge(true);
                    channel.setLightColor(Color.BLUE);
                    channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    mNotificationManager.createNotificationChannel(channel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    private String createNotificationChannels(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        String channelName = "Push Notification";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel mChannel = mNotificationManager.getNotificationChannel(channelName);
                if (mChannel == null) {
                    String name = channelName;
                    String description = channelName;
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel channel = new NotificationChannel(channelName, "Push Notification", importance);
                    channel.setDescription(description);
                    channel.enableVibration(true);
                    channel.enableLights(true);
                    channel.setShowBadge(true);
                    channel.setLightColor(Color.BLUE);
                    channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

                    mNotificationManager.createNotificationChannel(channel);
                }
            }
        } catch (Exception e) {
        }
        return channelName;
    }

    protected PendingIntent notificationCancelled(Context mContext, Bundle bundle) {
        try {
            Intent intent = new Intent(mContext, NotificationActionReceiver.class);
            intent.setAction("notification_cancelled");
            intent.putExtras(bundle);
            return PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        } catch (Exception e) {
            return null;
        }


    }

    public void registerNormalNotificationChannel(Context context) {
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel channel_sound = null;
                channel_sound = new NotificationChannel("CHANNEL_ID_SOUND", "CHANNEL_NAME_ALL2", NotificationManager.IMPORTANCE_HIGH);
                channel_sound.enableVibration(true);
                notificationManager.createNotificationChannel(channel_sound);
            }

        } catch (Exception e) {

        }
    }

    /*public void showNotification(final Context context, Intent intent, Bitmap bitmap) {
        try {
            String sound = "";
            Bundle bundle = intent.getExtras();
            String channelId = "NOTIFICATION_DEFAULT_CHANNEL";
            NOTIFICATION_ID = intent.getExtras().getInt("notificationId");

            if (bundle == null)
                return;

            if (bundle.containsKey("isCustom")) {
                if (bundle.getString("isCustom").equalsIgnoreCase("true")) {
                    showNotificationCustomNotification(context, intent, bitmap);
                    return;
                }
            }

            if (bundle.containsKey("sound"))
                sound = bundle.getString("sound");

            if (!TextUtils.isEmpty(sound)) {
                channelId = createNotificationChannels(context, sound);
                // channelId = "NOTIFICATION_CUSTOM_CHANNEL_" + sound.toUpperCase();
            } else
                channelId = createNotificationChannels(context);

            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("body");

            if (TextUtils.isEmpty(title))
                title = "Media content received";

            if (TextUtils.isEmpty(text))
                text = "Tap to view";

            String subtitle = "";
            if (intent.hasExtra("subTitle")) {
                subtitle = intent.getStringExtra("subTitle");
            }

            int icon;
            icon = 0;
            NotificationCompat.Builder builder;
            if (notifyManager == null) {
                notifyManager =
                        (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new NotificationCompat.Builder(context, channelId);
            } else {
                builder = new NotificationCompat.Builder(context, "");
            }
            getAppIcon(context, builder);
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            builder.setNumber(0);
            JSONArray customAction = null;
            try {
                customAction = new JSONArray(intent.getExtras().getString("customActions"));
                if (customAction.length() > 0) {
                    for (int i = 0; i < customAction.length(); i++) {
                        JSONObject jsonObject = customAction.getJSONObject(i);
                        builder.addAction(getActionIntent(context, intent.getExtras(), icon, jsonObject));
                    }
                }
            } catch (Exception ignored) {
                io.mob.resu.reandroidsdk.error.Log.e("error", ignored.getMessage());
            }
            builder.setContentTitle(title);
            builder.setContentText(text);
            builder.setSubText(subtitle);

            if (bitmap != null) {
                builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap)
                        .setBigContentTitle(title)
                        .setSummaryText(text));
            } else {
                builder.setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(title)
                        .bigText(text));
            }
            builder.setAutoCancel(true);
            intent.putExtra("clickActionName", "Open");
            intent.putExtra("clickActionId", "2");
            builder.setContentIntent(
                    PendingIntent.getActivity(
                            context,
                            NOTIFICATION_ID,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));
            notify(notifyManager, builder.build());

        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
    }

    public void showNotificationCustomNotification(final Context context, Intent intent, Bitmap bitmap) {
        try {

            String sound = "";
            Bundle bundle = intent.getExtras();
            String channelId = "NOTIFICATION_DEFAULT_CHANNEL";

            if (bundle == null)
                return;
            if (bundle.containsKey("sound"))
                sound = bundle.getString("sound");

            if (!TextUtils.isEmpty(sound)) {
                channelId = createNotificationChannels(context, sound);
                //   channelId = "NOTIFICATION_CUSTOM_CHANNEL_" + sound.toUpperCase();
            } else
                channelId = createNotificationChannels(context);

            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("body");
            String subtitle = "";

            if (TextUtils.isEmpty(title))
                title = "Media content received";

            if (TextUtils.isEmpty(text))
                text = "Tap to view";

            if (intent.hasExtra("subTitle")) {
                subtitle = intent.getStringExtra("subTitle");
            }
            text = text + subtitle;

            try {
                NOTIFICATION_ID = intent.getExtras().getInt("notificationId");

                Bitmap appLogo = null;
                int icon = Util.getAppIcon(context, false);

                if (icon != 0) {
                    appLogo = BitmapFactory.decodeResource(context.getResources(), icon);
                }

                int titleColor = Color.parseColor(intent.getStringExtra("titleColor"));
                int contentBgColor = Color.parseColor(intent.getStringExtra("contentBgColor"));
                int bodyColor = Color.parseColor(intent.getStringExtra("bodyColor"));

                RemoteViews collapsedView;
                int version = Integer.parseInt(Build.VERSION.RELEASE);
                int targetSdkVersion = context.getApplicationContext().getApplicationInfo().targetSdkVersion;

                if (version < 12 || targetSdkVersion < 31) {
                    collapsedView = new RemoteViews(context.getPackageName(), R.layout.custom_collapse_view_low);
                    collapsedView.setInt(R.id.rParant, "setBackgroundColor", contentBgColor);

                } else {
                    collapsedView = new RemoteViews(context.getPackageName(), R.layout.custom_collapse_view);
                }
                collapsedView.setTextViewText(R.id.notificationTitle, HtmlCompat.fromHtml(title, htmlCode));
                collapsedView.setTextViewText(R.id.notificationText, HtmlCompat.fromHtml(text, htmlCode));
                collapsedView.setImageViewBitmap(R.id.notificationImage, appLogo);
                collapsedView.setTextColor(R.id.notificationTitle, titleColor);
                collapsedView.setTextColor(R.id.notificationText, bodyColor);

                Bitmap bigPicture = null;
                RemoteViews bigPictureView;


                if (version < 12 || targetSdkVersion < 31) {
                    bigPictureView = new RemoteViews(context.getPackageName(), R.layout.custom_expand_view_low);
                    bigPictureView.setInt(R.id.rParant, "setBackgroundColor", contentBgColor);

                } else {
                    bigPictureView = new RemoteViews(context.getPackageName(), R.layout.custom_expand_view);
                }

                bigPictureView.setTextViewText(R.id.notificationTitle, HtmlCompat.fromHtml(title, htmlCode));
                bigPictureView.setTextViewText(R.id.notificationText, HtmlCompat.fromHtml(text, htmlCode));
                bigPictureView.setImageViewBitmap(R.id.notificationImage, appLogo);
                bigPictureView.setTextColor(R.id.notificationTitle, titleColor);
                bigPictureView.setTextColor(R.id.notificationText, bodyColor);


                if (bitmap != null) {
                    bigPictureView.setInt(R.id.notificationText, "setMaxLines", 2);
                    bigPictureView.setViewVisibility(R.id.big_picture_imageview, View.VISIBLE);
                    bigPictureView.setImageViewBitmap(R.id.big_picture_imageview, bitmap);
                } else {
                    if (TextUtils.isEmpty(title)) {
                        bigPictureView.setViewVisibility(R.id.big_picture_imageview, View.GONE);
                        bigPictureView.setViewVisibility(R.id.push_collapsed, View.GONE);
                    }
                    bigPictureView.setInt(R.id.notificationText, "setMaxLines", 12);
                }

                NotificationCompat.Builder builder;
                if (notifyManager == null) {
                    notifyManager =
                            (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builder = new NotificationCompat.Builder(context, channelId);
                } else {
                    builder = new NotificationCompat.Builder(context, "");
                }
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setCustomContentView(collapsedView);
                builder.setCustomBigContentView(bigPictureView);

                builder.setAutoCancel(true);
                intent.putExtra("clickActionName", "Open");
                intent.putExtra("clickActionId", "2");

                //Default Click
                builder.setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                NOTIFICATION_ID,
                                intent,
                                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));

                // notification clear
                builder.setDeleteIntent(notificationCancelled(context, bundle));
                // App icon setup
                getAppIcon(context, builder);
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                builder.setNumber(0);

                JSONArray customAction = null;
                try {
                    customAction = new JSONArray(intent.getExtras().getString("customActions"));
                    if (customAction.length() > 0) {
                        for (int i = 0; i < customAction.length(); i++) {
                            JSONObject jsonObject = customAction.getJSONObject(i);
                            int actionId = -1;
                            switch (i) {
                                case 0:
                                    actionId = R.id.custom_action_one;
                                    break;
                                case 1:
                                    actionId = R.id.custom_action_two;
                                    break;
                                case 2:
                                    actionId = R.id.custom_action_three;
                                    break;
                            }
                            if (actionId != -1) {
                                bigPictureView.setViewVisibility(actionId, View.VISIBLE);
                                bigPictureView.setTextViewText(actionId, jsonObject.optString("actionName"));
                                bigPictureView.setOnClickPendingIntent(actionId, getPendingIntent(context, intent.getExtras(), 0, jsonObject));
                                try {
                                    if (version < 12 || targetSdkVersion < 31) {
                                        int actionBgColor = Color.parseColor(jsonObject.optString("actionBgColor"));
                                        bigPictureView.setInt(actionId, "setBackgroundColor", actionBgColor);
                                    }
                                    int actionNameColor = Color.parseColor(jsonObject.optString("actionTextColor"));
                                    bigPictureView.setTextColor(actionId, actionNameColor);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            Notification notification = builder.build();
                            notifyManager.notify(NOTIFICATION_ID, notification);

                        }
                    } else {
                        Log.d("Notifications", "no actions received");
                        bigPictureView.setViewVisibility(R.id.push_actions, View.GONE);

                    }
                    bigPictureView.setViewVisibility(R.id.left, View.GONE);
                    bigPictureView.setViewVisibility(R.id.right, View.GONE);

                } catch (Exception ignored) {
                    io.mob.resu.reandroidsdk.error.Log.e("error", ignored.getMessage());
                }
                notify(notifyManager, builder.build());

            } catch (Exception e) {
                ExceptionTracker.track(e);
            }

        } catch (Exception e) {

        }
    }

    public void carouselNotification(Context context, Intent intent, int currentPosition, Bitmap bitmap) {

        try {


            Bundle bundle = intent.getExtras();
            bundle.putInt("position", currentPosition);
            if (bundle == null)
                return;

            if (!bundle.containsKey("carousel"))
                return;

            JSONArray notificationData;
            try {
                notificationData = new JSONArray(bundle.getString("carousel"));
            } catch (Exception e) {
                notificationData = new JSONArray();
            }
            if (notificationData.length() == 0)
                return;

            String sound = "";

            String channelId = "NOTIFICATION_DEFAULT_CHANNEL";

            if (bundle.containsKey("sound"))
                sound = bundle.getString("sound");

            if (!TextUtils.isEmpty(sound)) {
                channelId = createNotificationChannels(context, sound);
                //channelId = "NOTIFICATION_CUSTOM_CHANNEL_" + sound.toUpperCase();
            } else
                channelId = createNotificationChannels(context);

            try {
                JSONObject jsonObject = notificationData.getJSONObject(currentPosition);
                String title = jsonObject.getString("title");
                String text = jsonObject.getString("body");
                String subtitle = "";
                if (jsonObject.has("subTitle")) {
                    subtitle = jsonObject.getString("subTitle");
                }
                NOTIFICATION_ID = bundle.getInt("notificationId");
                Bitmap appLogo = null;
                int icon = Util.getAppIcon(context, false);
                if (icon != 0) {
                    appLogo = BitmapFactory.decodeResource(context.getResources(), icon);
                }
                int titleColor = Color.parseColor(jsonObject.getString("titleColor"));
                int contentBgColor = Color.parseColor(jsonObject.getString("contentBgColor"));
                int bodyColor = Color.parseColor(jsonObject.getString("bodyColor"));

                int version = Integer.parseInt(Build.VERSION.RELEASE);
                int targetSdkVersion = context.getApplicationContext().getApplicationInfo().targetSdkVersion;

                RemoteViews collapsedView;

                if (version < 12 || targetSdkVersion < 31) {
                    collapsedView = new RemoteViews(context.getPackageName(), R.layout.custom_collapse_view_low);
                    collapsedView.setInt(R.id.rParant, "setBackgroundColor", contentBgColor);
                } else {
                    collapsedView = new RemoteViews(context.getPackageName(), R.layout.custom_collapse_view);
                }

                collapsedView.setTextColor(R.id.notificationTitle, titleColor);
                collapsedView.setTextColor(R.id.notificationText, bodyColor);
                collapsedView.setTextViewText(R.id.notificationTitle, HtmlCompat.fromHtml(title, htmlCode));
                collapsedView.setTextViewText(R.id.notificationText, HtmlCompat.fromHtml(text, htmlCode));
                collapsedView.setImageViewBitmap(R.id.notificationImage, appLogo);

                collapsedView.setTextViewText(R.id.notificationTitle, title);
                collapsedView.setTextViewText(R.id.notificationText, text);

                RemoteViews bigPictureView;
                if (version < 12 || targetSdkVersion < 31) {
                    bigPictureView = new RemoteViews(context.getPackageName(), R.layout.custom_expand_view_low);
                    bigPictureView.setInt(R.id.rParant, "setBackgroundColor", contentBgColor);

                } else {
                    bigPictureView = new RemoteViews(context.getPackageName(), R.layout.custom_expand_view);
                }
                bigPictureView.setTextViewText(R.id.notificationTitle, HtmlCompat.fromHtml(title, htmlCode));
                bigPictureView.setTextViewText(R.id.notificationText, HtmlCompat.fromHtml(text, htmlCode));
                bigPictureView.setImageViewBitmap(R.id.notificationImage, appLogo);

                bigPictureView.setTextColor(R.id.notificationTitle, titleColor);
                bigPictureView.setTextColor(R.id.notificationText, bodyColor);
                bigPictureView.setTextViewText(R.id.notificationTitle, title);
                bigPictureView.setTextViewText(R.id.notificationText, text);

                if (notificationData.length() > 0) {
                    if (currentPosition == 0) {
                        bigPictureView.setViewVisibility(R.id.left, View.INVISIBLE);
                        bigPictureView.setViewVisibility(R.id.right, View.VISIBLE);
                    } else if (currentPosition == notificationData.length() - 1) {
                        bigPictureView.setViewVisibility(R.id.left, View.VISIBLE);
                        bigPictureView.setViewVisibility(R.id.right, View.INVISIBLE);
                    } else {
                        bigPictureView.setViewVisibility(R.id.left, View.VISIBLE);
                        bigPictureView.setViewVisibility(R.id.right, View.VISIBLE);
                    }
                } else {
                    bigPictureView.setViewVisibility(R.id.left, View.GONE);
                    bigPictureView.setViewVisibility(R.id.right, View.GONE);
                }
                if (bitmap != null) {
                    bigPictureView.setInt(R.id.notificationText, "setMaxLines", 2);
                    bigPictureView.setViewVisibility(R.id.big_picture_imageview, View.VISIBLE);
                    bigPictureView.setViewVisibility(R.id.rparant, View.VISIBLE);
                    bigPictureView.setImageViewBitmap(R.id.big_picture_imageview, bitmap);
                } else {
                    if (TextUtils.isEmpty(title)) {
                        bigPictureView.setViewVisibility(R.id.big_picture_imageview, View.GONE);
                        bigPictureView.setViewVisibility(R.id.push_collapsed, View.GONE);
                        bigPictureView.setInt(R.id.notificationText, "setMaxLines", 12);
                    }

                }

                bigPictureView.setOnClickPendingIntent(R.id.left, getCTAIntent(context, bundle, "CarouselLeft", "Left"));
                bigPictureView.setOnClickPendingIntent(R.id.right, getCTAIntent(context, bundle, "CarouselRight", "Right"));
                NotificationCompat.Builder builder;
                if (notifyManager == null) {
                    notifyManager =
                            (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builder = new NotificationCompat.Builder(context, channelId);
                } else {
                    builder = new NotificationCompat.Builder(context, "");
                }
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setCustomContentView(collapsedView);
                builder.setCustomBigContentView(bigPictureView);
                builder.setOnlyAlertOnce(true);
                //builder.setSubText(subtitle);
                builder.setAutoCancel(true);
                intent.putExtra("clickActionName", "Open");
                intent.putExtra("clickActionId", "2");
                //Default Click
                builder.setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                NOTIFICATION_ID,
                                intent,
                                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));

                // notification clear
                builder.setDeleteIntent(notificationCancelled(context, bundle));
                // App icon setup
                getAppIcon(context, builder);

                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                builder.setNumber(0);

                JSONArray customAction = null;
                try {
                    customAction = new JSONArray(jsonObject.optString("customActions"));
                    if (customAction.length() > 0) {
                        bigPictureView.setViewVisibility(R.id.push_actions, View.VISIBLE);
                        for (int i = 0; i < customAction.length(); i++) {
                            JSONObject jsonObjectCTA = customAction.getJSONObject(i);
                            int actionId = -1;
                            switch (i) {
                                case 0:
                                    actionId = R.id.custom_action_one;
                                    break;
                                case 1:
                                    actionId = R.id.custom_action_two;
                                    break;
                                case 2:
                                    actionId = R.id.custom_action_three;
                                    break;
                            }
                            if (actionId != -1) {
                                bigPictureView.setViewVisibility(actionId, View.VISIBLE);
                                bigPictureView.setTextViewText(actionId, jsonObjectCTA.optString("actionName"));
                                bigPictureView.setOnClickPendingIntent(actionId, getPendingIntent(context, bundle, 0, jsonObjectCTA));
                                try {
                                    int actionNameColor = Color.parseColor(jsonObjectCTA.optString("actionTextColor"));
                                    if (version < 12 || targetSdkVersion < 31) {
                                        int actionBgColor = Color.parseColor(jsonObjectCTA.optString("actionBgColor"));
                                        bigPictureView.setInt(actionId, "setBackgroundColor", actionBgColor);

                                    }
                                    bigPictureView.setTextColor(actionId, actionNameColor);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                    } else {
                        Log.d("Notifications", "no actions received");
                        bigPictureView.setViewVisibility(R.id.push_actions, View.GONE);
                    }


                } catch (Exception ignored) {
                    io.mob.resu.reandroidsdk.error.Log.e("error", ignored.getMessage());
                }

                builder.setContentTitle(title);
                builder.setContentText(text);
                if (bitmap != null) {
                    builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap)
                            .setBigContentTitle(title)
                            .setSummaryText(subtitle));
                } else {
                    builder.setStyle(new NotificationCompat.BigTextStyle()
                            .setBigContentTitle(title)
                            .setSummaryText(subtitle));
                }

                notifyManager.notify(NOTIFICATION_ID, builder.build());
            } catch (Exception e) {
                ExceptionTracker.track(e);
            }
        } catch (Exception e) {

        }

    }*/

    public void showNotification(final Context context, Intent intent, Bitmap bitmap) {

        if (intent.getExtras() == null)
            return;

        if (intent.getExtras().containsKey("isCustom")) {
            if (intent.getExtras().getString("isCustom").equalsIgnoreCase("true")) {
                showNotificationCustomNotification(context, intent, bitmap);
                return;
            }
        }
        new ShowAlertNotification(context, intent, bitmap).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
    }

    public void showNotificationCustomNotification(final Context context, Intent intent, Bitmap bitmap) {
        if (intent.getExtras() == null)
            return;
        new ShowNotificationCustomNotification(context, intent, bitmap).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");

    }

    public void carouselNotification(Context context, Intent intent, int currentPosition, Bitmap bitmap) {
        try {
            if (intent.getExtras() == null)
                return;

            if (!intent.getExtras().containsKey("carousel"))
                return;

            JSONArray notificationData;

            try {
                notificationData = new JSONArray(intent.getExtras().getString("carousel"));
            } catch (Exception e) {
                notificationData = new JSONArray();
            }
            if (notificationData.length() == 0)
                return;

            new CarouselNotification(context, intent, currentPosition, bitmap).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        } catch (Exception e) {

        }
    }

    // ************* Background tread enabled ****************

    private class ShowAlertNotification extends AsyncTask<String, String, NotificationCompat.Builder> {
        Context context;
        Intent intent;
        Bitmap bitmap;

        ShowAlertNotification(Context context, Intent intent, Bitmap bitmap) {
            this.context = context;
            this.intent = intent;
            this.bitmap = bitmap;
        }

        @Override
        protected NotificationCompat.Builder doInBackground(String... strings) {
            try {
                String sound = "";
                Bundle bundle = intent.getExtras();
                String channelId = "NOTIFICATION_DEFAULT_CHANNEL";
                NOTIFICATION_ID = intent.getExtras().getInt("notificationId");

               /* if (bundle == null)
                    return;*/

               /* if (bundle.containsKey("isCustom")) {
                    if (bundle.getString("isCustom").equalsIgnoreCase("true")) {
                        showNotificationCustomNotification(context, intent, bitmap);
                        return;
                    }
                }*/

                if (bundle.containsKey("sound"))
                    sound = bundle.getString("sound");

                if (!TextUtils.isEmpty(sound)) {
                    channelId = createNotificationChannels(context, sound);
                    // channelId = "NOTIFICATION_CUSTOM_CHANNEL_" + sound.toUpperCase();
                } else
                    channelId = createNotificationChannels(context);

                String title = intent.getStringExtra("title");
                String text = intent.getStringExtra("body");

                if (TextUtils.isEmpty(title))
                    title = "Media content received";

                if (TextUtils.isEmpty(text))
                    text = "Tap to view";

                String subtitle = "";
                if (intent.hasExtra("subTitle")) {
                    subtitle = intent.getStringExtra("subTitle");
                }

                int icon;
                icon = 0;
                NotificationCompat.Builder builder;
                if (notifyManager == null) {
                    notifyManager =
                            (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builder = new NotificationCompat.Builder(context, channelId);
                } else {
                    builder = new NotificationCompat.Builder(context, "");
                }
                getAppIcon(context, builder);
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                builder.setNumber(0);
                JSONArray customAction = null;
                try {
                    customAction = new JSONArray(intent.getExtras().getString("customActions"));
                    if (customAction.length() > 0) {
                        for (int i = 0; i < customAction.length(); i++) {
                            JSONObject jsonObject = customAction.getJSONObject(i);
                            builder.addAction(getActionIntent(context, intent.getExtras(), icon, jsonObject));
                        }
                    }
                } catch (Exception ignored) {
                    io.mob.resu.reandroidsdk.error.Log.e("error", ignored.getMessage());
                }
                builder.setContentTitle(title);
                builder.setContentText(text);
                builder.setSubText(subtitle);

                if (bitmap != null) {
                    builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap)
                            .setBigContentTitle(title)
                            .setSummaryText(text));
                } else {
                    builder.setStyle(new NotificationCompat.BigTextStyle()
                            .setBigContentTitle(title)
                            .bigText(text));
                }
                builder.setAutoCancel(true);
                intent.putExtra("clickActionName", "Open");
                intent.putExtra("clickActionId", "2");
                builder.setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                NOTIFICATION_ID,
                                intent,
                                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));
                notifyManager.notify(NOTIFICATION_ID, builder.build());

                return builder;
            } catch (Exception e) {
                ExceptionTracker.track(e);
            }
            return null;
        }


    }

    private class ShowNotificationCustomNotification extends AsyncTask<String, String, String> {

        Context context;
        Intent intent;
        Bitmap bitmap;

        ShowNotificationCustomNotification(Context context, Intent intent, Bitmap bitmap) {
            this.context = context;
            this.intent = intent;
            this.bitmap = bitmap;

        }


        @Override
        protected String doInBackground(String... strings) {
            try {

                String sound = "";
                Bundle bundle = intent.getExtras();
                String channelId = "NOTIFICATION_DEFAULT_CHANNEL";

               /* if (bundle == null)
                    return;
*/
                if (bundle.containsKey("sound"))
                    sound = bundle.getString("sound");

                if (!TextUtils.isEmpty(sound)) {
                    channelId = createNotificationChannels(context, sound);
                    //   channelId = "NOTIFICATION_CUSTOM_CHANNEL_" + sound.toUpperCase();
                } else
                    channelId = createNotificationChannels(context);

                String title = intent.getStringExtra("title");
                String text = intent.getStringExtra("body");
                String subtitle = "";

                if (TextUtils.isEmpty(title))
                    title = "Media content received";

                if (TextUtils.isEmpty(text))
                    text = "Tap to view";

                if (intent.hasExtra("subTitle")) {
                    subtitle = intent.getStringExtra("subTitle");
                }
                text = text + subtitle;

                try {
                    NOTIFICATION_ID = intent.getExtras().getInt("notificationId");

                    Bitmap appLogo = null;
                    int icon = Util.getAppIcon(context, false);

                    if (icon != 0) {
                        appLogo = BitmapFactory.decodeResource(context.getResources(), icon);
                    }

                    int titleColor = Color.parseColor(intent.getStringExtra("titleColor"));
                    int contentBgColor = Color.parseColor(intent.getStringExtra("contentBgColor"));
                    int bodyColor = Color.parseColor(intent.getStringExtra("bodyColor"));

                    RemoteViews collapsedView;
                    int version = Integer.parseInt(Build.VERSION.RELEASE);
                    int targetSdkVersion = context.getApplicationContext().getApplicationInfo().targetSdkVersion;

                    if (version < 12 || targetSdkVersion < 31) {
                        collapsedView = new RemoteViews(context.getPackageName(), R.layout.custom_collapse_view_low);
                        collapsedView.setInt(R.id.rParant, "setBackgroundColor", contentBgColor);

                    } else {
                        collapsedView = new RemoteViews(context.getPackageName(), R.layout.custom_collapse_view);
                    }
                    collapsedView.setTextViewText(R.id.notificationTitle, HtmlCompat.fromHtml(title, htmlCode));
                    collapsedView.setTextViewText(R.id.notificationText, HtmlCompat.fromHtml(text, htmlCode));
                    collapsedView.setImageViewBitmap(R.id.notificationImage, appLogo);
                    collapsedView.setTextColor(R.id.notificationTitle, titleColor);
                    collapsedView.setTextColor(R.id.notificationText, bodyColor);

                    Bitmap bigPicture = null;
                    RemoteViews bigPictureView;


                    if (version < 12 || targetSdkVersion < 31) {
                        bigPictureView = new RemoteViews(context.getPackageName(), R.layout.custom_expand_view_low);
                        bigPictureView.setInt(R.id.rParant, "setBackgroundColor", contentBgColor);

                    } else {
                        bigPictureView = new RemoteViews(context.getPackageName(), R.layout.custom_expand_view);
                    }

                    bigPictureView.setTextViewText(R.id.notificationTitle, HtmlCompat.fromHtml(title, htmlCode));
                    bigPictureView.setTextViewText(R.id.notificationText, HtmlCompat.fromHtml(text, htmlCode));
                    bigPictureView.setImageViewBitmap(R.id.notificationImage, appLogo);
                    bigPictureView.setTextColor(R.id.notificationTitle, titleColor);
                    bigPictureView.setTextColor(R.id.notificationText, bodyColor);


                    if (bitmap != null) {
                        bigPictureView.setInt(R.id.notificationText, "setMaxLines", 2);
                        bigPictureView.setViewVisibility(R.id.big_picture_imageview, View.VISIBLE);
                        bigPictureView.setImageViewBitmap(R.id.big_picture_imageview, bitmap);
                    } else {
                        if (TextUtils.isEmpty(title)) {
                            bigPictureView.setViewVisibility(R.id.big_picture_imageview, View.GONE);
                            bigPictureView.setViewVisibility(R.id.push_collapsed, View.GONE);
                        }
                        bigPictureView.setInt(R.id.notificationText, "setMaxLines", 12);
                    }

                    NotificationCompat.Builder builder;
                    if (notifyManager == null) {
                        notifyManager =
                                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        builder = new NotificationCompat.Builder(context, channelId);
                    } else {
                        builder = new NotificationCompat.Builder(context, "");
                    }
                    builder.setSmallIcon(R.mipmap.ic_launcher);
                    builder.setCustomContentView(collapsedView);
                    builder.setCustomBigContentView(bigPictureView);

                    builder.setAutoCancel(true);
                    intent.putExtra("clickActionName", "Open");
                    intent.putExtra("clickActionId", "2");

                    //Default Click
                    builder.setContentIntent(
                            PendingIntent.getActivity(
                                    context,
                                    NOTIFICATION_ID,
                                    intent,
                                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));

                    // notification clear
                    builder.setDeleteIntent(notificationCancelled(context, bundle));
                    // App icon setup
                    getAppIcon(context, builder);
                    builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                    builder.setNumber(0);

                    JSONArray customAction = null;
                    try {
                        customAction = new JSONArray(intent.getExtras().getString("customActions"));
                        if (customAction.length() > 0) {
                            for (int i = 0; i < customAction.length(); i++) {
                                JSONObject jsonObject = customAction.getJSONObject(i);
                                int actionId = -1;
                                switch (i) {
                                    case 0:
                                        actionId = R.id.custom_action_one;
                                        break;
                                    case 1:
                                        actionId = R.id.custom_action_two;
                                        break;
                                    case 2:
                                        actionId = R.id.custom_action_three;
                                        break;
                                }
                                if (actionId != -1) {
                                    bigPictureView.setViewVisibility(actionId, View.VISIBLE);
                                    bigPictureView.setTextViewText(actionId, jsonObject.optString("actionName"));
                                    bigPictureView.setOnClickPendingIntent(actionId, getPendingIntent(context, intent.getExtras(), 0, jsonObject));
                                    try {
                                        if (version < 12 || targetSdkVersion < 31) {
                                            int actionBgColor = Color.parseColor(jsonObject.optString("actionBgColor"));
                                            bigPictureView.setInt(actionId, "setBackgroundColor", actionBgColor);
                                        }
                                        int actionNameColor = Color.parseColor(jsonObject.optString("actionTextColor"));
                                        bigPictureView.setTextColor(actionId, actionNameColor);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                                Notification notification = builder.build();
                                notifyManager.notify(NOTIFICATION_ID, notification);

                            }
                        } else {
                            Log.d("Notifications", "no actions received");
                            bigPictureView.setViewVisibility(R.id.push_actions, View.GONE);

                        }
                        bigPictureView.setViewVisibility(R.id.left, View.GONE);
                        bigPictureView.setViewVisibility(R.id.right, View.GONE);

                    } catch (Exception ignored) {
                        io.mob.resu.reandroidsdk.error.Log.e("error", ignored.getMessage());
                    }

                    // notify(notifyManager, builder.build());

                    notifyManager.notify(NOTIFICATION_ID, builder.build());

                } catch (Exception e) {
                    ExceptionTracker.track(e);
                }

            } catch (Exception e) {

            }
            return null;
        }
    }

    private class CarouselNotification extends AsyncTask<String, String, String> {

        Context context;
        Intent intent;
        int currentPosition;
        Bitmap bitmap;

        CarouselNotification(Context context, Intent intent, int currentPosition, Bitmap bitmap) {
            this.context = context;
            this.intent = intent;
            this.currentPosition = currentPosition;
            this.bitmap = bitmap;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {


                Bundle bundle = intent.getExtras();
                bundle.putInt("position", currentPosition);
/*
                if (!bundle.containsKey("carousel"))
                    return;*/

                JSONArray notificationData;
                try {
                    notificationData = new JSONArray(bundle.getString("carousel"));
                } catch (Exception e) {
                    notificationData = new JSONArray();
                }
                /*if (notificationData.length() == 0)
                    return;*/

                String sound = "";

                String channelId = "NOTIFICATION_DEFAULT_CHANNEL";

                if (bundle.containsKey("sound"))
                    sound = bundle.getString("sound");

                if (!TextUtils.isEmpty(sound)) {
                    channelId = createNotificationChannels(context, sound);
                    //channelId = "NOTIFICATION_CUSTOM_CHANNEL_" + sound.toUpperCase();
                } else
                    channelId = createNotificationChannels(context);

                try {
                    JSONObject jsonObject = notificationData.getJSONObject(currentPosition);
                    String title = jsonObject.getString("title");
                    String text = jsonObject.getString("body");
                    String subtitle = "";
                    if (jsonObject.has("subTitle")) {
                        subtitle = jsonObject.getString("subTitle");
                    }
                    NOTIFICATION_ID = bundle.getInt("notificationId");
                    Bitmap appLogo = null;
                    int icon = Util.getAppIcon(context, false);
                    if (icon != 0) {
                        appLogo = BitmapFactory.decodeResource(context.getResources(), icon);
                    }
                    int titleColor = Color.parseColor(jsonObject.getString("titleColor"));
                    int contentBgColor = Color.parseColor(jsonObject.getString("contentBgColor"));
                    int bodyColor = Color.parseColor(jsonObject.getString("bodyColor"));

                    int version = Integer.parseInt(Build.VERSION.RELEASE);
                    int targetSdkVersion = context.getApplicationContext().getApplicationInfo().targetSdkVersion;

                    RemoteViews collapsedView;

                    if (version < 12 || targetSdkVersion < 31) {
                        collapsedView = new RemoteViews(context.getPackageName(), R.layout.custom_collapse_view_low);
                        collapsedView.setInt(R.id.rParant, "setBackgroundColor", contentBgColor);
                    } else {
                        collapsedView = new RemoteViews(context.getPackageName(), R.layout.custom_collapse_view);
                    }

                    collapsedView.setTextColor(R.id.notificationTitle, titleColor);
                    collapsedView.setTextColor(R.id.notificationText, bodyColor);
                    collapsedView.setTextViewText(R.id.notificationTitle, HtmlCompat.fromHtml(title, htmlCode));
                    collapsedView.setTextViewText(R.id.notificationText, HtmlCompat.fromHtml(text, htmlCode));
                    collapsedView.setImageViewBitmap(R.id.notificationImage, appLogo);

                    collapsedView.setTextViewText(R.id.notificationTitle, title);
                    collapsedView.setTextViewText(R.id.notificationText, text);

                    RemoteViews bigPictureView;
                    if (version < 12 || targetSdkVersion < 31) {
                        bigPictureView = new RemoteViews(context.getPackageName(), R.layout.custom_expand_view_low);
                        bigPictureView.setInt(R.id.rParant, "setBackgroundColor", contentBgColor);

                    } else {
                        bigPictureView = new RemoteViews(context.getPackageName(), R.layout.custom_expand_view);
                    }
                    bigPictureView.setTextViewText(R.id.notificationTitle, HtmlCompat.fromHtml(title, htmlCode));
                    bigPictureView.setTextViewText(R.id.notificationText, HtmlCompat.fromHtml(text, htmlCode));
                    bigPictureView.setImageViewBitmap(R.id.notificationImage, appLogo);

                    bigPictureView.setTextColor(R.id.notificationTitle, titleColor);
                    bigPictureView.setTextColor(R.id.notificationText, bodyColor);
                    bigPictureView.setTextViewText(R.id.notificationTitle, title);
                    bigPictureView.setTextViewText(R.id.notificationText, text);

                    if (notificationData.length() > 0) {
                        if (currentPosition == 0) {
                            bigPictureView.setViewVisibility(R.id.left, View.INVISIBLE);
                            bigPictureView.setViewVisibility(R.id.right, View.VISIBLE);
                        } else if (currentPosition == notificationData.length() - 1) {
                            bigPictureView.setViewVisibility(R.id.left, View.VISIBLE);
                            bigPictureView.setViewVisibility(R.id.right, View.INVISIBLE);
                        } else {
                            bigPictureView.setViewVisibility(R.id.left, View.VISIBLE);
                            bigPictureView.setViewVisibility(R.id.right, View.VISIBLE);
                        }
                    } else {
                        bigPictureView.setViewVisibility(R.id.left, View.GONE);
                        bigPictureView.setViewVisibility(R.id.right, View.GONE);
                    }
                    if (bitmap != null) {
                        bigPictureView.setInt(R.id.notificationText, "setMaxLines", 2);
                        bigPictureView.setViewVisibility(R.id.big_picture_imageview, View.VISIBLE);
                        bigPictureView.setViewVisibility(R.id.rparant, View.VISIBLE);
                        bigPictureView.setImageViewBitmap(R.id.big_picture_imageview, bitmap);
                    } else {
                        if (TextUtils.isEmpty(title)) {
                            bigPictureView.setViewVisibility(R.id.big_picture_imageview, View.GONE);
                            bigPictureView.setViewVisibility(R.id.push_collapsed, View.GONE);
                            bigPictureView.setInt(R.id.notificationText, "setMaxLines", 12);
                        }

                    }

                    bigPictureView.setOnClickPendingIntent(R.id.left, getCTAIntent(context, bundle, "CarouselLeft", "Left"));
                    bigPictureView.setOnClickPendingIntent(R.id.right, getCTAIntent(context, bundle, "CarouselRight", "Right"));
                    NotificationCompat.Builder builder;
                    if (notifyManager == null) {
                        notifyManager =
                                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        builder = new NotificationCompat.Builder(context, channelId);
                    } else {
                        builder = new NotificationCompat.Builder(context, "");
                    }
                    builder.setSmallIcon(R.mipmap.ic_launcher);
                    builder.setCustomContentView(collapsedView);
                    builder.setCustomBigContentView(bigPictureView);
                    builder.setOnlyAlertOnce(true);
                    //builder.setSubText(subtitle);
                    builder.setAutoCancel(true);
                    intent.putExtra("clickActionName", "Open");
                    intent.putExtra("clickActionId", "2");
                    //Default Click
                    builder.setContentIntent(
                            PendingIntent.getActivity(
                                    context,
                                    NOTIFICATION_ID,
                                    intent,
                                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));

                    // notification clear
                    builder.setDeleteIntent(notificationCancelled(context, bundle));
                    // App icon setup
                    getAppIcon(context, builder);

                    builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                    builder.setNumber(0);

                    JSONArray customAction = null;
                    try {
                        customAction = new JSONArray(jsonObject.optString("customActions"));
                        if (customAction.length() > 0) {
                            bigPictureView.setViewVisibility(R.id.push_actions, View.VISIBLE);
                            for (int i = 0; i < customAction.length(); i++) {
                                JSONObject jsonObjectCTA = customAction.getJSONObject(i);
                                int actionId = -1;
                                switch (i) {
                                    case 0:
                                        actionId = R.id.custom_action_one;
                                        break;
                                    case 1:
                                        actionId = R.id.custom_action_two;
                                        break;
                                    case 2:
                                        actionId = R.id.custom_action_three;
                                        break;
                                }
                                if (actionId != -1) {
                                    bigPictureView.setViewVisibility(actionId, View.VISIBLE);
                                    bigPictureView.setTextViewText(actionId, jsonObjectCTA.optString("actionName"));
                                    bigPictureView.setOnClickPendingIntent(actionId, getPendingIntent(context, bundle, 0, jsonObjectCTA));
                                    try {
                                        int actionNameColor = Color.parseColor(jsonObjectCTA.optString("actionTextColor"));
                                        if (version < 12 || targetSdkVersion < 31) {
                                            int actionBgColor = Color.parseColor(jsonObjectCTA.optString("actionBgColor"));
                                            bigPictureView.setInt(actionId, "setBackgroundColor", actionBgColor);

                                        }
                                        bigPictureView.setTextColor(actionId, actionNameColor);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }
                            }
                        } else {
                            Log.d("Notifications", "no actions received");
                            bigPictureView.setViewVisibility(R.id.push_actions, View.GONE);
                        }


                    } catch (Exception ignored) {
                        io.mob.resu.reandroidsdk.error.Log.e("error", ignored.getMessage());
                    }

                    builder.setContentTitle(title);
                    builder.setContentText(text);
                    if (bitmap != null) {
                        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap)
                                .setBigContentTitle(title)
                                .setSummaryText(subtitle));
                    } else {
                        builder.setStyle(new NotificationCompat.BigTextStyle()
                                .setBigContentTitle(title)
                                .setSummaryText(subtitle));
                    }

                    notifyManager.notify(NOTIFICATION_ID, builder.build());
                } catch (Exception e) {
                    ExceptionTracker.track(e);
                }
            } catch (Exception e) {

            }

            return null;
        }
    }

    // ************* Background tread enabled ****************

}

