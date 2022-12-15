package io.mob.resu.reandroidsdk;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;
import io.mob.resu.reandroidsdk.error.Log;
import io.mob.resu.reandroidsdk.error.Util;

import static io.mob.resu.reandroidsdk.error.Util.isAppCompatActivity;

public class BrandFormCapture {

    private HashMap<String, Object> FiledTrackViews;
    static BrandFormCapture brandFormCapture;
    Activity activity;
    ArrayList<String> screenNames;
    private ArrayList<JSONObject> captureIds = new ArrayList<>();
    String values = "";


    public static BrandFormCapture getInstance() {
        if (brandFormCapture == null)
            brandFormCapture = new BrandFormCapture();
        return brandFormCapture;
    }


    public void EnableBrandCapture(Activity mActivity) {
        try {
            this.activity = mActivity;
            Log.e("EnablePublisher", "Start");
            screenNames = getScreenNames(mActivity);
            captureIds = getBrandFormCaptureData(screenNames);
            View view = mActivity.getWindow().getDecorView().getRootView();

            for (JSONObject obj : captureIds) {
                try {
                    JSONArray jsonArray = obj.optJSONArray("formFields");
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            View view1 = null;
                            int resID = mActivity.getResources().getIdentifier(jsonObject.getString("viewId"), "id", mActivity.getPackageName());
                            view1 = view.findViewById(resID);
                            if (view1 != null) {
                                view1.setAccessibilityDelegate(new FieldTrackingListener());
                                view1.setOnTouchListener(new EventTochListener());
                                String tag;
                                Activity activity = getActivity(view1);
                                if (activity != null)
                                    tag = activity.getClass().getSimpleName();
                                else
                                    tag = view1.getContext().getClass().getSimpleName();

                                if (AppConstants.isReactNative) {
                                        FiledTrackViews.put("" + view1.getId(), view1);

                                } else {
                                    String[] id = view1.getResources().getResourceName(view1.getId()).split("/");
                                    String key = id[1] + "/" + tag;
                                        FiledTrackViews.put(key, view1);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    ExceptionTracker.track(e);
                }
            }
        } catch (Exception e) {

        }
    }

    private Activity getActivity(View v) {
        try {
            Context context = v.getContext();
            while (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
                context = ((ContextWrapper) context).getBaseContext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private ArrayList<JSONObject> getBrandFormCaptureData(ArrayList<String> screenNames) {
        try {

            ArrayList<JSONObject> forms = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(values);
            JSONArray jsonArray = jsonObject.optJSONArray("brandForms");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    if (screenNames.contains(obj.getString("screenName")) || screenNames.contains(obj.getString("subScreenName"))) {
                        forms.add(obj);
                    }
                }
            }
            return forms;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private class FieldTrackingListener extends View.AccessibilityDelegate {

        @Override
        public void sendAccessibilityEvent(View host, int eventType) {
            super.sendAccessibilityEvent(host, eventType);
            //Log.e("FieldTrackingListener", host.getClass().getSimpleName());
        }
    }

    private class EventTochListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            return false;
        }
    }

    private ArrayList<JSONObject> getCaptureValues(Activity activity) {
        try {

            ArrayList<String> screenNames = getScreenNames(activity);
            ArrayList<JSONObject> capturedList = getBrandFormCaptureData(screenNames);

            for (int i = 0; i < capturedList.size(); i++) {
                JSONObject jsonObject = capturedList.get(i);

                JSONArray jsonArray = jsonObject.optJSONArray("formFields");
                for (int j = 0; j < jsonArray.length(); j++) {

                    JSONObject fields = jsonArray.getJSONObject(j);
                    for (Map.Entry map : FiledTrackViews.entrySet()) {

                        View host = (View) map.getValue();
                        Object object = host.getTag();
                        String viewId = "";
                        if (AppConstants.isReactNative) {
                            viewId = "" + host.getId();
                        } else {
                            String[] id = host.getResources().getResourceName(host.getId()).split("/");
                            viewId = id[1];
                        }
                        if (fields.getString("viewId").equalsIgnoreCase(viewId)) {
                            fields.put("value", ((EditText) host).getText().toString());
                        }
                    }
                }
            }
            return capturedList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private ArrayList<String> getScreenNames(Activity mActivity) {

        try {
            if (isAppCompatActivity(mActivity)) {
                List<Fragment> fragmentList = ((FragmentActivity) mActivity).getSupportFragmentManager().getFragments();
                Log.e("List Of Fragments", "" + fragmentList.size());
                screenNames = new ArrayList<>();
                for (int j = 0; j < fragmentList.size(); j++) {
                    Log.e("List Of Fragments", "" + fragmentList.get(j).getClass().getSimpleName());
                    screenNames.add(fragmentList.get(j).getClass().getSimpleName());
                }
                screenNames.add(mActivity.getClass().getSimpleName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return screenNames;
    }

}
