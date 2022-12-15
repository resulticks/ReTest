package io.mob.resu.reandroidsdk;

import static io.mob.resu.reandroidsdk.error.Util.isAppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;
import io.mob.resu.reandroidsdk.error.Log;
import io.mob.resu.reandroidsdk.error.Util;

public class ContentPublisherManager {

    static ContentPublisherManager contentPublisherManager;
    Activity activity;
    String[] screenNames;
    private ArrayList<RContentPublisher> captureIds = new ArrayList<>();

    public static ContentPublisherManager getInstance() {
        if (contentPublisherManager == null)
            contentPublisherManager = new ContentPublisherManager();
        return contentPublisherManager;
    }

    public void EnablePublisher(Activity mActivity) {

        try {
            // Disabled
            /*this.activity = mActivity;
            Log.e("EnablePublisher", "Start");
            if (isAppCompatActivity(mActivity)) {
                List<Fragment> fragmentList = ((FragmentActivity) mActivity).getSupportFragmentManager().getFragments();
                Log.e("List Of Fragements", "" + fragmentList.size());
                screenNames = new String[fragmentList.size() + 1];
                for (int j = 0; j < fragmentList.size(); j++) {
                    Log.e("List Of Fragements", "" + fragmentList.get(j).getClass().getSimpleName());
                    screenNames[j] = fragmentList.get(j).getClass().getSimpleName();
                }
                screenNames[fragmentList.size()] = mActivity.getClass().getSimpleName();
            }
            new GetContents(mActivity).execute();*/

        } catch (Exception e) {

        }
    }

    private ArrayList<RContentPublisher> getContentInjectionList() {

        ArrayList<RContentPublisher> rContentPublishers = new ArrayList<>();
        try {
            String webData = "";

        } catch (Exception e) {

        }

        return rContentPublishers;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initializeViews(JSONObject content, View container) {

        try {
            //android.util.Log.e("Views types :", "" + content.getString("type"));

            if (content.getString("type").equals("layout")) {

            } else if (content.getString("type").equals("textview") && Util.isTextView(container)) {
                TextView textView = (TextView) container;
                if (content.has("fontColor") && !content.getString("fontColor").equalsIgnoreCase(""))
                    textView.setTextColor(Color.parseColor(content.getString("fontColor")));
                if (content.has("content") && !content.getString("content").equalsIgnoreCase(""))
                    textView.setText(content.getString("content"));
                if (content.has("fontSize") && content.getInt("fontSize") != 0)
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, content.getInt("fontSize"));
                if (content.has("fontStyle") && content.getInt("fontStyle") != 0)
                    textView.setTypeface(textView.getTypeface(), content.getInt("fontStyle"));
                if (content.has("visibility") && content.getInt("visibility") != 0)
                    textView.setVisibility(content.getInt("visibility"));
                if (content.has("bgColor") && !content.getString("bgColor").equalsIgnoreCase(""))
                    textView.setBackgroundColor(Color.parseColor(content.getString("bgColor")));

            } else if (content.getString("type").equals("button") && Util.isButton(container)) {
                Button button = (Button) container;
                if (content.has("fontColor") && !content.getString("fontColor").equalsIgnoreCase(""))
                    button.setTextColor(Color.parseColor(content.getString("fontColor")));
                if (content.has("content") && !content.getString("content").equalsIgnoreCase(""))
                    button.setText(content.getString("content"));
                if (content.has("fontSize") && content.getInt("fontSize") != 0)
                    button.setTextSize(TypedValue.COMPLEX_UNIT_SP, content.getInt("fontSize"));
                if (content.has("fontStyle") && content.getInt("fontStyle") != 0)
                    button.setTypeface(button.getTypeface(), content.getInt("fontStyle"));
                if (content.has("visibility") && content.getInt("visibility") != 0)
                    button.setVisibility(content.getInt("visibility"));
                if (content.has("bgColor") && !content.getString("bgColor").equalsIgnoreCase(""))
                    button.setBackgroundColor(Color.parseColor(content.getString("bgColor")));

            } else if (content.getString("type").equals("imageview") && Util.isImageView(container)) {

                ImageView imageView = (ImageView) container;
                /* new EnableContentPublisher.DownloadImageFromInternet(imageView)
                        .execute(content.getString("content")); */
                Glide.with(activity)
                        .load(content.getString("content"))
                        .into(imageView);

                if (!content.getString("bgColor").equalsIgnoreCase(""))
                    imageView.setBackgroundColor(Color.parseColor(content.getString("bgColor")));

                if (content.has("visibility") && content.getInt("visibility") != 0)
                    imageView.setVisibility(content.getInt("visibility"));

            } else if (content.getString("type").equals("spinner") && Util.isSpinner(container)) {
                Spinner v1 = (Spinner) container;
                ArrayAdapter myAdap = (ArrayAdapter) v1.getAdapter(); //cast to an ArrayAdapter
                int spinnerPosition = myAdap.getPosition(content.getString("content"));
                v1.setSelection(spinnerPosition);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetContents extends AsyncTask<String, String, String> {
        Activity mActivity;

        GetContents(Activity activity) {
            mActivity = activity;
        }

        protected String doInBackground(String... urls) {
            try {
                captureIds = new DataBase(mActivity).getContentPublisher(DataBase.Table.REGISTER_VIEWS_TABLE, screenNames);
            } catch (Exception e) {
            }
            return "";
        }

        protected void onPostExecute(String result) {
            try {
                View view = mActivity.getWindow().getDecorView().getRootView();
                if (captureIds != null) {
                    for (RContentPublisher obj : captureIds) {
                        try {
                            View view1 = null;
                            int resID = mActivity.getResources().getIdentifier(obj.getId(), "id", mActivity.getPackageName());
                            view1 = view.findViewById(resID);
                            if (view1 != null) {
                                initializeViews(obj.getContent(), view1);
                            }
                        } catch (Exception e) {
                            ExceptionTracker.track(e);
                        }
                    }
                }
            } catch (Exception e) {

            }

        }
    }


}
