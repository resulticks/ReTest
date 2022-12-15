package io.mob.resu.reandroidsdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Base64;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


class SharedPref {

    private static SharedPreferences preference = null;
    private static SharedPref sharedPref = null;

    public static SharedPref getInstance() {
        if (sharedPref != null) {
            return sharedPref;
        } else {
            sharedPref = new SharedPref();
            return sharedPref;
        }
    }


    // TODO For String Message Encryption
    public static String encrypt(String textToEncrypt) {
        try {
            byte[] data = textToEncrypt.getBytes("UTF-8");
            return Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            return textToEncrypt;
        }

    }

    // TODO Decryption of Encrypted Message
    public static String decrypt(String textToDecrypt) {
        try {
            byte[] data = Base64.decode(textToDecrypt, Base64.DEFAULT);
            return new String(data, "UTF-8");
        } catch (Exception e) {
            return textToDecrypt;
        }
    }
    
    /**
     * Singleton object for the shared preference
     *
     * @param context
     * @return SharedPreferences
     */

    public static SharedPreferences getPreferenceInstance(Context context) {
        String preferenceName = "rsut";
        if (preference != null) {
            return preference;
        } else {
            preference = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
            return preference;
        }
    }

    /**
     * Set the shared preference W.R.T the key
     *
     * @param context
     * @param key
     * @param value
     */

    public void setSharedValue(Context context, String key, String value) {
        try {
            getPreferenceInstance(context);
            if (!TextUtils.isEmpty(value) & value != null) {
                value = encrypt(value);
            } else {
                value = "";
            }
            Editor editor = preference.edit();
            editor.putString(key, value);
            editor.apply();
        } catch (Exception e) {

        }

    }


    private String getDeviceID(Context context) {
        String deviceID = "0394-209-2349";
        return deviceID;
    }

    /**
     * Set the shared preference W.R.T the key
     *
     * @param context
     * @param key
     * @param value
     */

    public void setSharedValue(Context context, String key, int value) {
        try {
            getPreferenceInstance(context);
            Editor editor = preference.edit();
            editor.putInt(key, value);
            editor.apply();
        } catch (Exception e) {

        }

    }

    /**
     * Set the shared preference W.R.T the key
     *
     * @param context
     * @param key
     * @param value
     */

    public void setSharedValue(Context context, String key, Boolean value) {
        try {
            getPreferenceInstance(context);
            Editor editor = preference.edit();
            editor.putBoolean(key, value);
            editor.apply();
        } catch (Exception e) {

        }
    }

    /**
     * Returns the shared preference key for the given key
     *
     * @param context
     * @param key
     * @return Boolean
     */

    public Boolean getBooleanValue(Context context, String key) {
        try {
            return getPreferenceInstance(context).getBoolean(key, false);

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the shared preference key for the given key
     *
     * @param context
     * @param key
     * @return Int
     */

    public int getIntValue(Context context, String key) {
        try {
            return getPreferenceInstance(context).getInt(key, 0);
        } catch (Exception e) {
            return 0;
        }
    }


    /**
     * Returns the shared preference key for the given key
     *
     * @param context
     * @param key
     * @return String
     */

    public String getStringValue(Context context, String key) {
        try {
            String value = getPreferenceInstance(context).getString(key, "");
            if (!TextUtils.isEmpty(value)) {
                value = decrypt(value);
            }
            return value;
        } catch (Exception e) {
            return "";
        }

    }


}
