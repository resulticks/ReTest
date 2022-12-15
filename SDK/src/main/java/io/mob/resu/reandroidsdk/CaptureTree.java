package io.mob.resu.reandroidsdk;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Handler;
import android.util.Base64;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import io.mob.resu.reandroidsdk.error.Log;
import io.socket.client.Socket;

import static io.mob.resu.reandroidsdk.ActivityLifecycleCallbacks.socketClientMessageImage;


class CaptureTree {
    private static Bitmap screenBitmap;


    public void CaptureTreeImage(boolean isDialog, boolean isConnected, boolean abTestEnabled, Activity mActivity, Socket mSocket, Handler handler, Runnable runnable, Bitmap screenBitmap) {

        try {
            CaptureTree.screenBitmap = screenBitmap;
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("imageData", takeScreenshot(mActivity));
            jsonObject1.put("mainScreenName", mActivity.getClass().getSimpleName());
            jsonObject1.put("manufacture", Build.MANUFACTURER);
            jsonObject1.put("deviceModel", Build.MODEL);
            jsonObject1.put("deviceType", isTablet(mActivity));
            jsonObject1.put("deviceOs", "Android");
            jsonObject1.put("height", pxToDp(mActivity.getWindow().getDecorView().getRootView().getHeight()));
            jsonObject1.put("width", pxToDp(mActivity.getWindow().getDecorView().getRootView().getWidth()));
            jsonObject1.put("isDialog", isDialog);
            jsonObject1.put("appId", "Android" + isTablet(mActivity).replace(" ", "") + SharedPref.getInstance().getStringValue(mActivity, AppConstants.reSharedAPIKey));
            jsonObject1.put("deviceId", Util.getDeviceId(mActivity));
            Log.e("Image", "Sent");
            mSocket.emit(socketClientMessageImage, jsonObject1);
            if (abTestEnabled && isConnected) {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static String takeScreenshot(Activity mActivity) {

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            screenBitmap = returnBitmap(screenBitmap, pxToDp(mActivity.getWindow().getDecorView().getRootView().getWidth()), pxToDp(mActivity.getWindow().getDecorView().getRootView().getHeight()));////where mIcon_val is bitmap to resize
            screenBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String value = Base64.encodeToString(byteArray, Base64.NO_WRAP);
            return "data:image/JPEG;base64," + value;
        } catch (Exception e) {
            return null;
        }
    }

    private static Bitmap returnBitmap(Bitmap mIcon_val, int width, int height) {
        try {
            Matrix matrix = new Matrix();
            if (width == 0)
                width = mIcon_val.getWidth();
            if (height == 0)
                height = mIcon_val.getHeight();
            matrix.postScale((float) width / mIcon_val.getWidth(), (float) height / mIcon_val.getHeight());
            return Bitmap.createBitmap(mIcon_val, 0, 0, mIcon_val.getWidth(), mIcon_val.getHeight(), matrix, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        if (xlarge || large)
            return "Android tab";
        else
            return "Android phone";
    }

    private static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }


}
