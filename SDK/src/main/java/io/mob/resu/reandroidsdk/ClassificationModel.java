package io.mob.resu.reandroidsdk;

public class ClassificationModel {
    String channelName;
    boolean isSelected = false;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
