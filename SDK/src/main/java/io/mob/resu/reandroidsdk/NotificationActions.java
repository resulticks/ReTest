package io.mob.resu.reandroidsdk;

import org.json.JSONArray;
import org.json.JSONObject;

public interface NotificationActions {

    void mediaClick();

    void CTAClick(JSONArray jsonArray, int action);
}
