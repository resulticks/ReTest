package io.mob.resu.reandroidsdk;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;



public class DataClear extends AsyncTask<String, String, String> {

    private final String response;
    private final int flag;
    Context context;
    private final ArrayList<MData> arrayList;

    DataClear(Context context, ArrayList<MData> arrayList, String response, int flag) {
        this.context = context;
        this.arrayList = arrayList;
        this.response = response;
        this.flag = flag;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            switch (flag) {
                case AppConstants.SDK_NOTIFICATION_VIEWED:
                    try {
                        new DataBase(context).deleteData(arrayList, DataBase.Table.CAMPAIGN_TABLE);
                    } catch (Exception e) {
                        ExceptionTracker.track(e);
                    }
                    break;

                case AppConstants.SDK_SCREEN_TACKING:
                    try {
                        new DataBase(context).deleteData(arrayList, DataBase.Table.SCREENS_TABLE);
                    } catch (Exception e) {
                        ExceptionTracker.track(e);
                    }
                    break;
                case AppConstants.SDK_CAMPAIGN_DETAILS:
                    SharedPref.getInstance().setSharedValue(context, AppConstants.reSharedReferral, response);
                    break;

                case AppConstants.SDK_EVENTS:
                    try {
                        new DataBase(context).deleteData(arrayList, DataBase.Table.EVENT_TABLE);
                    } catch (Exception e) {
                        ExceptionTracker.track(e);
                    }
                    break;
            }
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }

        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

    }


}
