package io.mob.resu.reandroidsdk;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;
import io.mob.resu.reandroidsdk.error.Log;

import static io.mob.resu.reandroidsdk.error.Util.isCheckBox;
import static io.mob.resu.reandroidsdk.error.Util.isEditText;
import static io.mob.resu.reandroidsdk.error.Util.isRadioButton;
import static io.mob.resu.reandroidsdk.error.Util.isRadioGroup;
import static io.mob.resu.reandroidsdk.error.Util.isRatingBar;
import static io.mob.resu.reandroidsdk.error.Util.isSeekBar;
import static io.mob.resu.reandroidsdk.error.Util.isSpinner;
import static io.mob.resu.reandroidsdk.error.Util.isSwitch;
import static io.mob.resu.reandroidsdk.error.Util.isTextView;
import static io.mob.resu.reandroidsdk.error.Util.isToggleButton;


public class ScreenSessionRecord extends AsyncTask<String, String, String> {

    Context context;
    private JSONObject values;
    private HashMap<String, Object> Views;

    ScreenSessionRecord(Context context, JSONObject values, HashMap<String, Object> Views) {
        this.context = context;
        this.values = values;
        this.Views = Views;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            values = getFieldData(context, values);
            new DataBase(context).insertScreenData(values.toString(), DataBase.Table.SCREENS_TABLE);
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }

        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

    }

    /**
     * f
     * Get Field Wise Tracking Data Capture
     *
     * @param mActivity
     * @param screenObject
     */
    private JSONObject getFieldData(Context mActivity, JSONObject screenObject) throws Exception {
        try {
            if (Views != null) {
                Log.e("EditText Record ", "" + Views.size());

                if (Views.size() > 0) {
                    ArrayList<JSONObject> capturedList = new ArrayList<>();

                    for (Map.Entry map : Views.entrySet()) {

                        View host = (View) map.getValue();
                        Object object = host.getTag();
                        String tag;
                        String viewId = "";
                        if (AppConstants.isReactNative) {
                            viewId = "" + host.getId();
                        } else {
                            String[] id = host.getResources().getResourceName(host.getId()).split("/");
                            viewId = id[1];
                        }

                        if (object instanceof String) {
                            tag = (String) object;
                            GetViewFieldList(new DataBase(mActivity).getFieldData(DataBase.Table.REGISTER_EVENT_TABLE, viewId, tag), capturedList, host, viewId);
                        } else {
                            Activity activity = getActivity(host);
                            if (activity != null) {
                                tag = activity.getClass().getSimpleName();
                            } else {
                                tag = host.getContext().getClass().getSimpleName();
                            }
                            GetViewFieldList(new DataBase(mActivity).getFieldData(DataBase.Table.REGISTER_EVENT_TABLE, viewId, tag), capturedList, host, viewId);
                        }

                    }
                    Views = new HashMap<>();
                    ArrayList<JSONObject> fieldTrack = new ArrayList<>();
                    ArrayList<JSONObject> brandFormField = new ArrayList<>();

                    for (JSONObject jsonObject : capturedList) {
                        if (jsonObject.has("campaignId")) {
                            fieldTrack.add(jsonObject);
                        } else {
                            brandFormField.add(jsonObject);
                            String fieldValue = jsonObject.getString("result");
                            String formId = jsonObject.optString("formId");
                            String fieldName = jsonObject.optString("fieldName");
                            String fieldType = jsonObject.optString("fieldType");
                            String screenName = jsonObject.optString("screenName");
                            String requiredfield = jsonObject.optString("requiredfield");
                            String viewId = jsonObject.optString("identifier");
                            new DataBase(mActivity).insertDataBrandWon(DataBase.Table.BRAND_OWN_FORM_TABLE, screenName, viewId, formId, fieldName, fieldValue, fieldType,requiredfield);
                        }
                    }
                    screenObject.put("fieldCapture", new JSONArray(fieldTrack));
                   // screenObject.put("brandFormCapture", new JSONArray(brandFormField));
                    brandFormSubmission(mActivity, brandFormField);
                }

                // Cordova relative
                if (AppConstants.isCordova) {
                    ArrayList<JSONObject> fieldTrack = new ArrayList<>();
                    ArrayList<JSONObject> brandFormField = new ArrayList<>();
                    for (int i = 0; i > AppConstants.hybridFieldTrack.length(); i++) {
                        JSONObject jsonObject = AppConstants.hybridFieldTrack.getJSONObject(i);
                        if (jsonObject.has("campaignId")) {
                            fieldTrack.add(jsonObject);
                        } else {
                            brandFormField.add(jsonObject);
                            String fieldValue = jsonObject.getString("result");
                            String formId = jsonObject.optString("formId");
                            String fieldName = jsonObject.optString("fieldName");
                            String fieldType = jsonObject.optString("fieldType");
                            String requiredfield = jsonObject.optString("requiredfield");
                            String screenName = jsonObject.optString("screenName");
                            String viewId = jsonObject.optString("identifier");
                            new DataBase(mActivity).insertDataBrandWon(DataBase.Table.BRAND_OWN_FORM_TABLE, screenName, viewId, formId, fieldName, fieldValue, fieldType,requiredfield);
                        }
                    }
                    screenObject.put("fieldCapture", new JSONArray(fieldTrack));
                //    screenObject.put("brandFormCapture", new JSONArray(brandFormField));
                    brandFormSubmission(mActivity, brandFormField);
                    AppConstants.hybridFieldTrack = null;
                }
            }
            return screenObject;
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    private void brandFormSubmission(Context mActivity, ArrayList<JSONObject> brandFormField) {
        try {
            if (brandFormField != null) {
                for (JSONObject obj : brandFormField) {
                    if (obj.has("formId") && obj.optBoolean("markAsSubmit") && obj.optString("result").equalsIgnoreCase("Clicked")) {
                        String tenantId = SharedPref.getInstance().getStringValue(mActivity, AppConstants.TENANT_ID).replace("cust_", "").replace("camp_", "").replace("resulsdk_", "").replace("rpt_", "");
                        String formId = obj.getString("formId");
                        ArrayList<JSONObject> formData = new DataBase(mActivity).getFormData(DataBase.Table.BRAND_OWN_FORM_TABLE, formId);
                        JSONObject formSubObject = new JSONObject();
                        formSubObject.put("formId", formId);
                        formSubObject.put("APIKey",tenantId);
                       // formSubObject.put("APIKey", "e7be7449_b573_4e29_a427_a46e36288409");
                        formSubObject.put("SourceUrl", obj.getString("screenName"));
                        formSubObject.put("pagereferrerurl", "");
                        formSubObject.put("pagetitle", "");
                        formSubObject.put("rid", SharedPref.getInstance().getStringValue(mActivity, AppConstants.PASSPORT_ID));
                        formSubObject.put("cid", "");
                        formSubObject.put("formData", new JSONArray(formData));
                        Log.e("Brand Form Data", formSubObject.toString());
                        new DataNetworkHandler().apiBrandFormSubmission(mActivity, formSubObject);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void GetViewFieldList(ArrayList<JSONObject> fieldData, ArrayList<JSONObject> capturedList, View host, String viewId) throws JSONException {

        try {
            if (fieldData != null && fieldData.size() > 0) {
                for (JSONObject jsonObject : fieldData) {
                    jsonObject.put("viewId", viewId);
                    if (jsonObject.getString("identifier").contains(viewId)) {
                        String result;
                        if (isEditText(host)) {
                            result = "" + ((EditText) host).getText();
                        } else if (isTextView(host)) {
                            result = "" + ((TextView) host).getText();
                        } else if (isSwitch(host)) {
                            result = "" + ((Switch) host).isChecked();
                        } else if (isRatingBar(host)) {
                            result = "" + ((RatingBar) host).getRating();
                        } else if (isSeekBar(host)) {
                            result = "" + ((SeekBar) host).getProgress();
                        } else if (isRadioButton(host)) {
                            result = "" + ((RadioButton) host).isChecked();
                        } else if (isRadioGroup(host)) {
                            int rating = ((RadioGroup) host).getCheckedRadioButtonId();
                            RadioButton radioButton = host.findViewById(rating);
                            result = "" + radioButton.isChecked();
                        } else if (isCheckBox(host)) {
                            result = "" + ((CheckBox) host).isChecked();
                        } else if (isSpinner(host)) {
                            result = "" + ((Spinner) host).getSelectedItem().toString();
                            if (AppConstants.isReactNative) {
                                result = getReactSpinnerValue(result);
                            }
                        } else if (isToggleButton(host)) {
                            result = "" + ((ToggleButton) host).isChecked();
                        } else {
                            result = "Clicked";
                        }

                        if (jsonObject.getString("captureType").equalsIgnoreCase("value"))
                            jsonObject.put("result", result);
                        else if (jsonObject.getString("captureType").equalsIgnoreCase("length"))
                            jsonObject.put("result", result.length());
                        else if (jsonObject.getString("captureType").equalsIgnoreCase("Click"))
                            jsonObject.put("result", "Clicked");
                    }
                    capturedList.add(jsonObject);
                }
            }
        } catch (Exception e) {

        }
    }

    private String getReactSpinnerValue(String val) {
        String result = "";
        try {
            JSONObject obj = new JSONObject(val);
            String nativeMap = obj.getString("NativeMap");
            obj = new JSONObject(nativeMap);
            result = obj.getString("label");
        } catch (Exception e) {
            Log.e("Parsing Exception :", e.getMessage());
        }
        return result;
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

        }
        return null;
    }
}
