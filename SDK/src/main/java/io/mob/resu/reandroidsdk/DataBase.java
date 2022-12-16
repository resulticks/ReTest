package io.mob.resu.reandroidsdk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;
import io.mob.resu.reandroidsdk.error.Log;

class DataBase {

    private static final int DATABASE_VERSION = 1;
    private static final String CAMPAIGN_TABLE = "rsut_campaign_table";
    private static final String NOTIFICATION_TABLE = "rsut_notification_table";
    private static final String SCREENS_TABLE = "rsut_screens_table";
    private static final String REGISTER_EVENT_TABLE = "rsut_register_event_table";
    private static final String REGISTER_VIEWS_TABLE = "rsut_register_views_table";
    private static final String EVENT_TABLE = "rsut_event_table";
    private static final String BRAND_OWN_FORM_TABLE = "rsut_brand_own_form_table";

    private static final String CREATE_BRAND_OWN_FORM_TABLE = "CREATE TABLE IF NOT EXISTS " + BRAND_OWN_FORM_TABLE + " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "formid TEXT , " +
            "fieldname TEXT , " +
            "fieldtype TEXT , " +
            "fieldvalue TEXT , " +
            "requiredfield TEXT , " +
            "viewid TEXT , " +
            "screenName TEXT );";

    private static final String CREATE_CAMPAIGN_TABLE = "CREATE TABLE IF NOT EXISTS " + CAMPAIGN_TABLE + " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "value LONGTEXT NOT NULL , " +
            "param1 TEXT , " +
            "param2 TEXT , " +
            "param3 TEXT , " +
            "param4 TEXT );";

    private static final String CREATE_NOTIFICATION_TABLE = "CREATE TABLE IF NOT EXISTS " + NOTIFICATION_TABLE + " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "value LONGTEXT NOT NULL, " +
            "id TEXT NOT NULL, " +
            "param1 TEXT , " +
            "param2 TEXT , " +
            "param3 TEXT , " +
            "param4 TEXT );";

    private static final String CREATE_EVENT_TABLE = "CREATE TABLE IF NOT EXISTS " + EVENT_TABLE + " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "value LONGTEXT NOT NULL , " +
            "param1 TEXT , " +
            "param2 TEXT , " +
            "param3 TEXT , " +
            "param4 TEXT );";

    private static final String CREATE_SCREENS_TABLE = "CREATE TABLE IF NOT EXISTS " + SCREENS_TABLE + " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "value LONGTEXT NOT NULL, " +
            "param1 TEXT , " +
            "param2 TEXT , " +
            "param3 TEXT , " +
            "param4 TEXT ); ";

    private static final String CREATE_REGISTER_EVENT_TABLE = "CREATE TABLE IF NOT EXISTS " + REGISTER_EVENT_TABLE + " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "viewid TEXT NOT NULL, " +
            "screenname TEXT NOT NULL, " +
            "value LONGTEXT NOT NULL, " +
            "param1 TEXT , " +
            "param2 TEXT , " +
            "param3 TEXT , " +
            "param4 TEXT );";

    private static final String CREATE_REGISTER_VIEWS_TABLE = "CREATE TABLE IF NOT EXISTS " + REGISTER_VIEWS_TABLE + " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "viewid TEXT NOT NULL, " +
            "screenname TEXT NOT NULL, " +
            "value LONGTEXT NOT NULL, " +
            "param1 TEXT , " +
            "param2 TEXT , " +
            "param3 TEXT , " +
            "param4 TEXT );";

    private static final String DATABASE_NAME = "rsut_sdk";
    private final DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    DataBase(Context ctx) {
        DBHelper = new DatabaseHelper(ctx);
    }

    private void openRead(String methodName) throws SQLException {
        db = DBHelper.getReadableDatabase();
        Log.e("DB_openRead", methodName);
    }

    private void openWrite(String methodName) throws SQLException {
        db = DBHelper.getWritableDatabase();
        Log.e("DB_openWrite", methodName);
    }

    private void closeWrite(String methodName) throws SQLException {
        if (db != null) {
            db.close();
        }
        Log.e("DB_closeWrite", methodName);
    }

    private void closeRead(String methodName) throws SQLException {
        if (db != null) {
            db.close();
        }
        Log.e("DB_closeRead", methodName);
    }


    /**
     * Getting All Table wise
     *
     * @param i
     * @return
     */
    ArrayList<RNotification> getDataByModel(Table i) {

        Cursor cursor = null;
        try {
            openRead("getDataByModel");

            ArrayList<RNotification> array_list = new ArrayList<>();
            String tableName = getTableName(i);

            cursor = db.rawQuery("select * from " + tableName, null);
            if (cursor != null) {
                int mCount = cursor.getCount();
                if (mCount > 0) {
                    if (cursor.getColumnCount() > 0) {
                        if (cursor.moveToFirst()) {
                            do {
                                RNotification mData = new RNotification();
                                JSONObject jsonObject = new JSONObject(cursor.getString(1));
                                mData.setNotificationId("" + cursor.getInt(0));
                                mData.setBody(jsonObject.optString("body"));
                                mData.setTitle(jsonObject.optString("title"));
                                mData.setNotificationImageUrl(jsonObject.optString("notificationImageUrl"));
                                mData.setActivityName(jsonObject.optString("activityName"));
                                mData.setFragmentName(jsonObject.optString("fragmentName"));
                                mData.setCampaignId(jsonObject.optString("campaignId"));
                                mData.setCustomParams(jsonObject.optString("customParams"));
                                mData.setMobileFriendlyUrl(jsonObject.optString("MobileFriendlyUrl"));
                                mData.setCustomActions(jsonObject.optString("customActions"));
                                mData.setPushType(jsonObject.optString("pushType"));
                                mData.setBannerStyle(jsonObject.optString("bannerStyle"));
                                mData.setSourceType(jsonObject.optString("sourceType"));
                                mData.setSubTitle(jsonObject.optString("subTitle"));
                                mData.setTtl(jsonObject.optString("ttl"));
                                mData.setUrl(jsonObject.optString("url"));
                                mData.setChannelID(jsonObject.optString("channelId"));
                                mData.setChannelName(jsonObject.optString("channelName"));
                                mData.setTag(jsonObject.optString("tag"));
                                mData.setCarousel(jsonObject.optString("carousel"));
                                mData.setIsCarousel(jsonObject.optString("isCarousel"));
                                mData.setTag(jsonObject.optString("tag"));
                                mData.setBodyColor(jsonObject.optString("bodyColor"));
                                mData.setTitleColor(jsonObject.optString("titleColor"));
                                mData.setContentBgColor(jsonObject.optString("contentBgColor"));
                                mData.setRead(Boolean.parseBoolean(jsonObject.optString("isRead")));
                                if (jsonObject.has("ttl")) {
                                    if (TextUtils.isEmpty(jsonObject.getString("ttl")) || Util.isExpired("" + jsonObject.get("ttl"))) {
                                        array_list.add(mData);
                                    } /*else {
                                    deleteByNotificationId("" + cursor.getInt(0), i);
                                }*/
                                } else {
                                    array_list.add(mData);
                                }
                            } while (cursor.moveToNext());
                        }
                    }
                }
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeRead("getDataByModel");

            return array_list;
        } catch (Exception e) {
            ExceptionTracker.track(e);
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeRead("getDataByModel");
            return new ArrayList<>();
        }
    }


    /**
     * Getting All Table wise
     *
     * @param i
     * @return
     */
    ArrayList<MData> getData(Table i) {
        Cursor cursor = null;
        try {

            openRead("getData");
            ArrayList<MData> array_list = new ArrayList<>();
            String tableName = getTableName(i);
            cursor = db.rawQuery("select * from " + tableName, null);
            if (cursor != null) {
                int mCount = cursor.getCount();
                if (mCount > 0 && cursor.getColumnCount() > 0) {
                    if (cursor.moveToFirst()) {
                        do {
                            MData mData = new MData();
                            mData.setId(cursor.getInt(0));
                            mData.setValues(cursor.getString(1));
                            array_list.add(mData);
                        } while (cursor.moveToNext());
                    }
                }
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

            closeRead("getData");

            return array_list;
        } catch (Exception e) {
            ExceptionTracker.track(e);
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

            closeRead("getData");
            return new ArrayList<>();
        }
    }

    ArrayList<JSONObject> getNotification(Table i) {
        Cursor cursor = null;
        try {

            openRead("getNotification");
            ArrayList<JSONObject> array_list = new ArrayList<>();
            String tableName = getTableName(i);
            cursor = db.rawQuery("select * from " + tableName, null);

            if (cursor != null) {

                int mCount = cursor.getCount();

                if (mCount > 0 && cursor.getColumnCount() > 0) {
                    if (cursor.moveToFirst()) {
                        do {
                            try {
                                JSONObject jsonObject = new JSONObject(cursor.getString(1));
                                jsonObject.put("notificationId", cursor.getInt(0));

                                if (jsonObject.has("ttl")) {
                                    if (TextUtils.isEmpty(jsonObject.getString("ttl")) || Util.isExpired("" + jsonObject.get("ttl"))) {
                                        array_list.add(jsonObject);
                                    } else {
                                        deleteByNotificationId("" + cursor.getInt(0), i);
                                    }
                                } else {
                                    array_list.add(jsonObject);
                                }

                            } catch (Exception e) {
                                Log.e("notification retrieval: ", e.toString());
                            }
                        } while (cursor.moveToNext());
                    }
                }

            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeRead("getNotification");

            return array_list;
        } catch (Exception e) {
            ExceptionTracker.track(e);
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeRead("getNotification");
            return new ArrayList<>();
        }
    }


    /**
     * Getting All Table wise
     *
     * @param i
     * @return
     */
    ArrayList<MData> getNData(Table i) {
        Cursor cursor = null;
        try {
            openRead("getNData");
            ArrayList<MData> array_list = new ArrayList<>();
            String tableName = getTableName(i);

            cursor = db.rawQuery("select * from " + tableName, null);
            if (cursor != null) {

                int mCount = cursor.getCount();

                if (mCount > 0 && cursor.getColumnCount() > 0) {
                    if (cursor.moveToFirst()) {
                        do {
                            MData mData = new MData();
                            mData.setId(cursor.getInt(1));
                            mData.setValues(cursor.getString(2));
                            array_list.add(mData);
                        } while (cursor.moveToNext());
                    }
                }

            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

            closeRead("getNData");
            return array_list;
        } catch (Exception e) {
            ExceptionTracker.track(e);
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

            closeRead("getNData");
            return new ArrayList<>();
        }
    }


    /**
     * Provide Already register Event
     *
     * @param tablename
     * @param viewId
     * @param screenName
     * @return
     */
    public ArrayList<JSONObject> getFieldData(Table tablename, String viewId, String screenName) {
        Cursor cursor = null;
        try {
            openRead("getFieldData");
            String tableName = getTableName(tablename);
            ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
            cursor = getCursorCount(viewId, screenName, tableName);
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        try {
                            JSONObject jsonObject = new JSONObject(cursor.getString(3));
                            jsonObjectArrayList.add(jsonObject);
                        } catch (Exception e) {
                            ExceptionTracker.track(e);
                        }

                    } while (cursor.moveToNext());
                }
                // cursor.close();
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeRead("getFieldData");

            return jsonObjectArrayList;
        } catch (Exception e) {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeRead("getFieldData");
            ExceptionTracker.track(e);
        }
        return null;
    }


    /**
     * Provide Already register Event
     *
     * @param tablename
     * @return
     */
    public ArrayList<JSONObject> getFormData(Table tablename, String formId) {
        Cursor cursor = null;
        try {

            openRead("getFormData");
            String tableName = getTableName(tablename);
            ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
            cursor = getFormField(formId, tableName);
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        try {
                            JSONObject jsonObject = new JSONObject();
                           /* "formid TEXT , " +
                                    "fieldname TEXT , " +
                                    "fieldtype TEXT , " +
                                    "fieldvalue TEXT , " +
                                    "viewid TEXT , " +*/
                            jsonObject.put("fieldName", cursor.getString(cursor.getColumnIndex("fieldname")));
                            jsonObject.put("fieldType", cursor.getString(cursor.getColumnIndex("fieldtype")));
                            jsonObject.put("fieldvalue", cursor.getString(cursor.getColumnIndex("fieldvalue")));
                            jsonObject.put("requiredfield", cursor.getString(cursor.getColumnIndex("requiredfield")));
                            jsonObjectArrayList.add(jsonObject);
                        } catch (Exception e) {
                            ExceptionTracker.track(e);
                        }

                    } while (cursor.moveToNext());
                }
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeRead("getFormData");

            return jsonObjectArrayList;
        } catch (Exception e) {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeRead("getFormData");
            ExceptionTracker.track(e);
        }
        return null;
    }

    public ArrayList<JSONObject> getFieldTrackingList(Table tablename, String screenName) {
        Cursor cursor = null;
        try {

            ArrayList<JSONObject> registerEvents = new ArrayList<>();

            openRead("getFieldTrackingList");
            String tableName = getTableName(tablename);
            cursor = getItems(screenName, tableName);
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        try {
                            registerEvents.add(new JSONObject(cursor.getString(3)));
                        } catch (Exception e) {
                            ExceptionTracker.track(e);
                        }
                    } while (cursor.moveToNext());
                }
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

            closeRead("getFieldTrackingList");

            return registerEvents;
        } catch (Exception e) {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeRead("getFieldTrackingList");
            ExceptionTracker.track(e);
        }
        return null;

    }


    /**
     * Delete list of rows table wise
     *
     * @param values
     * @param i
     */
    void deleteData(ArrayList<MData> values, Table i) {


        try {
            openWrite("deleteData");
            for (MData mData : values) {
                String tableName = getTableName(i);
                Log.e("datas deleted", tableName + " " + mData.getId());
                db.execSQL("delete from " + tableName + " where _id='" + mData.getId() + "'");
            }
            closeWrite("deleteData");

        } catch (Exception e) {
            closeWrite("deleteData");
            ExceptionTracker.track(e);
        }

    }

    void deleteDataValue(ArrayList<MData> values, Table i) {
        try {
            openWrite("deleteDataValue");
            for (MData mData : values) {
                String tableName = getTableName(i);
                Log.e("datas deleted", tableName + " " + mData.getId());
                db.execSQL("delete from " + tableName + " where value='" + mData.getValues() + "'");
            }
            closeWrite("deleteDataValue");
        } catch (Exception e) {
            closeWrite("deleteDataValue");
            ExceptionTracker.track(e);
        }

    }

    /**
     * Delete list of rows table wise
     *
     * @param i
     */
    void deleteByNotificationId(String id, Table i) {

        try {
            openWrite("deleteByNotificationId");
            String tableName = getTableName(i);
            Log.e("datas deleted", tableName + " " + id);
            db.execSQL("delete from " + tableName + " where _id='" + id + "'");
            closeWrite("deleteByNotificationId");

        } catch (Exception e) {
            closeWrite("deleteByNotificationId");
            ExceptionTracker.track(e);
        }

    }


    /**
     * Delete list of rows table wise
     *
     * @param i
     */
    void readByNotificationId(String id, Table i) {

        try {
            openWrite("readByNotificationId");
            String tableName = getTableName(i);
            Log.e("datas deleted", tableName + " " + id);
            db.execSQL("delete from " + tableName + " where _id='" + id + "'");
            closeWrite("readByNotificationId");

        } catch (Exception e) {
            closeWrite("readByNotificationId");
            ExceptionTracker.track(e);
        }

    }


    /**
     * Delete list of rows table wise
     *
     * @param i
     */
    void deleteByCampaignId(String id, Table i) {

        try {
            openWrite("deleteByCampaignId");
            String tableName = getTableName(i);
            Log.e("datas deleted", tableName + " " + id);
            db.execSQL("delete from " + tableName + " where id='" + id + "'");

            closeWrite("deleteByCampaignId");

        } catch (Exception e) {
            closeWrite("deleteByCampaignId");
            ExceptionTracker.track(e);
        }

    }


    /**
     * Delete data row id wise
     *
     * @param id
     * @param i
     */
    private void deleteEventData(String id, Table i) {

        try {
            openWrite("deleteEventData");
            String tableName = getTableName(i);
            Log.e("datas deleted", tableName + " " + id);
            db.execSQL("delete from " + tableName + " where id='" + id + "'");

            closeWrite("deleteEventData");

        } catch (Exception e) {
            closeWrite("deleteEventData");
            ExceptionTracker.track(e);
        }
    }

    /**
     * Delete data row id wise
     *
     * @param i
     */
    public void deleteNotificationTable(Table i, int id) {

        try {
            openWrite("deleteNotificationTable");
            String tableName = getTableName(i);
            db.execSQL("delete from " + tableName + " where nid='" + id + "'");
            Log.e("datas deleted", tableName + " " + id);

            closeWrite("deleteNotificationTable");

        } catch (Exception e) {
            closeWrite("deleteNotificationTable");
            ExceptionTracker.track(e);
        }

    }


    /**
     * Delete data row id wise
     *
     * @param i
     */
    public void deleteFormBrandFields(Table i, String formId) {

        try {
            openWrite("deleteFormBrandFields");
            String tableName = getTableName(i);
            db.execSQL("delete from " + tableName + " where formId='" + formId + "'");
            Log.e("datas deleted", tableName + " " + formId);

            closeWrite("deleteFormBrandFields");

        } catch (Exception e) {
            ExceptionTracker.track(e);
            closeWrite("deleteFormBrandFields");
        }

    }


    /**
     * Delete data row id wise
     *
     * @param i
     */
    public void deleteEventTable(Table i) {

        try {
            openWrite("deleteEventTable");
            String tableName = getTableName(i);
            db.execSQL("delete from " + tableName);

            closeWrite("deleteEventTable");

        } catch (Exception e) {
            closeWrite("deleteEventTable");
            ExceptionTracker.track(e);
        }

    }


    /**
     * Delete data row id wise
     *
     * @param mData
     * @param i
     */
    public void deleteNotificationData(RNotification mData, Table i) {

        try {
            String id = mData.getCampaignId();
            deleteEventData(id, i);
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }

    }

    /**
     * Delete data row id wise
     *
     * @param mData
     * @param i
     */
    public void deleteNotificationByObject(JSONObject mData, Table i) {

        try {
            String id = mData.getString("campaignId");
            deleteEventData(id, i);
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }

    }

    /**
     * enum type to get table name
     *
     * @param i
     * @return
     */
    @NonNull
    private String getTableName(Table i) {
        String tableName = "";
        switch (i) {
            case BRAND_OWN_FORM_TABLE:
                tableName = BRAND_OWN_FORM_TABLE;
                break;
            case CAMPAIGN_TABLE:
                tableName = CAMPAIGN_TABLE;
                break;
            case NOTIFICATION_TABLE:
                tableName = NOTIFICATION_TABLE;
                break;
            case SCREENS_TABLE:
                tableName = SCREENS_TABLE;
                break;
            case REGISTER_EVENT_TABLE:
                tableName = REGISTER_EVENT_TABLE;
                break;
            case REGISTER_VIEWS_TABLE:
                tableName = REGISTER_VIEWS_TABLE;
                break;

            case EVENT_TABLE:
                tableName = EVENT_TABLE;
                break;
        }
        return tableName;
    }


    /**
     * insert Data table wise
     *
     * @param value
     * @param i
     */
    synchronized void insertData(String value, Table i) {
        try {
            openWrite("insertData");
            String tableName = getTableName(i);
            ContentValues initialValues = new ContentValues();
            initialValues.put("value", value);
            // Log.e("data  insert to ", "" + value);
            //Log.e("data  insert to ", "" + tableName);
            db.insert(tableName, null, initialValues);
            closeWrite("insertData");

        } catch (Exception e) {
            closeWrite("insertData");
            ExceptionTracker.track(e);
        }
    }


    synchronized void insertDataBrandWon(Table i, String screenName, String viewId, String formId, String fieldName, String fieldvalue, String fieldType, String requiredfield) {
        try {
            openWrite("insertDataBrandWon");
            String tableName = getTableName(i);
            ContentValues initialValues = new ContentValues();
            initialValues.put("formid", formId);
            initialValues.put("fieldname", fieldName);
            initialValues.put("fieldtype", fieldType);
            initialValues.put("fieldvalue", fieldvalue);
            initialValues.put("requiredfield", requiredfield);
            initialValues.put("viewid", viewId);
            initialValues.put("screenName", screenName);
            Cursor cursor = isFormFieldExist(viewId, formId, screenName, tableName);
            if (cursor != null && cursor.getCount() > 0) {
                db.update(tableName, initialValues, "formid" + "=?" + " AND viewid" + "=?", new String[]{formId, viewId});
                //Log.e("record", "Updated");
            } else {
                db.insert(tableName, null, initialValues);
                // Log.e("record", "Inserted");
            }
            closeWrite("insertDataBrandWon");

        } catch (Exception e) {
            closeWrite("insertDataBrandWon");
            ExceptionTracker.track(e);
        }
    }

    synchronized void insertScreenData(String value, Table i) {
        try {
            openWrite("insertScreenData");
            String tableName = getTableName(i);
            ContentValues initialValues = new ContentValues();
            initialValues.put("value", value);
            Log.e("data  insert to ", "" + value);
            Log.e("data  insert to ", "" + tableName);
            db.insert(tableName, null, initialValues);
            closeWrite("insertScreenData");

        } catch (Exception e) {
            closeWrite("insertScreenData");
            ExceptionTracker.track(e);
        }


    }


    synchronized void insertScreenDataBulk(ArrayList<MData> mDataArrayList, Table i) {
        try {
            openWrite("insertScreenDataBulk");
            String tableName = getTableName(i);
            if (mDataArrayList != null && mDataArrayList.size() > 0) {
                int count = mDataArrayList.size();
                for (int j = 0; j < count; j++) {
                    ContentValues initialValues = new ContentValues();
                    String values = mDataArrayList.get(j).getValues();
                    initialValues.put("value", values);
                    // Log.e("data  insert to ", "" + values);
                    // Log.e("data  insert to ", "" + tableName);
                    db.insert(tableName, null, initialValues);
                }
            }

            closeWrite("insertScreenDataBulk");
        } catch (Exception e) {
            closeWrite("insertScreenDataBulk");
            ExceptionTracker.track(e);
        }


    }

    /**
     * Update Data table wise
     */
    void insertContentPublisherSingle(JSONObject object, Table table) {
        try {
            deleteEventTable(table);
            openWrite("insertContentPublisherSingle");
            String tableName = getTableName(table);
            ContentValues initialValues = new ContentValues();
            initialValues.put("value", object.getString("content"));
            initialValues.put("viewid", object.getString("identifier"));
            initialValues.put("screenname", object.getString("screenName"));
            // Log.e("data  insert to ", "" + tableName);
            db.insert(tableName, null, initialValues);
            closeWrite("insertContentPublisherSingle");

        } catch (Exception e) {
            closeWrite("insertContentPublisherSingle");
            ExceptionTracker.track(e);
        }
    }

    /**
     * Update Data table wise
     */
    void insertContentPublisherBulk(JSONArray jsonArray, Table table) {
        try {
            deleteEventTable(table);
            openWrite("insertContentPublisherBulk");
            String tableName = getTableName(table);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = new JSONObject(jsonArray.get(i).toString());
                object.put("result", "");
                ContentValues initialValues = new ContentValues();
                initialValues.put("value", object.toString());
                initialValues.put("viewid", object.getString("identifier"));
                initialValues.put("screenname", object.getString("screenName"));
                //  Log.e("data  insert to ", "" + tableName);
                db.insert(tableName, null, initialValues);
            }
            closeWrite("insertContentPublisherBulk");

        } catch (Exception e) {
            closeWrite("insertContentPublisherBulk");
            ExceptionTracker.track(e);
        }
    }


    /**
     * Update Data table wise
     */
    void insertOrUpdateData(JSONArray jsonArray) {
        try {

            openWrite("insertOrUpdateData");
            String tableName = getTableName(Table.REGISTER_EVENT_TABLE);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = new JSONObject(jsonArray.get(i).toString());
                ContentValues initialValues = new ContentValues();
                initialValues.put("value", jsonArray.get(i).toString());
                initialValues.put("viewid", object.getString("identifier"));
                initialValues.put("screenname", object.getString("screenName"));
                db.insert(tableName, null, initialValues);
            }
            closeWrite("insertOrUpdateData");
        } catch (Exception e) {
            closeWrite("insertOrUpdateData");
            ExceptionTracker.track(e);
        }
    }

    private String partiallyFieldData(String viewId, String formId, String ScreenName) {

        return "";

    }

    public ArrayList<RContentPublisher> getContentPublisher(Table tablename, String[] screenName) {
        Cursor cursor = null;
        try {
            ArrayList<RContentPublisher> registerEvents = new ArrayList<>();
            openRead("getContentPublisher");
            String tableName = getTableName(tablename);
            cursor = getMultipleItems(screenName, tableName);
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        try {
                            JSONObject jsonObject = new JSONObject(cursor.getString(cursor.getColumnIndex("value")));
                            RContentPublisher rContentPublisher = new RContentPublisher();
                            rContentPublisher.setId(jsonObject.getString("identifier"));
                            rContentPublisher.setContent(jsonObject);
                            registerEvents.add(rContentPublisher);
                        } catch (Exception e) {
                            ExceptionTracker.track(e);
                        }
                    } while (cursor.moveToNext());
                }
                //cursor.close();
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeRead("getContentPublisher");
            return registerEvents;
        } catch (Exception e) {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeRead("getContentPublisher");
            ExceptionTracker.track(e);
        }
        return new ArrayList<>();

    }


    private Cursor getMultipleItems(String[] screenNames, String tableName) {
        try {
            String query = "SELECT * FROM " + tableName + " WHERE screenname IN (" + covertString(screenNames.length) + ")";
            // Log.e("Content Publisher", query);
            Cursor cursor = db.rawQuery(query, screenNames);
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    String covertString(int arrayLength) {
        try {
            if (arrayLength < 1) {
                throw new RuntimeException("No Values");
            } else {
                StringBuilder sb = new StringBuilder(arrayLength * 2 - 1);
                sb.append("?");
                for (int i = 1; i < arrayLength; i++) {
                    sb.append(",?");
                }
                Log.e("Array Values : ", sb.toString());
                return sb.toString();
            }
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Update Data table wise
     *
     * @param i
     */
    void insertOrUpdateNData(String value, String id, Table i) {
        Cursor cursor = null;
        try {
            openWrite("insertOrUpdateNData");
            String tableName = getTableName(i);
            ContentValues initialValues = new ContentValues();
            initialValues.put("value", value);
            initialValues.put("id", id);
            Log.e("data insert to ", "" + tableName);
            cursor = getCursorCount(id, tableName);
            if (cursor != null && cursor.getCount() > 0)
                db.update(tableName, initialValues, "id" + "=?", new String[]{id});
            else
                db.insert(tableName, null, initialValues);

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeWrite("insertOrUpdateNData");

        } catch (Exception e) {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeWrite("insertOrUpdateNData");
            ExceptionTracker.track(e);
        }
    }

    /**
     * Update Data table wise
     *
     * @param i
     */
    void insertOrUpdateNData(String value, String id, boolean flag, Table i) {
        Cursor cursor = null;
        try {
            openWrite("insertOrUpdateNData");
            String tableName = getTableName(i);
            ContentValues initialValues = new ContentValues();
            initialValues.put("value", value);
            initialValues.put("id", id);
            initialValues.put("param1", "" + flag);
            Log.e("data insert to ", "" + tableName);
            cursor = getCursorCount(id, tableName);
            if (cursor != null && cursor.getCount() > 0)
                db.update(tableName, initialValues, "id" + "=?", new String[]{id});
            else
                db.insert(tableName, null, initialValues);

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeWrite("insertOrUpdateNData");

        } catch (Exception e) {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeWrite("insertOrUpdateNData");
            ExceptionTracker.track(e);
        }
    }


    /**
     * Update Data table wise
     *
     * @param i
     */
    boolean isNotificationExist(String id, Table i) {
        boolean flag = true;
        Cursor cursor = null;
        try {
            openRead("isNotificationExist");
            String tableName = getTableName(i);
            cursor = getCursorCount(id, tableName);
            flag = cursor == null || cursor.getCount() <= 0;

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeRead("isNotificationExist");

        } catch (Exception e) {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeRead("isNotificationExist");
            ExceptionTracker.track(e);
        }
        return flag;
    }


    /**
     * Update Data table wise
     *
     * @param i
     */
    ArrayList<Bundle> isNotificationExist(ArrayList<Bundle> bundles, Table i) {

        Cursor cursor = null;
        try {
            ArrayList<Bundle> finalBundles = new ArrayList<>();
            openRead("isNotificationExist");
            String tableName = getTableName(i);
            for (int j = 0; j < bundles.size(); j++) {
                cursor = getCursorCount(bundles.get(j).getString("id"), tableName);
                if (cursor == null || cursor.getCount() <= 0) {
                    finalBundles.add(bundles.get(j));
                }
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeRead("isNotificationExist");
            return finalBundles;
        } catch (Exception e) {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeRead("isNotificationExist");
            ExceptionTracker.track(e);
        }
        return new ArrayList<>();
    }

    int getNotificationCount(boolean flag, Table i) {
        Cursor cursor = null;
        int count = 0;
        try {
            openRead("getNotificationCount");
            String tableName = getTableName(i);
            cursor = getCursorNotificationCount(flag, tableName);

            if (cursor != null) {
                count = cursor.getCount();
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

            closeRead("getNotificationCount");
        } catch (Exception e) {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeRead("getNotificationCount");
        }
        return count;
    }


    /**
     * getting view id wise register events
     *
     * @param viewId
     * @param screenName
     * @param tableName
     * @return
     */
    private Cursor getCursorCount(String viewId, String screenName, String tableName) {
        return db.query(tableName, new String[]{"_id", "viewid", "screenname", "value"}, "viewid" + "=?" + " AND screenname" + "=?", new String[]{viewId, screenName}, null, null, null, null);
    }

    private Cursor isFormFieldExist(String viewId, String formId, String screenName, String tableName) {
        return db.query(tableName, new String[]{"formid", "viewid", "screenName", "fieldvalue"}, "formid" + "=?" + " AND viewid" + "=?" + " AND screenName" + "=?", new String[]{formId, viewId, screenName}, null, null, null, null);
    }


    private Cursor getFormField(String formId, String tableName) {
        return db.query(tableName, new String[]{"formid", "fieldname", "fieldvalue", "fieldtype", "requiredfield"}, "formid" + "=?", new String[]{formId}, null, null, null, null);
    }


    /**
     * getting view id wise register events
     *
     * @param tableName
     * @return
     */
    private Cursor getCursorCount(String id, String tableName) {
        return db.query(tableName, new String[]{"id"}, "id" + "=?", new String[]{id}, null, null, null, null);
    }

    /**
     * getting view id wise register events
     *
     * @param tableName
     * @return
     */
    private Cursor getCursorNotificationCount(boolean flag, String tableName) {
        return db.query(tableName, new String[]{"param1"}, "param1" + "=?", new String[]{"" + flag}, null, null, null, null);
    }

    /**
     * getting view id wise register events
     *
     * @param tableName
     * @return
     */
    private Cursor getCursor(String id, String tableName) {
        try {
            return db.query(tableName, new String[]{"id", "value"}, "id" + "=?", new String[]{id}, null, null, null, null);
        } catch (Exception e) {

        }
        return null;
    }

    String markRead(String id, Table tableName, boolean flag) {

        try {
            if (TextUtils.isEmpty(id))
                return "";
            if (id == null)
                return "";
        } catch (Exception e) {
            e.printStackTrace();
        }

       /* try {
            if (TextUtils.isEmpty(id))
                return "";
            if (id == null)
                return "";

            openRead("markRead");
            String tableNames = getTableName(tableName);
            Cursor cursor = getCursor(id, tableNames);
            String campaignId = "";
            JSONObject jsonObject = null;
            if (cursor != null) {
                int mCount = cursor.getCount();
                if (mCount > 0 && cursor.getColumnCount() > 0) {
                    if (cursor.moveToFirst()) {
                        do {
                            try {
                                jsonObject = new JSONObject(cursor.getString(1));
                                jsonObject.put("notificationId", cursor.getInt(0));
                                jsonObject.put("isRead", flag);
                                campaignId = jsonObject.getString("campaignId");
                            } catch (Exception e) {
                                Log.e("notification retrieval: ", e.toString());
                            }
                        } while (cursor.moveToNext());
                    }
                }

            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeRead("markRead");
            insertOrUpdateNData(jsonObject.toString(), campaignId, Table.NOTIFICATION_TABLE);
            return campaignId;
        } catch (Exception e) {
            e.printStackTrace();
            closeRead("markRead");
        }*/
        try {
            new InsertOrUpdateTask(id, tableName, flag).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {

        }

        return "";
    }

    public ArrayList<JSONObject> getFieldTracking(Table tablename, String screenName) {
        Cursor cursor = null;
        try {

            ArrayList<JSONObject> registerEvents = new ArrayList<>();
            openRead("getFieldTracking");
            String tableName = getTableName(tablename);

            cursor = getItems(screenName, tableName);
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        try {
                            registerEvents.add(new JSONObject(cursor.getString(3)));
                        } catch (Exception e) {
                            ExceptionTracker.track(e);
                        }
                    } while (cursor.moveToNext());
                }

            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeRead("getFieldTracking");

            return registerEvents;
        } catch (Exception e) {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            closeRead("getFieldTracking");
            ExceptionTracker.track(e);
        }
        return null;
    }

    private Cursor getItems(String screenName, String tableName) {
        return db.query(tableName, new String[]{"_id", "viewid", "screenname", "value"}, "screenname" + "=?", new String[]{screenName}, null, null, null, null);
    }


    /**
     * Enum table names
     */

    enum Table {
        NOTIFICATION_RESPONSE_PENDING_TABLE,
        NOTIFICATION_TABLE,
        CAMPAIGN_TABLE,
        BRAND_OWN_FORM_TABLE,
        SCREENS_TABLE,
        EVENT_TABLE,
        REGISTER_EVENT_TABLE,
        REGISTER_VIEWS_TABLE
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        //DB TABLE CREATION
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_BRAND_OWN_FORM_TABLE);
                db.execSQL(CREATE_CAMPAIGN_TABLE);
                db.execSQL(CREATE_NOTIFICATION_TABLE);
                db.execSQL(CREATE_SCREENS_TABLE);
                db.execSQL(CREATE_REGISTER_EVENT_TABLE);
                db.execSQL(CREATE_REGISTER_VIEWS_TABLE);
                db.execSQL(CREATE_EVENT_TABLE);
            } catch (SQLException e) {
                ExceptionTracker.track(e);
            }
            Log.e("DATA BASE CREATED", "DATA BASE CREATED");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Log.e("DATA BASE CREATED", "onUpgrade");
            } catch (SQLException e) {
                ExceptionTracker.track(e);
            }
            onCreate(db);
        }
    }

    private class InsertOrUpdateTask extends AsyncTask<String, String, String> {

        String id;
        Table tableName;
        boolean flag;

        InsertOrUpdateTask(String id, Table tableName, boolean flag) {
            this.id = id;
            this.tableName = tableName;
            this.flag = flag;
        }

        protected String doInBackground(String... urls) {
            Cursor cursor = null;
            try {
                String tableNames = getTableName(tableName);
                //dbOpen();
                openRead("InsertOrUpdateTask");
                cursor = getCursor(id, tableNames);
                String campaignId = "";
                JSONObject jsonObject = null;
                if (cursor != null) {

                    int mCount = cursor.getCount();
                    if (mCount > 0 && cursor.getColumnCount() > 0) {
                        if (cursor.moveToFirst()) {
                            do {
                                try {
                                    jsonObject = new JSONObject(cursor.getString(1));
                                    jsonObject.put("notificationId", cursor.getInt(0));
                                    jsonObject.put("isRead", flag);
                                    campaignId = jsonObject.getString("campaignId");
                                } catch (Exception e) {
                                    Log.e("notification retrieval: ", e.toString());
                                }
                            } while (cursor.moveToNext());
                        }
                    }

                }
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
                closeRead("InsertOrUpdateTask");
                insertOrUpdateNData(jsonObject.toString(), campaignId, flag, Table.NOTIFICATION_TABLE);
                return campaignId;
            } catch (Exception e) {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
                closeRead("InsertOrUpdateTask");
            }
            return "";
        }
    }


}
