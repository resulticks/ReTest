package io.mob.resu.reandroidsdk;

public class RNotification {

    private String body = "";
    private String titleColor = "";
    private String contentBgColor = "";
    private String bodyColor = "";

    public String getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(String titleColor) {
        this.titleColor = titleColor;
    }

    public String getContentBgColor() {
        return contentBgColor;
    }

    public void setContentBgColor(String contentBgColor) {
        this.contentBgColor = contentBgColor;
    }

    public String getBodyColor() {
        return bodyColor;
    }

    public void setBodyColor(String bodyColor) {
        this.bodyColor = bodyColor;
    }

    private String title = "";
    private String subTitle = "";
    private String notificationImageUrl = "";
    private String activityName = "";
    private String fragmentName = "";
    private String campaignId = "";
    private String customParams = "{}";
    private String notificationId = "";
    private String MobileFriendlyUrl = "";
    private String customActions = "[]";

    public String getCarousel() {
        return carousel;
    }

    public void setCarousel(String carousel) {
        this.carousel = carousel;
    }

    private String carousel = "[]";
    private String pushType = "";
    private String bannerStyle = "";
    private String sourceType = "";
    private String channelName = "";
    private String channelID = "";

    public String getIsCarousel() {
        return isCarousel;
    }

    public void setIsCarousel(String isCarousel) {
        this.isCarousel = isCarousel;
    }

    private String isCarousel = "false";
    private String ttl = "";
    private String url = "";
    private String tag = "";

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    private boolean isRead = false;

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCustomActions() {
        return customActions;
    }

    public void setCustomActions(String customActions) {
        this.customActions = customActions;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public String getBannerStyle() {
        return bannerStyle;
    }

    public void setBannerStyle(String bannerStyle) {
        this.bannerStyle = bannerStyle;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }


    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }


    public String getMobileFriendlyUrl() {
        return MobileFriendlyUrl;
    }

    public void setMobileFriendlyUrl(String mobileFriendlyUrl) {
        MobileFriendlyUrl = mobileFriendlyUrl;
    }

    public String getCustomParams() {
        return customParams;
    }

    public void setCustomParams(String customParams) {
        this.customParams = customParams;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotificationImageUrl() {
        return notificationImageUrl;
    }

    public void setNotificationImageUrl(String notificationImageUrl) {
        this.notificationImageUrl = notificationImageUrl;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getFragmentName() {
        return fragmentName;
    }

    public void setFragmentName(String fragmentName) {
        this.fragmentName = fragmentName;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

}
