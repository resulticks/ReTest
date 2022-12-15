package io.mob.resu.reandroidsdk;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import java.util.ArrayList;


public class MDeviceData {

    private Context context;
    private String appId = "";
    private String name = "";
    private String phone = "";
    private String email = "";
    private String deviceToken = "";
    private String deviceId = "";
    private String deviceOs = "Android";
    private String deviceType = "";
    private String deviceIdfa = "";
    private String packageName = "";
    private String deviceOsVersion = "";
    private String deviceManufacture = "";
    private String deviceModel = "";
    private String appVersionName = "";
    private String appVersionCode = "";
    private String appInstallDate = "";
    private String appUpdate = "";
    private String deviceIme = "";
    private String deviceSerialNumber = "";

    private String deviceNetWorkProviderName = "";
    private ArrayList<String> activityList = new ArrayList<>();

    public MDeviceData(Context context) {
        this.context = context;
        try {

            appId = SharedPref.getInstance().getStringValue(context, AppConstants.reSharedAPIKey);
            deviceId = Util.getDeviceId(context);
            deviceIdfa = SharedPref.getInstance().getStringValue(context, AppConstants.reSharedAdverId);
            deviceOsVersion = "" + Build.VERSION.RELEASE;
            deviceManufacture = Build.MANUFACTURER;
            deviceModel = Build.MODEL;
            deviceOs = "Android";

            if (isTablet(context))
                deviceType = "Android Tab";
            else
                deviceType = "Android Phone";


            appVersionName = Util.getAppVersionName(context);
            appVersionCode = Util.getAppVersionCode(context);
            deviceIme = Util.getDeviceIME(context);
            deviceNetWorkProviderName = Util.getDeviceNetworkProvider(context);
            deviceSerialNumber = Util.getDeviceSimSerialNumber(context);
            packageName = context.getApplicationContext().getPackageName();

        } catch (Exception e) {

        }
        //activityList = Util.getAllActivity(context);
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }


    public String getDeviceIme() {
        return deviceIme;
    }

    public void setDeviceIme(String deviceIme) {
        this.deviceIme = deviceIme;
    }

    public String getDeviceNetWorkProviderName() {
        return deviceNetWorkProviderName;
    }

    public void setDeviceNetWorkProviderName(String deviceNetWorkProviderName) {
        this.deviceNetWorkProviderName = deviceNetWorkProviderName;
    }

    boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getDeviceManufacture() {
        return deviceManufacture;
    }

    public void setDeviceManufacture(String deviceManufacture) {
        this.deviceManufacture = deviceManufacture;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public String getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(String appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public String getAppInstallDate() {
        return appInstallDate;
    }

    public void setAppInstallDate(String appInstallDate) {
        this.appInstallDate = appInstallDate;
    }

    public String getAppUpdate() {
        return appUpdate;
    }

    public void setAppUpdate(String appUpdate) {
        this.appUpdate = appUpdate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceOs() {
        return deviceOs;
    }

    public void setDeviceOs(String deviceOs) {
        this.deviceOs = deviceOs;
    }

    public String getDeviceIdfa() {
        return deviceIdfa;
    }

    public void setDeviceIdfa(String deviceIdfa) {
        this.deviceIdfa = deviceIdfa;
    }

    public String getDeviceOsVersion() {
        return deviceOsVersion;
    }

    public void setDeviceOsVersion(String deviceOsVersion) {
        this.deviceOsVersion = deviceOsVersion;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public ArrayList<String> getActivityList() {
        return activityList;
    }

    public void setActivityList(ArrayList<String> activityList) {
        this.activityList = activityList;
    }


}
