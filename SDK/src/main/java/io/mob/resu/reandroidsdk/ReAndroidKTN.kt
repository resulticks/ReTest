package io.mob.resu.reandroidsdk

import android.content.Context
import android.os.Bundle
import org.json.JSONObject
import java.util.*

class ReAndroidKTN {

    companion object {

        fun getInit(context: Context) {
            ReAndroidSDK.getInstance(context)
        }

        fun appConversionTracking(context: Context) {
            ReAndroidSDK.getInstance(context).appConversionTracking()
        }

        fun appConversionTracking(context: Context, jsonObject: JSONObject) {
            ReAndroidSDK.getInstance(context).appConversionTracking(jsonObject)
        }

        fun getCampaignData(context: Context, deepLinkInterface: IDeepLinkInterface) {
            ReAndroidSDK.getInstance(context).getCampaignData(deepLinkInterface)
        }

        fun addNewNotification(context: Context, title: String, body: String, activityName: String, fragmentName: String, customParams: JSONObject) {
            ReAndroidSDK.getInstance(context).addNewNotification(title, body, activityName, fragmentName, customParams)
        }

        fun addNewNotificationAddNewNotification(context: Context, title: String, body: String, activityName: String, fragmentName: String) {
            ReAndroidSDK.getInstance(context).addNewNotification(title, body, activityName, fragmentName)
        }

        fun addNewNotificationAddNewNotification(context: Context, title: String, body: String, activityName: String) {
            ReAndroidSDK.getInstance(context).addNewNotification(title, body, activityName)
        }

        fun onDeviceUserRegister(context: Context, modelRegisterUser: MRegisterUser) {
            ReAndroidSDK.getInstance(context).onDeviceUserRegister(modelRegisterUser)
        }

        fun onReceivedCampaign(context: Context, data: Map<String, String>) {
            ReAndroidSDK.getInstance(context).onReceivedCampaign(data)
        }

        fun onReceivedCampaign(context: Context, data: Bundle) {
            ReAndroidSDK.getInstance(context).onReceivedCampaign(data)
        }

        fun onLocationUpdate(context: Context, latitude: Double, longitude: Double) {
            ReAndroidSDK.getInstance(context).onLocationUpdate(latitude, longitude)
        }

        fun readNotification(context: Context, campaignId: String) {
            ReAndroidSDK.getInstance(context).readNotification(campaignId)
        }

        fun unReadNotification(context: Context, campaignId: String) {
            ReAndroidSDK.getInstance(context).unReadNotification(campaignId)
        }

        fun notificationCTAClicked(context: Context, campaignId: String, actionId: String) {
            ReAndroidSDK.getInstance(context).notificationCTAClicked(campaignId, actionId)
        }

        fun getUnReadNotificationCount(context: Context): Int {
            return ReAndroidSDK.getInstance(context).unReadNotificationCount
        }

        fun getReadNotificationCount(context: Context): Int {
            return ReAndroidSDK.getInstance(context).readNotificationCount
        }

        fun deleteNotification(context: Context, rNotification: RNotification) {
            return ReAndroidSDK.getInstance(context).deleteNotification(rNotification)
        }

        fun deleteNotificationByObject(context: Context, jsonObject: JSONObject) {
            return ReAndroidSDK.getInstance(context).deleteNotificationByObject(jsonObject)
        }

        fun deleteNotificationByCampaignId(context: Context, campaignId: String) {
            ReAndroidSDK.getInstance(context).deleteNotificationByCampaignId(campaignId)
        }

        fun deleteNotificationByNotificationId(context: Context, notificationId: String) {
            ReAndroidSDK.getInstance(context).deleteNotificationByNotificationId(notificationId)
        }

        fun getNotifications(context: Context): ArrayList<RNotification> {

            return ReAndroidSDK.getInstance(context).notifications
        }


        fun getNotificationByObject(context: Context): ArrayList<JSONObject> {

            return ReAndroidSDK.getInstance(context).notificationByObject
        }


        fun onTrackEvent(context: Context, eventName: String) {
            ReAndroidSDK.getInstance(context).onTrackEvent(eventName)

        }

        fun onTrackEvent(context: Context, data: JSONObject, eventName: String) {
            ReAndroidSDK.getInstance(context).onTrackEvent(data, eventName)
        }

        fun onTrackEvent(context: Context, data: HashMap<String, Object>, eventName: String) {
            ReAndroidSDK.getInstance(context).onTrackEvent(JSONObject(data as Map<*, *>), eventName)
        }

        fun formDataCapture(context: Context, data: HashMap<String, Object>) {
            ReAndroidSDK.getInstance(context).formDataCapture(JSONObject(data as Map<*, *>))
        }

        fun handleQrLink(context: Context, QRLink: String, iGetQRLinkDetail: IGetQRLinkDetail) {
            ReAndroidSDK.getInstance(context).handleQrLink(QRLink, iGetQRLinkDetail)
        }

        fun updatePushToken(context: Context, pushToken: String) {
            ReAndroidSDK.getInstance(context).updatePushToken(pushToken)
        }
    }

}





