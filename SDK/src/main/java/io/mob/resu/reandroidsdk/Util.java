package io.mob.resu.reandroidsdk;

import static io.mob.resu.reandroidsdk.AppConstants.AAGE;
import static io.mob.resu.reandroidsdk.AppConstants.ADOP;
import static io.mob.resu.reandroidsdk.AppConstants.AEDUCATION;
import static io.mob.resu.reandroidsdk.AppConstants.AEMAIL;
import static io.mob.resu.reandroidsdk.AppConstants.AEMPLOYED;
import static io.mob.resu.reandroidsdk.AppConstants.AGENDER;
import static io.mob.resu.reandroidsdk.AppConstants.AMARRIED;
import static io.mob.resu.reandroidsdk.AppConstants.ANAME;
import static io.mob.resu.reandroidsdk.AppConstants.APHONE;
import static io.mob.resu.reandroidsdk.AppConstants.APHOTO;
import static io.mob.resu.reandroidsdk.ReAndroidSDK.AppInfo;
import static io.mob.resu.reandroidsdk.error.Util.isAppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;
import io.mob.resu.reandroidsdk.error.Log;


public class Util {


    static int width = 0;
    static int height = 0;
    private static NotificationManager notifManager;

    public static String getAppKey(Context context) {

        //AppConstants.baseUrl = SharedPref.getInstance().getStringValue(context, "dynamicBaseUrl");
        try {
            String user = getMetadata(context, AppConstants.reManifestApiKey);

            if (user == null) {
                user = getMetadata(context, AppConstants.sdkManifestApiKey);
            }

            if (user != null) {
                user = user.replace("api_key_", "");
                SharedPref.getInstance().setSharedValue(context, AppConstants.reSharedAPIKey, user);

            } else {
                user = "";
                Toast.makeText(context, "Please add your SDK API KEY", Toast.LENGTH_LONG).show();
            }
            return user;
        } catch (Exception e) {

        }
        return "";
    }

    public static int getAppIcon(Context context, boolean isTransparent) {
        int user = 0;

        try {
            if (isTransparent) {
                user = getMetaDataResource(context, AppConstants.reManifestNotificationIconTrans);
                if (user == 0) {
                    user = getMetaDataResource(context, AppConstants.sdkManifestNotificationIconTrans);
                }
            } else {
                user = getMetaDataResource(context, AppConstants.reManifestNotificationIcon);
                if (user == 0) {
                    user = getMetaDataResource(context, AppConstants.sdkManifestNotificationIcon);
                }
            }

            if (user != 0) {
                return user;
            } else {
                Toast.makeText(context, "Please add your notification icon in the AndroidManifest.xml", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {

        }
        return 0;
    }

    public static int getAppIconColor(Context context) {
        try {
            int user = getMetaDataResource(context, AppConstants.reManifestNotificationColor);
            if (user == 0) {
                user = getMetaDataResource(context, AppConstants.sdkManifestNotificationColor);
            }

            if (user != 0)
                return user;
            else
                Toast.makeText(context, "Please add your notification color in the AndroidManifest.xml", Toast.LENGTH_LONG).show();
        } catch (Exception e) {

        }
        return 0;
    }

    public static String getMetaDataValues(Context context, String key, String errorMessage) {
        //  AppConstants.baseUrl = SharedPref.getInstance().getStringValue(context, "dynamicBaseUrl");
        try {
            String user = getMetadata(context, key);
            if (user != null) {
                return user;
            } else {
                Toast.makeText(context, "Please add your " + errorMessage + " in the manifest file", Toast.LENGTH_LONG).show();
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    public static JSONObject getAppDetails(final Context context) {

        JSONObject masterPage = new JSONObject();
        try {
            ArrayList<JSONObject> jsonObjects = new ArrayList<>();
            final AssetManager _am = context.createPackageContext(context.getPackageName(), 0).getAssets();
            final XmlResourceParser _xmlParser = _am.openXmlResourceParser(0, "AndroidManifest.xml");
            int _eventType = _xmlParser.getEventType();
            boolean isReceiver = false;
            boolean isMetadata = false;


            ArrayList<JSONObject> data = new ArrayList<>();
            JSONObject jsonObject = new JSONObject();

            while (_eventType != XmlPullParser.END_DOCUMENT) {

                if (_xmlParser.getName() != null) {
                    if ((_eventType == XmlPullParser.START_TAG) && _xmlParser.getName().equalsIgnoreCase("activity") || _xmlParser.getName().equalsIgnoreCase("data")) {
                        if (_xmlParser.getName().equalsIgnoreCase("activity")) {
                            if (jsonObject == null) {
                                jsonObject = new JSONObject();
                                data = new ArrayList<>();
                            } else {
                                if (!data.isEmpty()) {
                                    try {
                                        JSONObject object = data.get(0);
                                        String deepLinkUrl = "intent://" + object.getString("host") + "/#Intent;scheme=" + object.getString("scheme") + ";package=" + context.getPackageName();
                                        jsonObject.put("data", new JSONArray(data.toString()));
                                        jsonObject.put("isDeepLink", true);
                                        jsonObject.put("isDeepLinkUrl", deepLinkUrl);
                                    } catch (Exception e) {
                                        e.getMessage();
                                        jsonObject.put("isDeepLink", false);
                                    }
                                } else {
                                    jsonObject.put("isDeepLink", false);
                                }

                                if (jsonObject.has("activityName"))
                                    jsonObjects.add(jsonObject);

                                jsonObject = new JSONObject();
                                data = new ArrayList<>();
                            }

                            for (byte i = 0; i < _xmlParser.getAttributeCount(); i++) {


                                Log.e("Label", "" + _xmlParser.getAttributeName(i) + ":" + _xmlParser.getAttributeValue(i));


                                if (_xmlParser.getAttributeName(i).equalsIgnoreCase("name") && !_xmlParser.getAttributeValue(i).equalsIgnoreCase("com.google.android.gms.ads.AdActivity") && !_xmlParser.getAttributeValue(i).equalsIgnoreCase("com.google.android.gms.ads.purchase.InAppPurchaseActivity")) {
                                    String name = _xmlParser.getAttributeValue(i);
                                    String[] names = name.replace(".", ",").split(",");
                                    jsonObject.put("activityName", name);

                                    if (!jsonObject.has("displayName"))
                                        jsonObject.put("displayName", names[names.length - 1]);

                                    Log.e("name", "" + names[names.length - 1]);

                                }

                            }

                        } else if (_xmlParser.getName().equalsIgnoreCase("data")) {

                            JSONObject jsonObject1 = new JSONObject();

                            for (byte i = 0; i < _xmlParser.getAttributeCount(); i++) {
                                jsonObject1.put(_xmlParser.getAttributeName(i), _xmlParser.getAttributeValue(i));
                            }

                            if (jsonObject1.has("host"))
                                data.add(jsonObject1);

                        }
                    } else if ((_eventType == XmlPullParser.START_TAG) && _xmlParser.getName().equalsIgnoreCase("receiver")) {
                        for (byte i = 0; i < _xmlParser.getAttributeCount(); i++) {
                            if (_xmlParser.getAttributeName(i).equalsIgnoreCase("name") && _xmlParser.getAttributeValue(i).equalsIgnoreCase("io.mob.resu.reandroidsdk.InstallReferrerReceiver"))
                                isReceiver = true;
                        }
                    } else if ((_eventType == XmlPullParser.START_TAG) && _xmlParser.getName().equalsIgnoreCase("meta-data")) {
                        for (byte i = 0; i < _xmlParser.getAttributeCount(); i++) {
                            if (_xmlParser.getAttributeName(i).equalsIgnoreCase("name") && _xmlParser.getAttributeValue(i).equalsIgnoreCase("resulticks.key"))
                                isMetadata = true;

                            if (_xmlParser.getAttributeName(i).equalsIgnoreCase("name") && _xmlParser.getAttributeValue(i).equalsIgnoreCase("sdk.key"))
                                isMetadata = true;
                        }
                    }
                }
                _eventType = _xmlParser.nextToken();
            }

            masterPage.put("screens", new JSONArray(jsonObjects.toString()));
            masterPage.put("isReceiver", isReceiver);
            masterPage.put("isMetadata", isMetadata);
            _xmlParser.close();
        } catch (final XmlPullParserException exception) {
            exception.printStackTrace();
        } catch (final PackageManager.NameNotFoundException exception) {
            exception.printStackTrace();
        } catch (final IOException exception) {
            exception.printStackTrace();
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
        return masterPage;
    }

    public static String getFCMToken(Context context) {
        try {
           /* Class aClass = Class.forName("com.google.firebase.iid.FirebaseInstanceId");
            Method method = aClass.getMethod("getInstance");
            Object dog = method.invoke(method);
            Log.e("tokken", "" + dog.getClass().getSimpleName());
            Method method1 = dog.getClass().getMethod("getToken");
            Log.e("tokken", "" + method1.invoke(dog));*/
            return "";//+ method1.invoke(dog);
        } catch (Exception e) {
            //e.printStackTrace();
            ExceptionTracker.track(e);
            return getGCMToken(context);
        }

    }

    private static String getGCMToken(Context context) {
        try {
           /* int senderID = context.getResources().getIdentifier("gcm_defaultSenderId", "String", context.getPackageName());
            Class aClass = Class.forName("com.google.android.gms.iid.InstanceID");
            Method method = aClass.getMethod("getToken", String.class, String.class, Bundle.class);*/
            return "";//+ method.invoke(null, context.getString(senderID), "GCM", null);
        } catch (Exception e) {
            //e.printStackTrace();
            // ExceptionTracker.track(e);
            return "";
        }
    }

    /**
     * Method checks if the app is in background or not
     */
    static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(context.getPackageName())) {
                                isInBackground = false;
                            }
                        }
                    }
                }
            } else {
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                if (componentInfo.getPackageName().equals(context.getPackageName())) {
                    isInBackground = false;
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }

        return isInBackground;
    }

    /* static void getAdvertisementId(final Context appContext) {
         AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
             @Override
             protected String doInBackground(Void... params) {


                 AdvertisingIdClient.Info idInfo = null;
                 try {
                     idInfo = AdvertisingIdClient.getAdvertisingIdInfo(appContext);
                 } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException | IOException e) {
                     e.printStackTrace();
                 }
                 try {
                     SharedPref.getInstance().setSharedValue(appContext, appContext.getString(R.string.resulticksSharedAdverId), idInfo.getId());
                 } catch (NullPointerException e) {
                     e.printStackTrace();
                     SharedPref.getInstance().setSharedValue(appContext, appContext.getString(R.string.resulticksSharedAdverId), "");

                 }

                 return "";
             }

             @Override
             protected void onPostExecute(String advertId) {
 //                Toast.makeText(getApplicationContext(), advertId, Toast.LENGTH_SHORT).show();
             }

         };
         task.execute();
     }
 */
    static String getCurrentUTC() {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = (SimpleDateFormat) getTimeStampFormat();
            return simpleDateFormat.format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            return "0000-00-00'T'00:00:00";
        }
    }

    static String getNotificationCurrentUTC() {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return simpleDateFormat.format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            return "0000-00-00'T'00:00:00";
        }

    }

    static Date getStringToUTC(String date) {
        try {
            SimpleDateFormat simpleDateFormat = (SimpleDateFormat) getTimeStampFormat();
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    static String getStringDate(String date) {
        try {
            Date date1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss aaa").parse(date);
            SimpleDateFormat simpleDateFormat = (SimpleDateFormat) getTimeStampFormat();
            return simpleDateFormat.format(date1.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    static boolean isExpired(String strDate) {

        try {
            SimpleDateFormat simpleDateFormat = (SimpleDateFormat) getTimeStampFormat();
            Date date = simpleDateFormat.parse(strDate);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return showScreenSession(getCurrentCalenderUTC(), calendar);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private static Calendar getCurrentCalenderUTC() {
        try {
            SimpleDateFormat simpleDateFormat = (SimpleDateFormat) getTimeStampFormat();
            Date date = simpleDateFormat.parse(getCurrentUTC());
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getMetadata(Context context, String name) {

        try {
            final ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo.metaData != null && applicationInfo.metaData.containsKey(name)) {
                return applicationInfo.metaData.getString(name, null);
            } else
                return null;

        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
        return null;
    }

    private static int getMetaDataResource(Context context, String name) {

        try {
            final ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo.metaData != null && applicationInfo.metaData.containsKey(name)) {
                return applicationInfo.metaData.getInt(name);
            } else
                return 0;

        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
        return 0;
    }

    @SuppressLint("HardwareIds")
    static String getDeviceId(Context context) {
        try {
            String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            SharedPref.getInstance().setSharedValue(context, AppConstants.reSharedDatabaseDeviceId, deviceId);
            return deviceId;
        } catch (Exception e) {
            return "";

        }

    }

    static String getDeviceType(Context context) {

        try {
            String deviceType;
            if (isTablet(context))
                deviceType = "Android Tab";
            else
                deviceType = "Android Phone";
            return deviceType;
        } catch (Exception e) {
            return "Android Phone";
        }

    }

    static boolean isTablet(Context context) {
        try {
            boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
            boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
            return (xlarge || large);
        } catch (Exception e) {
            return false;
        }

    }

    static ArrayList<String> getAllActivity(Context appContext) {
        ArrayList<String> activityList = new ArrayList<>();
        try {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            PackageManager pm = appContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo(appContext.getApplicationContext().getPackageName(), PackageManager.GET_ACTIVITIES);
            ActivityInfo[] list = info.activities;
            for (ActivityInfo activityInfo : list) {
                Log.d("", "ActivityInfo = " + activityInfo);
                if (!TextUtils.isEmpty(activityInfo.name) && !activityInfo.name.startsWith("com.google.android")) {
                    activityList.add(activityInfo.name);
                    String myClass = activityInfo.name;
                    Class<?> myClass1 = Class.forName(myClass);
                    Activity obj = (Activity) myClass1.newInstance();

                }
            }

            info = pm.getPackageInfo(appContext.getApplicationContext().getPackageName(), PackageManager.GET_ACTIVITIES);
            list = info.activities;
            for (ActivityInfo activityInfo : list) {

                if (!TextUtils.isEmpty(activityInfo.name) && !activityInfo.name.startsWith("com.google.android")) {
                    activityList.add(activityInfo.name);
                    String myClass = activityInfo.name;
                    Class<?> myClass1 = Class.forName(myClass);
                    Activity obj = (Activity) myClass1.newInstance();
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return activityList;
    }

    static String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (packageInfo.versionName != null)
                return packageInfo.versionName;
            else
                return "";
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return "";
    }

    static String getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            return "" + packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return "";
    }

    static String getDeviceIME(Context context) {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return "" + telephonyManager.getDeviceId();
        } catch (Exception ignored) {
        }
        return "";
    }

    static String getDeviceNetworkProvider(Context context) {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            // Get System TELEPHONY service reference
            TelephonyManager tManager = (TelephonyManager) context.getApplicationContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String carrierName = tManager.getNetworkOperatorName();

            return "" + carrierName;
        } catch (Exception ignored) {
        }
        return "";
    }

    static String getDeviceSimSerialNumber(Context context) {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            // Get System TELEPHONY service reference
            TelephonyManager tManager = (TelephonyManager) context.getApplicationContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String getSimSerialNumber = tManager.getSimSerialNumber();

            return "" + getSimSerialNumber;
        } catch (Exception ignored) {
        }
        return "";
    }

    static String getDeviceSimNumber(Context context) {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            // Get System TELEPHONY service reference
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return "" + tm.getLine1Number();
        } catch (Exception ignored) {
        }
        return "";
    }

    static void catchMessage(Exception e) {
        Log.e("Utility", "error");
        // e.printStackTrace();
    }

    /**
     * Check Activity have Fragment
     *
     * @param activity
     * @return
     */

    static boolean itHasFragment(Activity activity) {
        try {
            if (isAppCompatActivity(activity)) {
                FragmentManager manager = ((FragmentActivity) activity).getSupportFragmentManager();
                return manager == null || manager.getFragments() == null || manager.getFragments().size() <= 0;
            }
            return false;
        } catch (Exception e) {
            catchMessage(e);
            return true;
        }


    }

    static String getResumeJourneyData(Context context) {
        String data = SharedPref.getInstance().getStringValue(context, AppConstants.reResumeJourney);
        if (TextUtils.isEmpty(data)) {
            data = "{}";
        }

        Log.e("getResumeJourneyData", data);
        return data;
    }

    /**
     * Show screen Spent Timer
     *
     * @param start
     * @param end
     */
    private static boolean showScreenSession(Calendar start, Calendar end) {

        long difference = end.getTime().getTime() - start.getTime().getTime();
        long differenceSeconds = difference / 1000 % 60;
        long differenceMinutes = difference / (60 * 1000) % 60;
        long differenceHours = difference / (60 * 60 * 1000) % 24;
        long differenceDays = difference / (24 * 60 * 60 * 1000);

        try {


            if (differenceDays > 0 || differenceHours > 0 || differenceMinutes > 0 || differenceSeconds > 3) {
                /// Log.e("Notification ", "live");
                return true;
            } else {
                Log.e("Notification ", "Expired");
                return false;
            }

        } catch (Exception e) {
            Log.e("Duration Call", "" + e);
            return true;
        }


    }

    /**
     * Show screen Spent Timer
     *
     * @param start
     * @param end
     */
    static String appSession(Date start, Date end) {
        try {
            long difference = (end.getTime() - start.getTime()) / 1000;
            return "" + difference;
        } catch (Exception e) {
            return "";
        }

    }

    /**
     * Deep linking Data erase
     *
     * @param activity
     */
    static void deepLinkDataReset(Context activity) {
        try {

            Log.e("destory Called", "Destory");
            JSONObject referrerObject = new JSONObject();
            referrerObject.put(AppConstants.reDeepLinkParamIsNewInstall, false);
            referrerObject.put(AppConstants.reDeepLinkParamIsViaDeepLinkingLauncher, false);
            SharedPref.getInstance().setSharedValue(activity, AppConstants.reNotificationViaLauncher, false);
            SharedPref.getInstance().setSharedValue(activity, AppConstants.reDeepLinkParamIsViaDeepLinkingLauncher, false);
            SharedPref.getInstance().setSharedValue(activity, AppConstants.reApiParamIsNewUser, false);
            SharedPref.getInstance().setSharedValue(activity, AppConstants.reSharedReferral, referrerObject.toString());
            SharedPref.getInstance().setSharedValue(activity, AppConstants.reSharedCampaignId, "");
            SharedPref.getInstance().setSharedValue(activity, AppConstants.reSharedMobileFriendlyUrl, "");

        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
    }

    /**
     * Current timestamp
     *
     * @param calendar
     * @return
     */
    static String getTime(Calendar calendar) {
        try {
            SimpleDateFormat simpleDateFormat = (SimpleDateFormat) getTimeStampFormat();
            return simpleDateFormat.format(calendar.getTime());
        } catch (Exception e) {
            return "0000-00-00'T'00:00:00";
        }

    }

    /**
     * Get App Crash Reasons
     *
     * @param mActivity
     * @param appCrashValue
     * @param screenObject
     * @throws JSONException
     */
    static void getAppCrashData(Context mActivity, String appCrashValue, JSONObject screenObject) throws JSONException {
        // App Crash
        try {

            if (appCrashValue != null) {
                JSONObject appCrash = new JSONObject();
                appCrash.put(AppConstants.reApiParamCrashText, appCrashValue);
                appCrash.put(AppConstants.reApiParamTimeStamp, getCurrentUTC());
                screenObject.put(AppConstants.reApiParamAppCrash, appCrash);
            }
        } catch (Exception e) {

        }
    }

    static String getUserDetails(Context appContext, MRegisterUser SDKUserRegister) {

        JSONObject userDetail;
        userDetail = new JSONObject();
        try {
            userDetail = new JSONObject();

            userDetail.put("AdvertiserID", SDKUserRegister.getAdId());
            userDetail.put("userUniqueId", SDKUserRegister.getUserUniqueId());


            if (ANAME)
                userDetail.put("name", SDKUserRegister.getName());
            else
                userDetail.put("name", "");

            if (APHONE)
                userDetail.put("phone", SDKUserRegister.getPhone());
            else
                userDetail.put("phone", "");

            if (AAGE)
                userDetail.put("age", SDKUserRegister.getAge());
            else
                userDetail.put("age", "");

            if (AEMPLOYED)
                userDetail.put("employed", SDKUserRegister.isEmployed());
            else
                userDetail.put("employed", "");

            if (AEDUCATION)
                userDetail.put("education", SDKUserRegister.getEducation());
            else
                userDetail.put("education", "");

            if (AMARRIED)
                userDetail.put("married", SDKUserRegister.isMarried());
            else
                userDetail.put("married", "");

            if (APHOTO)
                userDetail.put("profileUrl", SDKUserRegister.getProfileUrl());
            else
                userDetail.put("profileUrl", "");

            if (AGENDER)
                userDetail.put("gender", SDKUserRegister.getGender());
            else
                userDetail.put("gender", "");

            if (ADOP)
                userDetail.put("dob", SDKUserRegister.getDob());
            else
                userDetail.put("dob", "");


            if (!TextUtils.isEmpty(SDKUserRegister.getDeviceToken()) && !SDKUserRegister.getDeviceToken().equalsIgnoreCase("null"))
                userDetail.put("deviceToken", SDKUserRegister.getDeviceToken());
            else
                userDetail.put("deviceToken", getFCMToken(appContext));

            MDeviceData mDeviceData = new MDeviceData(appContext);
            userDetail.put("appId", mDeviceData.getAppId());
            userDetail.put("deviceOs", mDeviceData.getDeviceOs());
            userDetail.put("deviceIdfa", mDeviceData.getDeviceIdfa());
            userDetail.put("packageName", mDeviceData.getPackageName());
            userDetail.put("deviceOsVersion", mDeviceData.getDeviceOsVersion());
            userDetail.put("deviceManufacture", mDeviceData.getDeviceManufacture());
            userDetail.put("deviceModel", mDeviceData.getDeviceModel());
            userDetail.put("appVersionName", mDeviceData.getAppVersionName());
            userDetail.put("appVersionCode", mDeviceData.getAppVersionCode());
            userDetail.put("deviceType", mDeviceData.getDeviceType());
            if (AppInfo == null) {
                userDetail.put("androidAppInfo", getAppDetails(appContext.getApplicationContext()));
            } else {
                userDetail.put("androidAppInfo", AppInfo);
            }

            userDetail.put("sdkVersion", AppConstants.SDK_VERSION);
            userDetail.put("deviceId", mDeviceData.getDeviceId());

            userDetail.put(AppConstants.TENANT_ID, SharedPref.getInstance().getStringValue(appContext, AppConstants.TENANT_ID));
            userDetail.put(AppConstants.BUSINESS_SHORT_CODE, SharedPref.getInstance().getStringValue(appContext, AppConstants.BUSINESS_SHORT_CODE));
            userDetail.put(AppConstants.DEPARTMENT_ID, SharedPref.getInstance().getStringValue(appContext, AppConstants.DEPARTMENT_ID));
            userDetail.put(AppConstants.TENANT_SHORT_CODE, SharedPref.getInstance().getStringValue(appContext, AppConstants.TENANT_SHORT_CODE));

            SharedPref.getInstance().setSharedValue(appContext, "AdvertiserID", userDetail.optString("AdvertiserID"));
            SharedPref.getInstance().setSharedValue(appContext, "token", userDetail.optString("deviceToken"));

        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
        return userDetail.toString();
    }

    public static String getNetworkType(Context context) {
        try {
            TelephonyManager teleMan = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            int networkType = teleMan.getNetworkType();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return "1xRTT";
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return "CDMA";
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return "EDGE";
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return "eHRPD";
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return "EVDO rev. 0";
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return "EVDO rev. A";
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return "EVDO rev. B";
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return "GPRS";
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return "HSDPA";
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return "HSPA";
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "HSPA+";
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return "HSUPA";
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "iDen";
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "LTE";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return "UMTS";
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return "Unknown";
            }
            return "New type of network";
        } catch (Exception e) {
            return "";
        }

    }

    static String getBluetoothVersion() {
        return "";
    }

    static boolean isBluetoothEnabled(Context context) {

        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            // Bluetooth is not enabled :)
            // Bluetooth is enabled
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
            } else return mBluetoothAdapter.isEnabled();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    static synchronized void readScreenData(View view) {
        try {

            String result = "";


            if (view instanceof EditText)
                result = "" + ((EditText) view).getText();
            else if (view instanceof TextView)
                result = "" + ((TextView) view).getText();
            else if (view instanceof Spinner)
                result = "" + ((Spinner) view).getSelectedItem().toString();
            else
                result = "";


            if (!TextUtils.isEmpty(result))
                AppConstants.screenData = AppConstants.screenData + "||" + result;

            if (view instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) view;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    readScreenData(vg.getChildAt(i));
                }
            }
        } catch (Exception e) {

            ExceptionTracker.track(e);
        }

    }

    static String getAppUpdateTime(Context context) {
        PackageInfo packageInfo;
        System.out.println("getDisplayLanguage: " + Locale.getDefault().getDisplayLanguage());
        try {
            {
                packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                Date currentDate = new Date(packageInfo.lastUpdateTime);
                System.out.println("Update Date: " + currentDate);
                DateFormat df = getTimeStampFormat();
                return df.format(currentDate);
            }
        } catch (Exception e) {
            //should never happen
            return null;
        }
    }

    static boolean hasNFC(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            boolean isNFCSupported = pm.hasSystemFeature(PackageManager.FEATURE_NFC);
            return isNFCSupported;
        } catch (Exception e) {
            return false;
        }

    }

    static boolean isEnabledLocation(Context context) {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                return lm.isLocationEnabled();
            } else {
                int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                        Settings.Secure.LOCATION_MODE_OFF);
                return (mode != Settings.Secure.LOCATION_MODE_OFF);

            }
        } catch (Exception e) {
            return false;
        }


    }

    static String getLocationAccuracyType(Context context) {
        try {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return "";
            } else {
                return "";
            }

        } catch (Exception e) {
            return "";
        }
    }

    static String getScreenResolution(Context context) {
        try {
            if (width == 0) {
                width = Resources.getSystem().getDisplayMetrics().heightPixels;
                height = Resources.getSystem().getDisplayMetrics().widthPixels;
            }
            return +width + "X" + height;
        } catch (Exception e) {
            return "";
        }

    }

    static String getTimezone() {

        try {
            TimeZone timeZone = TimeZone.getDefault();
            return timeZone.getID();
        } catch (Exception e) {
            return "";
        }

    }

    static String getCurrency() {
        try {
            Locale defaultLocale = Locale.getDefault();
            Currency currency = Currency.getInstance(defaultLocale);
            return currency.getCurrencyCode();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getAppFirstInstallTime(Context context) {
        PackageInfo packageInfo;
        try {

            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            Date currentDate = new Date(packageInfo.firstInstallTime);
            //printing value of Date
            System.out.println("install Date: " + currentDate);
            DateFormat df = getTimeStampFormat();
            return df.format(currentDate);

        } catch (Exception e) {
            //should never happen
            return null;
        }
    }

    static JSONObject onLocationUpdate(Context appContext, double latitude, double longitude) {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            jsonObject.put("appId", SharedPref.getInstance().getStringValue(appContext, AppConstants.reSharedAPIKey));
            jsonObject.put("deviceId", Util.getDeviceId(appContext));
            jsonObject.put("userId", SharedPref.getInstance().getStringValue(appContext, AppConstants.reSharedUserId));
            jsonObject.put(AppConstants.TENANT_ID, SharedPref.getInstance().getStringValue(appContext, AppConstants.TENANT_ID));
            jsonObject.put(AppConstants.BUSINESS_SHORT_CODE, SharedPref.getInstance().getStringValue(appContext, AppConstants.BUSINESS_SHORT_CODE));
            jsonObject.put(AppConstants.DEPARTMENT_ID, SharedPref.getInstance().getStringValue(appContext, AppConstants.DEPARTMENT_ID));
            jsonObject.put(AppConstants.TENANT_SHORT_CODE, SharedPref.getInstance().getStringValue(appContext, AppConstants.TENANT_SHORT_CODE));
            jsonObject.put(AppConstants.PASSPORT_ID, SharedPref.getInstance().getStringValue(appContext, AppConstants.PASSPORT_ID));

            return jsonObject;

        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
        return new JSONObject();
    }

    static JSONObject getNotificationAmplifier(Context appContext) {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("appId", SharedPref.getInstance().getStringValue(appContext, AppConstants.reSharedAPIKey));
            jsonObject.put("deviceId", Util.getDeviceId(appContext));
            jsonObject.put("userId", SharedPref.getInstance().getStringValue(appContext, AppConstants.reSharedUserId));
            jsonObject.put(AppConstants.TENANT_ID, SharedPref.getInstance().getStringValue(appContext, AppConstants.TENANT_ID));
            jsonObject.put(AppConstants.BUSINESS_SHORT_CODE, SharedPref.getInstance().getStringValue(appContext, AppConstants.BUSINESS_SHORT_CODE));
            jsonObject.put(AppConstants.DEPARTMENT_ID, SharedPref.getInstance().getStringValue(appContext, AppConstants.DEPARTMENT_ID));
            jsonObject.put(AppConstants.TENANT_SHORT_CODE, SharedPref.getInstance().getStringValue(appContext, AppConstants.TENANT_SHORT_CODE));
            jsonObject.put(AppConstants.PASSPORT_ID, SharedPref.getInstance().getStringValue(appContext, AppConstants.PASSPORT_ID));


            return jsonObject;

        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
        return new JSONObject();
    }

    static String getIpAddress(Context context) {
        try {

            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            String ip = InetAddress.getLocalHost().getHostAddress();
            return ip;
        } catch (Exception e) {

        }
        return "";
    }

    static String getMobileNetworkOperator(Context context) {
        String operator = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                operator = telephonyManager.getNetworkOperatorName();
            }
        } catch (Exception e) {
        }

        return operator;
    }

    public static DateFormat getTimeStampFormat() {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            return df;
        } catch (Exception e) {
            return null;
        }

    }


    public static String getLauncherActivityName(Context context) {
        String activityName = "";
        try {
            final PackageManager pm = context.getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(context.getPackageName());
            List<ResolveInfo> activityList = pm.queryIntentActivities(intent, 0);
            if (activityList != null) {
                activityName = activityList.get(0).activityInfo.name;
            }
        } catch (Exception e) {

        }
        return activityName;
    }

    static void getCampaignDetails(Context context, String url) {

        try {
            URL url1 = new URL(url);
            Map<String, String> query_pairs = new LinkedHashMap<>();
            String query = url1.getQuery();
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                Log.e("" + URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));

                String key = "" + URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                if (!TextUtils.isEmpty(key)) {
                    if (key.equalsIgnoreCase("rid")) {
                        SharedPref.getInstance().setSharedValue(context, AppConstants.PASSPORT_ID, URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                    }
                    if (key.equalsIgnoreCase("utm_medium")) {
                        SharedPref.getInstance().setSharedValue(context, AppConstants.CAMPAIGN_CHANNEL, URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                    }
                    if (key.equalsIgnoreCase("utm_source")) {
                        SharedPref.getInstance().setSharedValue(context, AppConstants.CAMPAIGN_REFERRAL, URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                    }
                    if (key.equalsIgnoreCase("utm_campaign")) {
                        SharedPref.getInstance().setSharedValue(context, AppConstants.CAMPAIGN_NAME, URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                    }
                    if (key.equalsIgnoreCase("cid")) {
                        SharedPref.getInstance().setSharedValue(context, AppConstants.CAMPAIGN_ID, URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void rlog(String name, Boolean flag) {
        //Log.e("Config " + name, "" + flag);
    }

    static JSONObject getCampaignWithObject(Context context) {

        JSONObject obj = new JSONObject();
        try {
            obj.put(AppConstants.PASSPORT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.PASSPORT_ID));
            obj.put(AppConstants.CAMPAIGN_NAME, SharedPref.getInstance().getStringValue(context, AppConstants.CAMPAIGN_NAME));
            obj.put(AppConstants.CAMPAIGN_REFERRAL, SharedPref.getInstance().getStringValue(context, AppConstants.CAMPAIGN_REFERRAL));
            obj.put(AppConstants.CAMPAIGN_ID, SharedPref.getInstance().getStringValue(context, AppConstants.CAMPAIGN_ID));
            obj.put(AppConstants.CAMPAIGN_CHANNEL, SharedPref.getInstance().getStringValue(context, AppConstants.CAMPAIGN_CHANNEL));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    static String getEventName(String actionId) {

        String val = "";
        switch (Integer.parseInt(actionId)) {

            case 1:
                if (AppConstants.CNOTIFICATION_EXPIRED)
                    val = "NOTIFICATION EXPIRED";
                break;

            case 2:
                if (AppConstants.CNOTIFICATION_OPENED)
                    val = "NOTIFICATION_OPENED";
                break;

            case 3:
                if (AppConstants.CNOTIFICATION_DISMISSED)
                    val = "NOTIFICATION_DISMISSED";
                break;

            case 4:
                if (AppConstants.CNOTIFICATION_MAYBE_LATER)
                    val = "NOTIFICATION_MAY_BE_LATER";
                break;

            case 5:
                if (AppConstants.CNOTIFICATION_RECEIVED)
                    val = "NOTIFICATION_RECEIVED";
                break;

            case 6:
                if (AppConstants.CNOTIFICATION_CUSTOM_ACTION)
                    val = "NOTIFICATION_CUSTOM_ACTION";
                break;

            case 7:
                if (AppConstants.CNOTIFICATION_CUSTOM_ACTION)
                    val = "NOTIFICATION_CUSTOM_ACTION";
                break;

            case 8:
                if (AppConstants.CNOTIFICATION_UNSUBSCRIBED)
                    val = "NOTIFICATION_UNSUBSCRIBED";

            case 9:
                if (AppConstants.CNOTIFICATION_RESUME_JOURNEY)
                    val = "NOTIFICATION_RESUME_JOURNEY";
                break;

            default:
                val = "";
                break;
        }
        return val;

    }

    static boolean ruleAppInstalled(Context context) {
        try {
            Date appinstalled = Util.getStringToUTC(Util.getAppFirstInstallTime(context));
            Date currentDate = Util.getStringToUTC(Util.getCurrentUTC());
            long difference = currentDate.getTime() - appinstalled.getTime();
            long differenceDays = difference / (24 * 60 * 60 * 1000);
            return differenceDays == 0;
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * Check Internet Connection
     *
     * @return
     */
    boolean hasNetworkConnection(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert cm != null;
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    return true;
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // connected to the ic_mobile provider's data plan
                    return true;
                }
            }
        } catch (Exception e) {
            // ExceptionTracker.track(e);
        }
        return false;
    }

    /**
     * SDK App id Validation
     */
    String apiCallAPIValidation(Context appContext, String token) {

        MDeviceData mDeviceData = new MDeviceData(appContext);
        JSONObject userDetail;
        userDetail = new JSONObject();
        try {
            SharedPref.getInstance().setSharedValue(appContext, AppConstants.reSharedDatabaseDeviceId, Util.getDeviceId(appContext));
            userDetail.put("appId", mDeviceData.getAppId());
            userDetail.put("sdkVersion", AppConstants.SDK_VERSION);
            if (token != null) {
                userDetail.put("deviceToken", token);
            } else {
                userDetail.put("deviceToken", Util.getFCMToken(appContext));
            }
            userDetail.put("deviceId", mDeviceData.getDeviceId());
            userDetail.put("deviceOs", mDeviceData.getDeviceOs());
            userDetail.put("deviceIdfa", mDeviceData.getDeviceIdfa());
            userDetail.put("deviceType", mDeviceData.getDeviceType());
            userDetail.put("packageName", mDeviceData.getPackageName());
            userDetail.put("deviceOsVersion", mDeviceData.getDeviceOsVersion());
            userDetail.put("deviceManufacture", mDeviceData.getDeviceManufacture());
            userDetail.put("deviceModel", mDeviceData.getDeviceModel());
            userDetail.put("deviceIme", mDeviceData.getDeviceIme());
            userDetail.put("appVersionName", mDeviceData.getAppVersionName());
            userDetail.put("appVersionCode", mDeviceData.getAppVersionCode());
            userDetail.put("ipAddress", getIpAddress(appContext));
            userDetail.put("mobileNetworkOperator", getMobileNetworkOperator(appContext));
            userDetail.put("appInstallDate", getAppFirstInstallTime(appContext));
            userDetail.put("deviceSerialNumber", mDeviceData.getDeviceSerialNumber());
            userDetail.put("appUpdateDate", getAppUpdateTime(appContext));

            if (AppConstants.BLUETOOTH_CONNECTIVITY) {
                userDetail.put("isBluetoothEnabled", isBluetoothEnabled(appContext));
                userDetail.put("bluetoothVersion", getBluetoothVersion());
            } else {
                userDetail.put("isBluetoothEnabled", false);
                userDetail.put("bluetoothVersion", false);
                Log.e("------********", "********--------");
                Log.e("isBluetoothEnabled Tracking ", "Disabled");
                Log.e("------********", "********--------");
            }
            if (AppConstants.WIFI_CONNECTIVITY)
                userDetail.put("isWifiEnabled", isWifiEnabled(appContext));
            else {
                userDetail.put("isWifiEnabled", false);
                Log.e("------********", "********--------");
                Log.e("isWifiEnabled Tracking ", "Disabled");
                Log.e("------********", "********--------");
            }
            if (AppConstants.NFC_CONNECTIVITY)
                userDetail.put("hasNFC", hasNFC(appContext));
            else {
                userDetail.put("hasNFC", false);
                Log.e("------********", "********--------");
                Log.e("hasNFC Tracking ", "Disabled");
                Log.e("------********", "********--------");
            }
            userDetail.put("isNotificationEnabled", NotificationManagerCompat.from(appContext).areNotificationsEnabled());
            userDetail.put("phoneRadioType", "");
            userDetail.put("networkType", getNetworkType(appContext));
            userDetail.put("locationAccuracyType", "");
            userDetail.put("screenResolution", getScreenResolution(appContext));
            userDetail.put("timezone", getTimezone());
            userDetail.put("currency", getCurrency());
            userDetail.put("region", "");
            userDetail.put("location", "");
            userDetail.put("appLanguage", Locale.getDefault().getDisplayLanguage());
            userDetail.put("deviceLanguage", Resources.getSystem().getConfiguration().locale.getDisplayLanguage());
            userDetail.put("deviceCountry", Resources.getSystem().getConfiguration().locale.getDisplayCountry());
            userDetail.put("configEmail", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userDetail.toString();
    }

    boolean isWifiEnabled(Context context) {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            return mWifi.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    void setConfig(Context context) {
        /*try {
            String config = SharedPref.getInstance().getStringValue(context, "mobileConfig");
            if (!TextUtils.isEmpty(config)) {
                JSONObject jsonObject = new JSONObject(config);

                JSONArray featuresConfig = jsonObject.optJSONArray("mobileconfig");
                if (featuresConfig != null) {
                    for (int i = 0; i < featuresConfig.length(); i++) {

                        JSONObject object = featuresConfig.getJSONObject(i);
                        String value = object.optString("apiName", "");
                        boolean flag = object.optBoolean("flag", true);
                        rlog(" API : " + value, flag);

                        switch (value) {

                            case "SDK":
                                AppConstants.SDK = flag;
                                break;
                            case "Location tracking":
                                AppConstants.LOCATION_TRACKING = flag;
                                break;
                            case "Screen tracking":
                                AppConstants.SCREEN_TRACKING = flag;
                                break;
                            case "Bluetooth connectivity":
                                AppConstants.BLUETOOTH_CONNECTIVITY = flag;
                                break;
                            case "Mobile network connectivity":
                                AppConstants.NETWORK_CONNECTIVITY = flag;
                                break;
                            case "WiFi connectivity":
                                AppConstants.WIFI_CONNECTIVITY = flag;
                                break;
                            case "NFC connectivity":
                                AppConstants.NFC_CONNECTIVITY = flag;
                                break;
                            case "Custom event":
                                AppConstants.CUSTOM_EVENT = flag;
                                break;
                            case "Field tracking":
                                AppConstants.FIELD_TRACKING = flag;
                                break;
                            case "User register":
                                AppConstants.USER_REGISTER = flag;
                                break;
                            case "In-page content customization":
                                AppConstants.IN_PAGE_CONTENT_INJECTION = flag;
                                break;
                            case "In-app/Inbox for DND":
                                AppConstants.NOTIFICATION_DND_DISABLED = flag;
                                break;
                            case "App Crash":
                                AppConstants.APP_CRASH = flag;
                                break;
                            default:
                                break;
                        }

                    }
                }
                JSONArray eventconfig = jsonObject.optJSONArray("eventconfig");
                if (eventconfig != null) {
                    for (int i = 0; i < eventconfig.length(); i++) {

                        JSONObject object = eventconfig.getJSONObject(i);
                        String value = object.optString("eventname", "");
                        rlog(" Event : " + value, true);

                        switch (value) {

                            case "Notification opened":
                                AppConstants.CNOTIFICATION_OPENED = true;
                                break;
                            case "App crashed":
                                AppConstants.CAPP_CRASHED = true;
                                break;
                            case "App first open":
                                AppConstants.CFIRST_APP_OPEN = true;
                                break;
                            case "Notification expired":
                                AppConstants.CNOTIFICATION_EXPIRED = true;
                                break;
                            case "Notification maybe later":
                                AppConstants.CNOTIFICATION_MAYBE_LATER = true;
                                break;
                            case "Notification custom actions":
                                AppConstants.CNOTIFICATION_CUSTOM_ACTION = true;
                                break;
                            case "Notification received":
                                AppConstants.CNOTIFICATION_RECEIVED = true;
                                break;
                            case "Notification dismissed":
                                AppConstants.CNOTIFICATION_DISMISSED = true;
                                break;
                            case "Notification unsubscribed":
                                AppConstants.CNOTIFICATION_UNSUBSCRIBED = true;
                                break;
                            case "Notification resume journey":
                                AppConstants.CNOTIFICATION_RESUME_JOURNEY = true;
                                break;
                            case "Resume journey":
                                AppConstants.CRESUME_JOURNEY = true;
                                break;

                            default:
                                break;
                        }

                    }
                }
                JSONArray attributeConfig = jsonObject.optJSONArray("attributeconfig");
                if (attributeConfig != null) {
                    for (int i = 0; i < attributeConfig.length(); i++) {

                        JSONObject object = attributeConfig.getJSONObject(i);
                        String value = object.optString("attributename", "");
                        rlog(" Attribute : " + value, true);
                        switch (value) {

                            case "Email":
                                AEMAIL = true;
                                break;
                            case "Name":
                                ANAME = true;
                                break;
                            case "Gender":
                                AGENDER = true;
                                break;
                            case "Photo":
                                APHOTO = true;
                                break;
                            case "Employed":
                                AEMPLOYED = true;
                                break;
                            case "Education":
                                AEDUCATION = true;
                                break;
                            case "Married":
                                AMARRIED = true;
                                break;
                            case "Age":
                                AAGE = true;
                                break;
                            case "Dob":
                                ADOP = true;
                                break;
                            case "Phone":
                                APHONE = true;
                                break;

                            default:
                                break;
                        }

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        new SetConFiguration(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
    }

    private class SetConFiguration extends AsyncTask<String, String, String> {

        Context context;

        SetConFiguration(Context context) {
            this.context = context;
        }

        protected String doInBackground(String... urls) {
            try {
                String config = SharedPref.getInstance().getStringValue(context, "mobileConfig");
                Log.e("config",config);
                if (!TextUtils.isEmpty(config)) {
                    JSONObject jsonObject = new JSONObject(config);

                    JSONArray featuresConfig = jsonObject.optJSONArray("mobileconfig");
                    if (featuresConfig != null) {
                        for (int i = 0; i < featuresConfig.length(); i++) {

                            JSONObject object = featuresConfig.getJSONObject(i);
                            String value = object.optString("apiName", "");
                            boolean flag = object.optBoolean("flag", true);
                            rlog(" API : " + value, flag);

                            switch (value) {

                                case "SDK":
                                    AppConstants.SDK = flag;
                                    break;
                                case "Location tracking":
                                    AppConstants.LOCATION_TRACKING = flag;
                                    break;
                                case "Screen tracking":
                                    AppConstants.SCREEN_TRACKING = flag;
                                    break;
                                case "Bluetooth connectivity":
                                    AppConstants.BLUETOOTH_CONNECTIVITY = flag;
                                    break;
                                case "Mobile network connectivity":
                                    AppConstants.NETWORK_CONNECTIVITY = flag;
                                    break;
                                case "WiFi connectivity":
                                    AppConstants.WIFI_CONNECTIVITY = flag;
                                    break;
                                case "NFC connectivity":
                                    AppConstants.NFC_CONNECTIVITY = flag;
                                    break;
                                case "Custom event":
                                    AppConstants.CUSTOM_EVENT = flag;
                                    break;
                                case "Field tracking":
                                    AppConstants.FIELD_TRACKING = flag;
                                    break;
                                case "User register":
                                    AppConstants.USER_REGISTER = flag;
                                    break;
                                case "In-page content customization":
                                    AppConstants.IN_PAGE_CONTENT_INJECTION = flag;
                                    break;
                                case "In-app/Inbox for DND":
                                    AppConstants.NOTIFICATION_DND_DISABLED = flag;
                                    break;
                                case "App Crash":
                                    AppConstants.APP_CRASH = flag;
                                    break;
                                default:
                                    break;
                            }

                        }
                    }
                    JSONArray eventconfig = jsonObject.optJSONArray("eventconfig");
                    if (eventconfig != null) {
                        for (int i = 0; i < eventconfig.length(); i++) {

                            JSONObject object = eventconfig.getJSONObject(i);
                            String value = object.optString("eventname", "");
                            rlog(" Event : " + value, true);

                            switch (value) {

                                case "Notification opened":
                                    AppConstants.CNOTIFICATION_OPENED = true;
                                    break;
                                case "App crashed":
                                    AppConstants.CAPP_CRASHED = true;
                                    break;
                                case "App first open":
                                    AppConstants.CFIRST_APP_OPEN = true;
                                    break;
                                case "Notification expired":
                                    AppConstants.CNOTIFICATION_EXPIRED = true;
                                    break;
                                case "Notification maybe later":
                                    AppConstants.CNOTIFICATION_MAYBE_LATER = true;
                                    break;
                                case "Notification custom actions":
                                    AppConstants.CNOTIFICATION_CUSTOM_ACTION = true;
                                    break;
                                case "Notification received":
                                    AppConstants.CNOTIFICATION_RECEIVED = true;
                                    break;
                                case "Notification dismissed":
                                    AppConstants.CNOTIFICATION_DISMISSED = true;
                                    break;
                                case "Notification unsubscribed":
                                    AppConstants.CNOTIFICATION_UNSUBSCRIBED = true;
                                    break;
                                case "Notification resume journey":
                                    AppConstants.CNOTIFICATION_RESUME_JOURNEY = true;
                                    break;
                                case "Resume journey":
                                    AppConstants.CRESUME_JOURNEY = true;
                                    break;

                                default:
                                    break;
                            }

                        }
                    }
                    JSONArray attributeConfig = jsonObject.optJSONArray("attributeconfig");
                    if (attributeConfig != null) {
                        for (int i = 0; i < attributeConfig.length(); i++) {

                            JSONObject object = attributeConfig.getJSONObject(i);
                            String value = object.optString("attributename", "");
                            rlog(" Attribute : " + value, true);
                            switch (value) {

                                case "Email":
                                    AEMAIL = true;
                                    break;
                                case "Name":
                                    ANAME = true;
                                    break;
                                case "Gender":
                                    AGENDER = true;
                                    break;
                                case "Photo":
                                    APHOTO = true;
                                    break;
                                case "Employed":
                                    AEMPLOYED = true;
                                    break;
                                case "Education":
                                    AEDUCATION = true;
                                    break;
                                case "Married":
                                    AMARRIED = true;
                                    break;
                                case "Age":
                                    AAGE = true;
                                    break;
                                case "Dob":
                                    ADOP = true;
                                    break;
                                case "Phone":
                                    APHONE = true;
                                    break;

                                default:
                                    break;
                            }

                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }


    }


}
