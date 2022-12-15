package io.mob.resu.reandroidsdk;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;

/**
 * Created by SDK on 08/02/18.
 */
public class OfflineEventTrack extends AsyncTask<String, String, String> {
    private final String userEvents;
    Context context;
    private final APIInterface apiInterface;
    private ArrayList<MData> dbEvents;

    OfflineEventTrack(Context context, String userEvents, APIInterface apiInterface) {
        this.context = context;
        this.userEvents = userEvents;
        this.apiInterface = apiInterface;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return getEventsFromLocalDataBase(context);
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            if (result != null)
                apiInterface.apiEventTracking(context, result, dbEvents);
        } catch (Exception e) {

        }

    }


    private String getEventsFromLocalDataBase(Context context) {
        JSONObject eventsObj = new JSONObject();
        try {
            new DataBase(context).insertData(userEvents, DataBase.Table.EVENT_TABLE);
            dbEvents = new DataBase(context).getData(DataBase.Table.EVENT_TABLE);
            eventsObj.put("appId", SharedPref.getInstance().getStringValue(context, AppConstants.reSharedAPIKey));
            eventsObj.put("deviceId", SharedPref.getInstance().getStringValue(context, AppConstants.reSharedDatabaseDeviceId));
            eventsObj.put("userId", SharedPref.getInstance().getStringValue(context, AppConstants.reSharedUserId));
            eventsObj.put(AppConstants.APP_ID, SharedPref.getInstance().getStringValue(context, AppConstants.reSharedAPIKey));
            eventsObj.put(AppConstants.TENANT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_ID));
            eventsObj.put(AppConstants.BUSINESS_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.BUSINESS_SHORT_CODE));
            eventsObj.put(AppConstants.DEPARTMENT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.DEPARTMENT_ID));
            eventsObj.put(AppConstants.TENANT_SHORT_CODE, SharedPref.getInstance().getStringValue(context, AppConstants.TENANT_SHORT_CODE));
            eventsObj.put(AppConstants.PASSPORT_ID, SharedPref.getInstance().getStringValue(context, AppConstants.PASSPORT_ID));
            ArrayList<JSONObject> campaigns = new ArrayList<>();
            // making format of webservice
            if (dbEvents != null && dbEvents.size() > 0) {
                for (MData mData : dbEvents) {
                    String s = mData.getValues();
                    JSONObject jsonObject1 = new JSONObject(s);
                    campaigns.add(jsonObject1);
                }
            }
            eventsObj.put("events", new JSONArray(campaigns));
        } catch (Exception e) {
            ExceptionTracker.track(e);
            try {
                eventsObj.put("appId", SharedPref.getInstance().getStringValue(context, AppConstants.reSharedAPIKey));
                eventsObj.put("deviceId", SharedPref.getInstance().getStringValue(context, AppConstants.reSharedDatabaseDeviceId));
                eventsObj.put("userId", SharedPref.getInstance().getStringValue(context, AppConstants.reSharedUserId));
                ArrayList<JSONObject> campaigns = new ArrayList<>();
                JSONObject jsonObject1 = new JSONObject(userEvents);
                campaigns.add(jsonObject1);
                eventsObj.put("events", new JSONArray(campaigns));

            } catch (Exception ew) {
                ExceptionTracker.track(ew);
            }
        }

        try {
            new DataBase(context).deleteData(dbEvents, DataBase.Table.EVENT_TABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return eventsObj.toString();
    }

}

