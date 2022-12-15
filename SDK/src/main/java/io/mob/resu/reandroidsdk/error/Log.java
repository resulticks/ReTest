package io.mob.resu.reandroidsdk.error;


import static io.mob.resu.reandroidsdk.AppConstants.LogFlag;

public class Log {

    public static void d(String name, String value) {
       if (LogFlag)
            android.util.Log.d(name, value);
    }

    public static void i(String name, String value) {
        if (LogFlag)
            android.util.Log.i(name, value);
    }

    public static void e(String name, String value) {
        if (LogFlag)
            android.util.Log.e(name, value);
    }

    public static void w(String name, String value) {
        if (LogFlag)
            android.util.Log.e(name, value);
    }
}
