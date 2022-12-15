package io.mob.resu.reandroidsdk;

import static io.mob.resu.reandroidsdk.AppConstants.NOTIFICATION_RECEIVED_AMP;
import static io.mob.resu.reandroidsdk.AppConstants.NOTIFICATION_RECEIVED_FCM;
import static io.mob.resu.reandroidsdk.Util.getLauncherActivityName;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;
import io.mob.resu.reandroidsdk.error.Log;


class NotificationHelper {

    private static Context appContext;

    NotificationHelper(Context context) {
        appContext = context;
    }

    private Intent getIntent(Map<String, String> map) {
        Intent intent = new Intent();
        try {
            JSONObject jsonObject = new JSONObject();
            try {
                intent = new Intent(appContext, Class.forName(map.get("navigationScreen").trim()));
            } catch (Exception e) {
                intent = new Intent(appContext, Class.forName(getLauncherActivityName(appContext)));
            }

            for (String value : map.keySet()) {
                intent.putExtra(value, map.get(value));
                jsonObject.put(value, map.get(value));
            }
            Log.e("Push Notification", "" + jsonObject.toString());
            intent.putExtra("activityName", map.get("navigationScreen"));
            intent.putExtra("fragmentName", map.get("category"));
            intent.putExtra("notificationId", map.get("id").hashCode());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            return intent;
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
        return intent;

    }

    private Intent getIntent(Bundle map) {
        Intent intent = new Intent();
        try {
            try {
                intent = new Intent(appContext, Class.forName(map.getString("navigationScreen").trim()));
            } catch (Exception e) {
                intent = new Intent(appContext, Class.forName(getLauncherActivityName(appContext)));
            }
            Object[] parameters = map.keySet().toArray();
            JSONObject jsonObject = new JSONObject();
            for (Object o : parameters) {
                String key = "" + o;
                String value = "" + map.get(key);
                //Log.e("key", "" + o);
                // Log.e("values", "" + map.get(key));
                intent.putExtra(key, value);
                jsonObject.put(key, value);
            }
            Log.e("Push Notification", "" + jsonObject.toString());
            intent.putExtra("notificationId", map.getString("id").hashCode());
            intent.putExtra("activityName", map.getString("navigationScreen"));
            intent.putExtra("fragmentName", map.getString("category"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            return intent;
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
        return intent;

    }

    void handleDataMessage(Bundle map) {
        try {
            if (map.containsKey("isCarousel")) {
                if (Boolean.parseBoolean(map.getString("isCarousel"))) {
                    if (map.containsKey("carousel")) {
                        Intent intent = getIntent(map);
                        addNotification(intent);
                        presentNotification(intent);
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("url", AppConstants.CarosualUrl + "=" + map.getString("id"));
                            new DataNetworkHandler().apiCallGetCarouselNotification(appContext, jsonObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    Intent intent = getIntent(map);
                    addNotification(intent);
                    presentNotification(intent);
                }
            } else {
                Intent intent = getIntent(map);
                addNotification(intent);
                presentNotification(intent);
            }


        } catch (Exception e) {
            ExceptionTracker.track(e);
        }

    }

    void handleDataMessage(Map<String, String> map) {

        try {

            if (map.containsKey("isCarousel")) {
                if (Boolean.parseBoolean(map.get("isCarousel"))) {
                    if (map.containsKey("carousel")) {
                        Intent intent = getIntent(map);
                        addNotification(intent);
                        presentNotification(intent);
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("deviceid", "123456");
                            jsonObject.put("notificationId", "X7P_I_BYP_6S_T_JTZF_20201020031534");
                            jsonObject.put("appid", "b7d852b7-4ea8-43d9-a001-3d655834e314");
                            jsonObject.put("url", AppConstants.CarosualUrl + "=" + map.get("id"));
                            new DataNetworkHandler().apiCallGetCarouselNotification(appContext, jsonObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Intent intent = getIntent(map);
                    addNotification(intent);
                    presentNotification(intent);
                }
            } else {
                Intent intent = getIntent(map);
                addNotification(intent);
                presentNotification(intent);
            }
            // new OfflineCampaignTrack(appContext, map.get("id"), NOTIFICATION_RECEIVED, "Notification Received", false, null, null, DataNetworkHandler.getInstance()).execute();

        } catch (Exception e) {
            ExceptionTracker.track(e);
        }

    }

    private void presentNotification(Intent intent) {

        try {

            Bundle map = intent.getExtras();
            String pushType = map.getString("pushType");
            String url = "";
            if (map.containsKey("url"))
                url = map.getString("url").trim();
            assert pushType != null;

            if (NotificationManagerCompat.from(appContext).areNotificationsEnabled()) {

                if (pushType.equalsIgnoreCase("2")) {
                    // In-app
                    if (Util.isAppIsInBackground(appContext)) {
                        if (!TextUtils.isEmpty(url) && map.getString("sourceType").equalsIgnoreCase("4"))
                            new PictureStyleNotification(appContext, intent);
                        else {
                            new AppNotification().showNotification(appContext, intent, null);
                        }
                    } else {
                        new AppWidgets().showBannerDialog(ReAndroidSDK.activityLifecycleCallbacks.mActivity, intent);
                    }
                } else if (pushType.equalsIgnoreCase("1")) {

                    if (intent.getExtras().containsKey("carousel")) {
                        JSONArray jsonArray = new JSONArray(intent.getStringExtra("carousel"));
                        String imageUrl = jsonArray.getJSONObject(0).optString("url");
                        if (TextUtils.isEmpty(imageUrl)) {
                            new AppNotification().carouselNotification(appContext, intent, 0, null);
                        } else {
                            new PictureStyleNotification(appContext, intent, imageUrl, 0);
                        }
                    } else if (!TextUtils.isEmpty(url) && map.getString("sourceType").equalsIgnoreCase("4")) {
                        new PictureStyleNotification(appContext, intent);
                    } else {
                        new AppNotification().showNotification(appContext, intent, null);
                    }

                }
            } else {

                if (!Util.isAppIsInBackground(appContext) && NotificationManagerCompat.from(appContext).areNotificationsEnabled() || AppConstants.NOTIFICATION_DND_DISABLED) {
                    new AppWidgets().showBannerDialog(ActivityLifecycleCallbacks.mActivity, intent);
                } else {
                    Log.e("User Disabled", "Notifications");
                }
            }

        } catch (Exception es) {
            es.printStackTrace();
        }
    }

    private void addNotification(Intent intent) {
        try {
            Bundle map = intent.getExtras();
            Set<String> stringSet = map.keySet();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("timeStamp", Util.getCurrentUTC());
            String id = getValue(map, "id");
            jsonObject.put("campaignId", id);
            jsonObject.put("isRead", false);

            for (String s : stringSet) {
                jsonObject.put(s, map.get(s));
            }
            /*try {
                ArrayList<RNotification> arrayList = new DataBase(appContext).getDataByModel(DataBase.Table.NOTIFICATION_TABLE);
                if (arrayList != null && arrayList.size() > 15) {
                    new DataBase(appContext).deleteNotificationData(arrayList.get(0), DataBase.Table.NOTIFICATION_TABLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            new DataBase(appContext).insertOrUpdateNData(jsonObject.toString(), id, DataBase.Table.NOTIFICATION_TABLE);
            */
            try {
                if (NotificationManagerCompat.from(appContext).areNotificationsEnabled() || AppConstants.NOTIFICATION_DND_DISABLED) {
                    new DBTask(jsonObject, id).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            } catch (Exception e) {

            }

            try {
                if (map.containsKey("isAMP")) {
                    boolean isEnd = Boolean.parseBoolean(map.getString("isEND"));
                    new OfflineCampaignTrack(appContext, id, NOTIFICATION_RECEIVED_AMP, true,isEnd, DataNetworkHandler.getInstance()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                } else {
                    new OfflineCampaignTrack(appContext, id, NOTIFICATION_RECEIVED_FCM, "Notification received", false, null, null, DataNetworkHandler.getInstance()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                }
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
    }

    private String getValue(Bundle map, String key) {
        try {
            if (map.containsKey(key)) {
                return map.getString(key);
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    private class DBTask extends AsyncTask<String, String, String> {
        JSONObject jsonObject;
        String id;

        DBTask(JSONObject jsonObject, String id) {
            this.jsonObject = jsonObject;
            this.id = id;
        }

        protected String doInBackground(String... urls) {
            try {
                ArrayList<RNotification> arrayList = new DataBase(appContext).getDataByModel(DataBase.Table.NOTIFICATION_TABLE);
                if (arrayList != null && arrayList.size() > 15) {
                    new DataBase(appContext).deleteNotificationData(arrayList.get(0), DataBase.Table.NOTIFICATION_TABLE);
                }
                new DataBase(appContext).insertOrUpdateNData(jsonObject.toString(), id, DataBase.Table.NOTIFICATION_TABLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
    }

}
