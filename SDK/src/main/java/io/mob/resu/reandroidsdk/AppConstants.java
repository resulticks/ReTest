package io.mob.resu.reandroidsdk;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class AppConstants {

    final static String reResumeJourney = "ResumeJourney";
    final static String reSharedUserId = "sharedUserId";
    final static String reSharedDatabaseDeviceId = "sharedDatabaseDeviceId";
    final static String reSharedReferral = "QueryParameter";
    final static String reSharedAPIKey = "sharedAPIKey";
    final static String reSharedMobileFriendlyUrl = "mobileFriendlyUrl";
    final static String reSharedAdverId = "advertId";
    final static String reSharedCampaignId = "sharedCampaignId";

    final static String reManifestApiKey = "resulticks.key";
    final static String reManifestNotificationIcon = "resulticks.default_notification_icon";
    final static String reManifestNotificationColor = "resulticks.default_notification_color";
    final static String reManifestNotificationIconTrans = "resulticks.default_notification_icon_transparent";
    final static String reDeepLinkParamReferralId = "_resulticks_link_unique_Id";

    final static String sdkManifestNotificationIconTrans = "sdk.default_notification_icon_transparent";
    final static String sdkManifestNotificationIcon = "sdk.default_notification_icon";
    final static String sdkManifestNotificationColor = "sdk.default_notification_color";
    final static String sdkDeepLinkParamReferralId = "_sdk_link_unique_Id";
    final static String sdkManifestApiKey = "sdk.key";


    final static String reAppNotificationId = "notificationId";
    final static String reDeepLinkParamIsNewInstall = "isNewInstall";
    final static String reDeepLinkParamIsViaDeepLinkingLauncher = "isViaDeepLinkingLauncher";
    final static String reNotificationViaLauncher = "isViaNotificationLauncher";

    final static String reApiParamId = "id";
    final static String reApiParamNavigationScreen = "navigationScreen";
    final static String reApiParamIsNewUser = "isNewUser";
    final static String reApiParamStatus = "status";
    final static String reApiParamRating = "rating";
    final static String reApiParamComments = "comments";
    final static String reApiParamStartTime = "startTime";
    final static String reApiParamEndTime = "endTime";
    final static String reApiParamSubScreenName = "subScreenName";
    final static String reApiParamErrorLog = "errorLog";
    final static String reApiParamScreenName = "screenName";
    final static String reApiParamCrashText = "crashText";
    final static String reApiParamTimeStamp = "timeStamp";
    final static String reApiParamAppCrash = "appCrash";
    final static String TENANT_SHORT_CODE = "tCode";
    final static String BUSINESS_SHORT_CODE = "bCode";
    final static String DEPARTMENT_ID = "did";
    final static String TENANT_ID = "tenantId";
    final static String PASSPORT_ID = "passportId";
    final static String CAMPAIGN_REFERRAL = "campaignReferral";
    final static String CAMPAIGN_CHANNEL = "campaignChannel";
    final static String CAMPAIGN_NAME = "campaignName";
    final static String CAMPAIGN_ID = "campaignId";
    final static String CITY = "city";
    final static String STATE = "state";
    final static String COUNTRY = "country";
    final static String APP_ID = "appId";
    static final String LAST_VISITED_ACTIVITY = "lastVisitedActivity";
    static final String LAST_VISITED_FRAGMENT = "lastVisitedFragment";
    static final String LAST_APP_OPENED = "AppLastOpenedDate";
    static final String CURRENT_ACTIVITY = "CurrentActivityName";
    static final String CURRENT_FRAGMENT = "CurrentFragmentName";

    final static String reAppMode = "appState";
    final static String reDeviceOs = "deviceOs";
    final static String reOsVersion = "osVersion";
    final static String reAppVersion = "appVersion";
    final static String reNetWorkType = "networkType";
    final static String reIsDoNotDisturbModeEnabled = "isDoNotDisturbModeEnabled";
    final static String reIsNotificationEnabled = "isNotificationEnabled";
    final static String reIsBatterySavingsModeEnabled = "isBatterySavingsModeEnabled";
    final static String reBatteryStatus = "batteryStatus";

    static final int SDK_USER_REGISTER = 1001;
    static final int SDK_API_KEY = 1002;
    static final int SDK_NOTIFICATION_VIEWED = 1003;
    static final int SDK_EVENTS = 1007;
    static final int SDK_SCREEN_TACKING = 1004;
    static final int SDK_LOCATION_TACKING = 1005;
    static final int SDK_CAMPAIGN_DETAILS = 1006;
    static final int SDK_CAMPAIGN_DETAILS_USER = 1008;
    static final int SDK_NOTIFICATION_AMPLIFIER = 1009;
    static final int SDK_RULES = 1010;
    static final int SDK_CAMPAIGN_CONVERSION = 1011;
    static final int SDK_FORM_DATA = 1012;
    static final int SDK_GET_FIELD_TRACK = 1013;
    static final int SDK_CAMPAIGN_BLAST = 1014;
    static final int SDK_CAROUSEL = 1015;
    static final int SDK_TOKEN_UPDATE = 1016;
    static final int SDK_SET_LAST_VISIT = 1017;
    static final int SDK_GET_LAST_VISIT = 1018;
    static final int SDK_GET_MOBILE_CONFIG = 1019;
    static final int SDK_BRAND_OWN_FORM = 1020;
    static final int SDK_GET_AUTH = 1021;
    static boolean CRESUME_JOURNEY = false;
    static int retrycout =0;

    public static String LAST_ACTIVITY_NAME = "";
    public static String LAST_FRAGMENT_NAME = "";
    public static String APP_CURRENT_EVENT = "CurrentEventName";
    public static String CURRENT_ACTIVITY_NAME = "";
    public static String CURRENT_FRAGMENT_NAME = "";

    static final String SDK_VERSION = "3.0.2";
    static final String NOTIFICATION_EXPIRED = "1";
    static final String NOTIFICATION_OPEN = "2";
    static final String NOTIFICATION_DISMISSED = "3";
    static final String NOTIFICATION_MAY_BE_LATER = "4";
    static final String NOTIFICATION_RECEIVED = "5";
    static final String NOTIFICATION_RECEIVED_FCM = "5";
    static final String NOTIFICATION_RECEIVED_AMP = "55";
    static final String NOTIFICATION_CUSTOM_CTA1 = "6";
    static final String NOTIFICATION_CUSTOM_CTA2 = "7";
    static final String format = "yyyy-MM-dd'T'HH:mm:ss";
    static String token = "";
    static String screenData = "";

    static JSONObject resumeJourney = new JSONObject();
    static JSONObject deviceData = null;

    static JSONObject lastJourneyData = null;
    static JSONObject lastDeviceData = null;


    static final String socketLiveDashboard = "https://soc.resu.io";
    static final String socketUrl = "https://mobsoc.resu.io/";

    static final String CarosualUrl = "getMobileCarousel?id";
    static final String Brand_FORM_API = "IndexInsertBrandOwnData";
    static final String FORM_API = "submitLeadForm";
    static final String CAMPAIGN_BLAST_API = "MobileSDKBlast";


    // Team Environment
  /* 2022
   public static final String baseUrl = "https://teamsdk.resulticks.net/Home/";
   public static final String FirstBaseUrl = "https://teamsdk.resulticks.net/Home/";*/

    // Run23 Environment
    static final String baseUrl = "https://mobis.resu.io/Home/";
    static final String FirstBaseUrl = "https://mobis.resu.io/Home/";
    /*static final String baseUrl = "https://mad.rsut.io/Home/";
    static final String FirstBaseUrl = "https://mad.rsut.io/Home/";
*/
    public static boolean LogFlag = false;
    static int retryCount = 1;
    static String deviceId = "";
    static boolean licence = false;
    static boolean isLogin = false;
    static String sessionId = "";
    public static ArrayList<JSONObject> oldError = new ArrayList<>();
    static ArrayList<JSONObject> newError;
    static JSONArray rulesArray = new JSONArray();

    // General hybrid
    // ** Don't changes access modifier **
    public static boolean isHyBird = false;
    public static int screenViews = 0;
    public static JSONArray hybridFieldTrack;
    public static JSONArray hybridViewsJson = new JSONArray();
    public static String HybridScreenUrl = "";
    public static boolean isCordova = false;
    public static String appOpenTime = "";
    public static String pageStartTime = "";
    public static JSONObject deviceDetails = null;
    // react native specific
    static boolean isReactNative = false;

    // Config
    static boolean SDK = true;
    static boolean LOCATION_TRACKING = true;
    static boolean SCREEN_TRACKING = true;
    static boolean BLUETOOTH_CONNECTIVITY = true;
    static boolean NETWORK_CONNECTIVITY = true;
    static boolean WIFI_CONNECTIVITY = true;
    static boolean NFC_CONNECTIVITY = true;
    static boolean CUSTOM_EVENT = true;
    static boolean FIELD_TRACKING = true;
    static boolean USER_REGISTER = true;
    static boolean IN_PAGE_CONTENT_INJECTION = true;
    static boolean NOTIFICATION_DND_DISABLED = true;
    static boolean APP_CRASH = true;

    //default Event config
    static boolean CFIRST_APP_OPEN = false;
    static boolean CNOTIFICATION_EXPIRED = false;
    static boolean CNOTIFICATION_OPENED = false;
    static boolean CNOTIFICATION_RECEIVED = false;
    static boolean CNOTIFICATION_MAYBE_LATER = false;
    static boolean CNOTIFICATION_CUSTOM_ACTION = false;
    static boolean CNOTIFICATION_DISMISSED = false;
    static boolean CAPP_CRASHED = false;
    static boolean CNOTIFICATION_UNSUBSCRIBED = false;
    static boolean CNOTIFICATION_RESUME_JOURNEY = false;

    //default Attribute config
    static boolean AEMAIL = false;
    static boolean ANAME = false;
    static boolean AAGE = false;
    static boolean AMARRIED = false;
    static boolean AEDUCATION = false;
    static boolean APHOTO = false;
    static boolean AGENDER = false;
    static boolean ADOP = false;
    static boolean APHONE = false;
    static boolean AEMPLOYED = false;

}
