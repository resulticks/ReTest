package io.mob.resu.reandroidsdk;

import org.json.JSONObject;

public class RContentPublisher {

    String id;
    JSONObject content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }
}
