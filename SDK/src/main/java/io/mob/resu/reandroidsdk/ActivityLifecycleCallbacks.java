package io.mob.resu.reandroidsdk;

import static android.content.Context.SENSOR_SERVICE;
import static io.mob.resu.reandroidsdk.AppConstants.CURRENT_ACTIVITY_NAME;
import static io.mob.resu.reandroidsdk.AppConstants.CURRENT_FRAGMENT_NAME;
import static io.mob.resu.reandroidsdk.AppConstants.FIELD_TRACKING;
import static io.mob.resu.reandroidsdk.AppConstants.HybridScreenUrl;
import static io.mob.resu.reandroidsdk.AppConstants.LAST_ACTIVITY_NAME;
import static io.mob.resu.reandroidsdk.AppConstants.LAST_APP_OPENED;
import static io.mob.resu.reandroidsdk.AppConstants.LAST_FRAGMENT_NAME;
import static io.mob.resu.reandroidsdk.AppConstants.LAST_VISITED_ACTIVITY;
import static io.mob.resu.reandroidsdk.AppConstants.appOpenTime;
import static io.mob.resu.reandroidsdk.AppConstants.hybridViewsJson;
import static io.mob.resu.reandroidsdk.AppConstants.newError;
import static io.mob.resu.reandroidsdk.AppConstants.socketUrl;
import static io.mob.resu.reandroidsdk.Util.deepLinkDataReset;
import static io.mob.resu.reandroidsdk.Util.getNotificationCurrentUTC;
import static io.mob.resu.reandroidsdk.error.Util.isCheckBox;
import static io.mob.resu.reandroidsdk.error.Util.isEditText;
import static io.mob.resu.reandroidsdk.error.Util.isRadioButton;
import static io.mob.resu.reandroidsdk.error.Util.isRatingBar;
import static io.mob.resu.reandroidsdk.error.Util.isSeekBar;
import static io.mob.resu.reandroidsdk.error.Util.isSpinner;
import static io.mob.resu.reandroidsdk.error.Util.isSwitch;
import static io.mob.resu.reandroidsdk.error.Util.isToggleButton;
import static io.mob.resu.reandroidsdk.error.Util.isViewGroup;
import static io.mob.resu.reandroidsdk.error.Util.isWebView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;
import io.mob.resu.reandroidsdk.error.Log;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

class ActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks, ShakeDetector.OnShakeListener {
    static final String socketClientMessageImage = "client-message-image";
    private static final String socketMarketerAcceptance = "acceptance-message-from-marketer";
    private final static Handler handler = new Handler();
    private final static Handler ReceiverHandler = new Handler();
    static Activity mActivity;
    private static WindowChangeListener windowChangeListener;
    private static WindowChangeListener oldWindowChangeListener;
    private static ArrayList<JSONObject> viewsJson = new ArrayList<>();
    private static Calendar oldCalendar = Calendar.getInstance();
    private static Calendar sCalendar = Calendar.getInstance();
    private static Boolean isConnected = false;
    private static ShakeDetector shakeDetector;
    private static SensorManager sensorManager;
    private final String TAG = this.getClass().getSimpleName();
    private final String android = "Android";
    private final String deviceId = "deviceId";
    private final String socketHandShakeRequest = "client-handshake-request";
    private final String socketServerDisConnect = "message-server-disconnect";
    private final String socketServerHandShakeResponse = "client-handshake-response";
    private final String mainScreenName = "mainScreenName";
    /**
     * Socket onConnectError
     */
    private final Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(" Socket onConnectError ", "Activity");
                    }
                });
            } catch (Exception e) {
                //
            }
        }
    };

    /**
     * Socket onDisconnect
     */
    private final Emitter.Listener onDisconnect = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            try {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isConnected = false;
                    }
                });

            } catch (Exception e) {
                //
            }
        }
    };

    private boolean isFlag = false;
    private String randomNumber = randomNumber();
    private Dialog dialog;
    private boolean shakeFlag = true;
    private Bitmap ScreenBitmap;
    private int count = 0;
    private String newActivityName;
    private String appBackgroundActivity;
    private Socket mSocket;
    private Boolean abTestEnabled = false;

    /**
     * Socket Int
     */ {
        try {
            mSocket = IO.socket(socketUrl);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendAuthCode() {
        try {
            if (isConnected) {
                try {
                    JSONObject jobj = new JSONObject();
                    jobj.put("appId", android + isTablet(mActivity).replace(" ", "") + SharedPref.getInstance().getStringValue(mActivity, AppConstants.reSharedAPIKey));
                    // jobj.put("appId", "AndroidAndroidPhonea27794d9-c4e6-45f9-815f-5470224f8f2a");
                    jobj.put("deviceId", Util.getDeviceId(mActivity));
                    jobj.put("oAuthCode", randomNumber);
                    jobj.put("deviceName", Build.DEVICE);
                    jobj.put("deviceModel", Build.MODEL);
                    jobj.put("deviceManufacture", Build.MANUFACTURER);
                    mSocket.emit("client-auth-code", jobj);

                    Log.e(" Socket Connected ", jobj.getString("appId"));
                } catch (Exception e) {
                    ExceptionTracker.track(e);
                }
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 2000);
            } else {
                isFlag = true;
                try {
                    handler.removeCallbacks(runnable);
                } catch (Exception e) {
                    ExceptionTracker.track(e);
                }

            }
        } catch (Exception e) {
            //
        }
    }

    private void hyBirdCallBacks(Activity activity, String message) {

        try {
            Intent intent = new Intent("SocketCallBacks");
            intent.putExtra("state", message);
            LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
        } catch (Exception e) {
            Log.e("hyBirdCallBacks", "" + e.getMessage());
        }

    }

    private String randomNumber() {
        try {
            Random r = new Random();
            int numbers = 100000 + (int) (r.nextFloat() * 899900);
            return String.valueOf(numbers);
        } catch (Exception e) {
            return "" + System.currentTimeMillis();
        }


    }

    private void showAuthDialog(final Activity context) {
        try {
            context.runOnUiThread(new Runnable() {
                public void run() {

                    try {
                        if (dialog != null) {
                            if (dialog.isShowing())
                                dialog.dismiss();
                        }
                        if (!abTestEnabled) {
                            dialog = new Dialog(context, R.style.AppCompatAlertDialogStyle);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setCancelable(false);
                            dialog.setContentView(R.layout.dialog_feedback);
                            final EditText comments = dialog.findViewById(R.id.ed_comments);
                            final Button cancel = dialog.findViewById(R.id.btn_cancel);
                            Button submit = dialog.findViewById(R.id.btn_submit);
                            comments.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    if (comments.getText().toString().trim().length() == 6) {
                                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(
                                                cancel.getWindowToken(), 0);
                                    }
                                }
                            });


                            submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        if (randomNumber.equals(comments.getText().toString())) {
                                            sendSocketData();
                                            hyBirdCallBacks(mActivity, "start");
                                            if (isConnected)
                                                mSocket.off(socketMarketerAcceptance, onMarketerAcceptance);
                                            dialog.dismiss();
                                        } else {
                                            Toast.makeText(mActivity, "In valid code", Toast.LENGTH_SHORT).show();
                                            comments.setText("");
                                        }
                                    } catch (Exception e) {
                                        ExceptionTracker.track(e);
                                    }
                                }
                            });

                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        handler.removeCallbacks(runnable);
                                        if (!abTestEnabled)
                                            disableSocket();
                                        dialog.dismiss();
                                    } catch (Exception e) {
                                        ExceptionTracker.track(e);
                                    }
                                }
                            });

                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    Log.e("Dialog dismissed", "dismissed");
                                }
                            });

                            dialog.show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }

    }

    private int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Activity onActivityCreated
     *
     * @param activity
     * @param bundle
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        try {

            mActivity = activity;
            AppLifecyclePresenter.getInstance().instantiate(activity);

            LAST_ACTIVITY_NAME = CURRENT_ACTIVITY_NAME;

            CURRENT_ACTIVITY_NAME = activity.getClass().getSimpleName();
            appBackgroundActivity = activity.getClass().getSimpleName();
            if (Util.itHasFragment(mActivity)) {
                EnabledContentPublisher(mActivity);
            } else {
                LAST_FRAGMENT_NAME = "";
                CURRENT_FRAGMENT_NAME = "";
            }
            Log.e("Activity Created : ", activity.getClass().getSimpleName());
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }

    }

    /**
     * Activity  onActivityStarted
     *
     * @param activity
     */
    @Override
    public void onActivityStarted(final Activity activity) {

        try {
            AppRuleListener.context = activity;
            ReAndroidSDK.appContext = activity;

            Log.e("Activity Started : ", activity.getClass().getSimpleName());
            dialog = null;
            mActivity = activity;
            newActivityName = activity.getClass().getSimpleName();

            SharedPref.getInstance().setSharedValue(activity, AppConstants.CURRENT_ACTIVITY, newActivityName);
            AppConstants.oldError = newError;
            if (AppConstants.oldError == null)
                AppConstants.oldError = new ArrayList<>();
            newError = new ArrayList<>();
            oldCalendar = sCalendar;
            sCalendar = Calendar.getInstance();
            onWindowChangeListener(activity);

            // if (AppConstants.FILELD_TRACKING)
            //shakeInit();
            new ShakeShake(activity).execute();
          /*  else {
                Log.e("------********", "********--------");
                Log.e("Field Tracking ", "Disabled");
                Log.e("------********", "********--------");
            }*/
            AppLifecyclePresenter.getInstance().onSessionStartActivity(activity, activity.getClass().getSimpleName());
            SessionTimer.getInstance().startTimer(activity);

           /* if (!Util.itHasFragment(activity)) {
                AppRuleListener.getInstance().(activity);
            }*/
            ReAndroidSDK.onPageChangeListener.onPageChanged(activity.getClass().getSimpleName(), "No fragment available");

        } catch (Exception e) {
            ExceptionTracker.track(e);
        }

    }

    /**
     * Activity onActivityResumed
     *
     * @param activity
     */
    @Override
    public void onActivityResumed(Activity activity) {
        try {
            oldCalendar = Calendar.getInstance();
            sCalendar = Calendar.getInstance();
            AppLifecyclePresenter.getInstance().campaignTracker(activity, null);
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }


    }

    /**
     * Activity onActivityPaused
     *
     * @param activity
     */
    @Override
    public void onActivityPaused(final Activity activity) {
        //AppRuleListener.getInstance().processAppInBackground(mActivity);
        try {
            new CountDownTimer(100, 1000) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    try {
                        Log.e("Activity onActivityPaused : ", activity.getClass().getSimpleName());
                        if (appBackgroundActivity.equalsIgnoreCase(activity.getClass().getSimpleName())) {
                            Log.e("Activity onActivityPaused : ", activity.getClass().getSimpleName());
                            AppRuleListener.getInstance().processAppInBackground(mActivity);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        } catch (Exception e) {

        }

    }    /**
     * Socket interval screen update
     */
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {

            try {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isFlag = true;
                        Log.e("WindowChangeListener", "Activity");
                        if (abTestEnabled) {
                            sendSocketData();
                        } else {
                            handler.removeCallbacks(runnable);
                            sendAuthCode();
                        }
                    }
                });
            } catch (Exception e) {
//
            }
        }
    };

    /**
     * Activity onActivityStopped
     *
     * @param activity
     */
    @Override
    public void onActivityStopped(final Activity activity) {
        try {
            if (Util.itHasFragment(activity))
                AppLifecyclePresenter.getInstance().onSessionStop(activity, oldCalendar, Calendar.getInstance(), activity.getClass().getSimpleName(), null, null);
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
    }

    /**
     * onActivitySaveInstanceState
     *
     * @param activity
     * @param bundle
     */
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    /**
     * onActivityDestroyed
     *
     * @param activity
     */
    @Override
    public void onActivityDestroyed(final Activity activity) {
        try {

            if (Util.isAppIsInBackground(mActivity)) {
                if (activity.getClass().getSimpleName().equalsIgnoreCase(newActivityName)) {
                    SharedPref.getInstance().setSharedValue(activity, AppConstants.reResumeJourney, "");
                    AppRuleListener.getInstance().processExit(activity);
                    SessionTimer.getInstance().stopTimer();

                    deepLinkDataReset(activity);
                    SharedPref.getInstance().setSharedValue(activity, LAST_APP_OPENED, getNotificationCurrentUTC());
                    SharedPref.getInstance().setSharedValue(activity, LAST_VISITED_ACTIVITY, activity.getLocalClassName());
                    Log.e(TAG, "App Terminated");
                    Log.e("appOpenTime", "" + appOpenTime);
                    Log.e("CloseTime", "" + Util.getCurrentUTC());

                    String session = Util.appSession(Util.getStringToUTC(appOpenTime), Util.getStringToUTC(Util.getCurrentUTC()));
                    Long sessionTime = Long.parseLong(session);
                    SharedPref.getInstance().setSharedValue(activity, "lastSessionTime", "" + sessionTime);
                    try {
                       /* int count = new DataBase(activity).getData(DataBase.Table.SCREENS_TABLE).size();
                        if (count >= 3) {*/
                        new OfflineScreenTrack(activity, DataNetworkHandler.getInstance()).execute();
                        //}
                        if (sensorManager != null)
                            sensorManager.unregisterListener(shakeDetector);

                    } catch (Exception f) {
                        f.printStackTrace();
                    }

                } else {
                    Log.e(TAG, "App Continue");
                }

            }
            if (AppLifecyclePresenter.fragmentLifecycleCallbacks != null) {
                FragmentLifecycleCallbacks.fragment = null;
                FragmentLifecycleCallbacks.v = null;
                FragmentLifecycleCallbacks.view = null;
            }
        } catch (
                Exception e) {
            ExceptionTracker.track(e);
        }
        SessionTimer.getInstance().setLastVisit(activity, "");
    }

    /**
     * onWindowChangeListener
     *
     * @param activity
     */
    private void onWindowChangeListener(Activity activity) {
        try {
            oldWindowChangeListener = windowChangeListener;
            windowChangeListener = new WindowChangeListener();
            if (oldWindowChangeListener == null) {
                oldWindowChangeListener = windowChangeListener;
            }
            activity.getWindow().getDecorView().getRootView().getViewTreeObserver().addOnGlobalLayoutListener(windowChangeListener);
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
    }

    /**
     * Shaking Sensor
     * shakeInit
     */
    private void shakeInit() {
        try {
            shakeDetector = new ShakeDetector();
            shakeDetector.setOnShakeListener(this);
            if (sensorManager != null) {
                sensorManager.registerListener(shakeDetector,
                        sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                        SensorManager.SENSOR_DELAY_FASTEST);
            }


        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
    }

    /**
     * App Crash Data Handler
     *
     * @param appCrash
     */
    public void appCrashHandle(String appCrash) {
        try {
            deepLinkDataReset(mActivity);
            AppLifecyclePresenter.getInstance().onSessionStop(mActivity, oldCalendar, Calendar.getInstance(), mActivity.getClass().getSimpleName(), null, appCrash);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * enableSocket
     */
    private void enableSocket() {
        try {
            // if (AppConstants.FILELD_TRACKING) {
            Log.e("enableSocket", "Called");
            handler.removeCallbacks(runnable);
            initializeSocket();
            oldWindowChangeListener = windowChangeListener;
            windowChangeListener = new WindowChangeListener();
           /* } else {
                Log.e("------********", "********--------");
                Log.e("Config FILELD TRACKING", "Disabled");
                Log.e("------********", "********--------");
            }*/
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }

    }

    /**
     * initializeSocket
     */
    private void initializeSocket() {

        try {
            mSocket = getSocket();
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on(socketServerHandShakeResponse, clientHandshakeResponse);
            mSocket.on(socketMarketerAcceptance, onMarketerAcceptance);
            mSocket.on(socketServerDisConnect, onDisconnectServer);
            mSocket.connect();
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }


    }

    /**
     * getSocket
     *
     * @return
     */
    private Socket getSocket() {
        return mSocket;
    }

    /**
     * sendSocketData
     * Field track view Json
     */
    private void sendSocketData() {
        abTestEnabled = true;
        String manufacture = "manufacture";
        String deviceModel = "deviceModel";
        String deviceType = "deviceType";
        String deviceOs = "deviceOs";
        String controls = "controls";
        try {
            View v1;
            viewsJson = new ArrayList<>();
            count = 0;
            String socketClientMessageData = "client-message-data";
            if (!AppConstants.isCordova) {
                // Native and and React-Native Platforms
                FragmentLifecycleCallbacks fragmentLy = AppLifecyclePresenter.fragmentLifecycleCallbacks;
                if (fragmentLy != null) {
                    if (fragmentLy.isOldDialogFragment && FragmentLifecycleCallbacks.view != null) {
                        printTree(FragmentLifecycleCallbacks.view);
                    } else {
                        fragmentLy.isOldDialogFragment = false;
                        printTree(mActivity.getWindow().getDecorView().getRootView());
                    }
                } else {
                    printTree(mActivity.getWindow().getDecorView().getRootView());
                }

                if (viewsJson.size() > 0) {
                    if (isConnected) {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put(mainScreenName, mActivity.getClass().getSimpleName());
                        jsonObject1.put(manufacture, Build.MANUFACTURER);
                        jsonObject1.put(deviceModel, Build.MODEL);
                        jsonObject1.put(deviceType, isTablet(mActivity));
                        jsonObject1.put(deviceOs, android);
                        jsonObject1.put("appId", android + isTablet(mActivity).replace(" ", "") + SharedPref.getInstance().getStringValue(mActivity, AppConstants.reSharedAPIKey));
                        jsonObject1.put(deviceId, Util.getDeviceId(mActivity));
                        jsonObject1.put(controls, new JSONArray(viewsJson).toString());
                        String result = new JSONArray(viewsJson).toString();
                        mSocket.emit(socketClientMessageData, jsonObject1);

                    }
                }
            } else {
                // Cordova screen view json
                if (hybridViewsJson != null && hybridViewsJson.length() > 0) {
                    if (isConnected) {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put(mainScreenName, HybridScreenUrl);
                        jsonObject1.put(manufacture, Build.MANUFACTURER);
                        jsonObject1.put(deviceModel, Build.MODEL);
                        jsonObject1.put(deviceType, isTablet(mActivity));
                        jsonObject1.put(deviceOs, android);
                        jsonObject1.put("appId", android + isTablet(mActivity).replace(" ", "") + SharedPref.getInstance().getStringValue(mActivity, AppConstants.reSharedAPIKey));
                        jsonObject1.put(deviceId, Util.getDeviceId(mActivity));
                        jsonObject1.put(controls, hybridViewsJson.toString());
                        mSocket.emit(socketClientMessageData, jsonObject1);
                    }
                }

            }
            FragmentLifecycleCallbacks fragmentLy = AppLifecyclePresenter.fragmentLifecycleCallbacks;

            // Dialog fragment view capture
            if (fragmentLy != null) {
                if (fragmentLy.isOldDialogFragment && FragmentLifecycleCallbacks.view != null) {
                    v1 = FragmentLifecycleCallbacks.view;
                } else {
                    v1 = mActivity.getWindow().getDecorView();
                }
            } else {
                v1 = mActivity.getWindow().getDecorView();
            }


            v1.setDrawingCacheEnabled(true);
            if (ScreenBitmap != null && !ScreenBitmap.isRecycled())
                ScreenBitmap.recycle();

            ScreenBitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            if (fragmentLy != null) {
                new CaptureTree().CaptureTreeImage(fragmentLy.isOldDialogFragment, isConnected, abTestEnabled, mActivity, mSocket, handler, runnable, ScreenBitmap);
            } else {
                new CaptureTree().CaptureTreeImage(false, isConnected, abTestEnabled, mActivity, mSocket, handler, runnable, ScreenBitmap);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }    /**
     * Socket clientHandshakeResponse
     */
    private final Emitter.Listener clientHandshakeResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            try {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            Log.e("Socket clientHandshakeResponse ", "" + args[0]);


                            JSONObject data = new JSONObject((String) args[0]);
                            Log.e("Socket clientHandshakeResponse ", "" + data.optString("isConnected"));
                            if (Boolean.parseBoolean(data.optString("isConnected"))) {
                                if (abTestEnabled) {
                                    sendSocketData();
                                } else {
                                    sendAuthCode();
                                }
                            } else {
                                Toast.makeText(mActivity, "Please try again later!", Toast.LENGTH_SHORT).show();
                                disableSocket();
                                shakeFlag = false;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                });

            } catch (Exception e) {

            }

        }
    };

    /**
     * disableSocket
     */
    void disableSocket() {
        try {
            shakeFlag = true;
            abTestEnabled = false;
            isConnected = false;
            mSocket.disconnect();
            hyBirdCallBacks(mActivity, "stop");
            handler.removeCallbacks(runnable);
            mSocket.off(Socket.EVENT_CONNECT, onConnect);
            mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.off(socketServerHandShakeResponse, clientHandshakeResponse);
            mSocket.off(socketServerDisConnect, onDisconnectServer);
            mSocket.off(socketMarketerAcceptance, onMarketerAcceptance);
            SessionTimer.getInstance().stopTimer();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onShake(int count) {
        try {
            if (shakeFlag) {
                if (!abTestEnabled && !isConnected) {
                    shakeFlag = false;
                    boolean isWhiteList = SharedPref.getInstance().getBooleanValue(mActivity, "IsWhiteListed");
                    // if (isWhiteList) {
                    Log.e("Device", "WhiteListed Sucess");
                    enableSocket();
                    // }

                }
            }
            if (SharedPref.getInstance().getBooleanValue(mActivity, "logFlag")) {
                AppConstants.LogFlag = false;
                SharedPref.getInstance().setSharedValue(mActivity, "logFlag", false);
            } else {
                AppConstants.LogFlag = true;
                SharedPref.getInstance().setSharedValue(mActivity, "logFlag", true);
            }

        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
    }

    /**
     * Screen Structure
     *
     * @param view
     */
    @SuppressLint("ResourceType")
    private synchronized void printTree(View view) {
        String viewUniqueId = "view_unique_id_";
        String id = "id";
        String isShow = "isShow";
        String viewId = "viewId";
        String screenName = "screenName";
        String viewType = "viewType";
        String category = "category";
        String width = "width";
        String height = "height";
        String left = "left";
        String top = "top";
        String translationX = "translationX";
        String translationY = "translationY";
        String isWebView = "isWebView";
        String scrollX = "scrollX";
        String scrollY = "scrollY";
        String subviews = "subviews";

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(category, view.getClass().getSimpleName());

            if (view.getVisibility() == View.GONE)
                return;

            jsonObject.put(isShow, false);

            // if view has id
            if (view.getId() <= -1) {
                view.setId(mActivity.getResources().getIdentifier(viewUniqueId + count, id, mActivity.getPackageName()));
                jsonObject.put(isShow, false);
            } else {

                // if view has click listener
                if (view.hasOnClickListeners()) {
                    jsonObject.put(isShow, true);
                    jsonObject.put(category, "AppCompatTextView");
                    jsonObject.put(viewType, "Others");
                }

                // view type finder
                if (isEditText(view)) {
                    jsonObject.put(viewType, "EditText");
                    jsonObject.put(isShow, ((EditText) view).getInputType() != 129 && ((EditText) view).getInputType() != 18 && ((EditText) view).getInputType() != 145 && ((EditText) view).getInputType() != 225);
                } else if (isRadioButton(view) || isCheckBox(view) || isToggleButton(view) || isSwitch(view) || isRatingBar(view) || isSeekBar(view) || isSpinner(view)) {
                    jsonObject.put(isShow, true);
                    jsonObject.put(viewType, "Value");
                }

            }

            try {
                // react-native specific
                if (AppConstants.isReactNative) {
                    jsonObject.put(viewId, "" + view.getId());
                } else {
                    String[] strings = view.getResources().getResourceName(view.getId()).split("/");
                    if (strings[1].startsWith("view_unique")) {
                        jsonObject.put(isShow, false);
                    }
                    jsonObject.put(viewId, strings[1]);
                }
            } catch (Exception e) {
                jsonObject.put(viewId, mActivity.getResources().getIdentifier(viewUniqueId + count, id, mActivity.getPackageName()));
                jsonObject.put(isShow, false);
            }
            count = count + 1;


            Object object = view.getTag();
            if (object != null) {
                if (object instanceof String) {
                    jsonObject.put(screenName, "" + object);
                } else {
                    jsonObject.put(screenName, "" + mActivity.getClass().getSimpleName());
                }
            } else {
                jsonObject.put(screenName, "" + mActivity.getClass().getSimpleName());
            }
            jsonObject.put(mainScreenName, "" + mActivity.getClass().getSimpleName());
            String activityName = "activityName";
            jsonObject.put(activityName, "" + mActivity.getClass().getName());
            int _translationX = 0;
            int _translationY = 0;

            jsonObject.put(id, view.hashCode());
            jsonObject.put(width, pxToDp(view.getWidth()));
            jsonObject.put(height, pxToDp(view.getHeight()));
            jsonObject.put(left, pxToDp(view.getLeft()));
            jsonObject.put(top, pxToDp(view.getTop()));
            jsonObject.put(translationX, pxToDp(_translationX));
            jsonObject.put(translationY, pxToDp(_translationY));

            if (isWebView(view)) {
                jsonObject.put(isWebView, true);
                jsonObject.put(scrollX, 0);
                jsonObject.put(scrollY, 0);
            } else {
                jsonObject.put(isWebView, false);
                jsonObject.put(scrollX, pxToDp(view.getScrollX()));
                jsonObject.put(scrollY, pxToDp(view.getScrollY()));
            }

            if (isViewGroup(view)) {
                ViewGroup vg = (ViewGroup) view;
                ArrayList<Integer> subViews = new ArrayList<>();
                for (int i = 0; i < vg.getChildCount(); i++) {
                    subViews.add(vg.getChildAt(i).hashCode());
                }
                jsonObject.put(subviews, subViews);
                viewsJson.add(jsonObject);
                for (int i = 0; i < vg.getChildCount(); i++) {
                    printTree(vg.getChildAt(i));
                }

            } else {
                viewsJson.add(jsonObject);
            }


        } catch (Exception e) {

            ExceptionTracker.track(e);
        }

    }

    /**
     * Each Screen Field capture list wise Adding field capture listener
     *
     * @param activity
     * @param screenName
     */
    private void setIdWiseTracking(Activity activity, String screenName) {
        try {
            count = 0;
            //new EnableFieldCapture(activity, screenName).execute();
        } catch (Exception e) {
            e.printStackTrace();
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
                return "Android ab";
            else
                return "Android phone";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Android Phone";
    }

    //String[] screenNames;
    private void EnabledContentPublisher(final Activity mActivity) {
        try {
            ContentPublisherManager.getInstance().EnablePublisher(mActivity);
        } catch (Exception e) {

        }
    }

    private class ShakeShake extends AsyncTask<String, String, String> {
        Activity activity;

        ShakeShake(Activity activity) {
            this.activity = activity;
        }

        protected String doInBackground(String... urls) {
            try {

                if (sensorManager != null && shakeDetector != null)
                    sensorManager.unregisterListener(shakeDetector);

                sensorManager = (SensorManager) activity.getSystemService(SENSOR_SERVICE);

                shakeInit();
            } catch (Exception e) {

            }

            return "";
        }

    }

    /**
     * Enable View wise setting a tag
     */
    private class WindowChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            try {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 2000);
                if (isFlag) {
                    isFlag = false;
                    if (FIELD_TRACKING) {
                        setIdWiseTracking(mActivity, mActivity.getClass().getSimpleName());
                    } else {
                        Log.e("------********", "********--------");
                        Log.e(" FIELD_TRACKING ", "Disabled");
                        Log.e("------********", "********--------");
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * Socket onDisconnectServer
     */
    private final Emitter.Listener onDisconnectServer = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            try {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (dialog != null) {
                                if (dialog.isShowing())
                                    dialog.dismiss();
                            }

                        } catch (Exception e) {
                            ExceptionTracker.track(e);
                        }
                        randomNumber = randomNumber();
                        disableSocket();
                    }
                });
            } catch (Exception e) {

            }

        }
    };




    private final Emitter.Listener onMarketerAcceptance = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            try {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(" onMarketerAcceptance ", "Activity");
                        ReceiverHandler.removeCallbacks(ReceiverWaiting);
                        if (dialog == null)
                            showAuthDialog(mActivity);
                        else {
                            if (!dialog.isShowing())
                                showAuthDialog(mActivity);
                        }

                    }
                });
            } catch (Exception e) {

            }


        }
    };


    /**
     * Socket interval response time out listener
     */
    private final Runnable ReceiverWaiting = new Runnable() {
        @Override
        public void run() {
            try {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!abTestEnabled) {
                            disableSocket();
                            shakeFlag = false;
                        }

                    }
                });
            } catch (Exception e) {

            }

        }
    };


    /**
     * Socket onConnect
     */
    private final Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!isConnected) {
                                isConnected = true;
                            }
                            ReceiverHandler.removeCallbacks(ReceiverWaiting);
                            ReceiverHandler.postDelayed(ReceiverWaiting, 90000);
                            Log.e(" Socket Connected ", "Activity");
                            JSONObject jobj = new JSONObject();
                            jobj.put("appId", android + isTablet(mActivity).replace(" ", "") + SharedPref.getInstance().getStringValue(mActivity, AppConstants.reSharedAPIKey));
                            //jobj.put(appId, "AndroidAndroidPhonea27794d9-c4e6-45f9-815f-5470224f8f2a");
                            jobj.put(deviceId, Util.getDeviceId(mActivity));
                            mSocket.emit(socketHandShakeRequest, jobj);
                            Log.e(" Socket Connected ", jobj.getString("appId"));
                        } catch (Exception E) {
                            E.printStackTrace();
                        }
                    }

                });
            } catch (Exception e) {

            }


        }
    };


}
