package io.mob.resu.reandroidsdk.error;


import static io.mob.resu.reandroidsdk.AppConstants.oldError;
import static io.mob.resu.reandroidsdk.Util.getTimeStampFormat;

import android.annotation.SuppressLint;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ExceptionTracker {

    // public static ArrayList<JSONObject> errors = new ArrayList<>();

    public static void track(Exception exception) {
        // exception.printStackTrace();
        // android.util.Log.e("Error", "" + exception.getMessage());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("error", exception.getMessage());
            jsonObject.put("timeStamp", getCurrentUTC());
            oldError.add(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void track(String message) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("error", message);
            jsonObject.put("timeStamp", getCurrentUTC());
            oldError.add(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String getCurrentUTC() {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = (SimpleDateFormat) getTimeStampFormat();
            return simpleDateFormat.format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            return "0000-00-00'T'00:00:00";
        }
    }
}