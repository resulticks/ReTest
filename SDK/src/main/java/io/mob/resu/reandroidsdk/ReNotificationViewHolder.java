package io.mob.resu.reandroidsdk;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;

import static io.mob.resu.reandroidsdk.Util.getLauncherActivityName;


public class ReNotificationViewHolder extends ReBaseViewHolder<RNotification> implements View.OnClickListener {

    TextView mTitle;
    TextView mSubTitle;
    ImageView mImg;
    Button mdelete;
    Button mOption1;
    Button mOption2;
    LinearLayout mContainer;
    RelativeLayout mMedia;
    RelativeLayout mContent;
    WebView mEDM;
    View mItem;
    ImageView mPlaceHolder;
    ProgressBar progressBar;

    private NotificationRecyclerAdapterListener listener;


    public ReNotificationViewHolder(View itemView) {
        super(itemView);
        bindHolder();
    }

    public ReNotificationViewHolder(View itemView, NotificationRecyclerAdapterListener listener) {
        super(itemView);
        this.listener = listener;
        bindHolder();
    }

    @Override
    void populateData() {

        try {
            if (data.isRead()) {
                mTitle.setTypeface(null, Typeface.BOLD);
                mSubTitle.setTypeface(null, Typeface.BOLD);
            } else {
                mTitle.setTypeface(null, Typeface.NORMAL);
                mSubTitle.setTypeface(null, Typeface.NORMAL);
            }

            if (data.getUrl().contains("EmailHeader")) {
                mContent.setVisibility(View.GONE);
            } else {
                mContent.setVisibility(View.VISIBLE);
            }

            mTitle.setText(data.getTitle() + " " + data.getSubTitle());
            mSubTitle.setText(data.getBody());
            if (!TextUtils.isEmpty(data.getTitleColor())) {
                mTitle.setTextColor(Color.parseColor(data.getTitleColor()));
            }
            if (!TextUtils.isEmpty(data.getBodyColor())) {
                mSubTitle.setTextColor(Color.parseColor(data.getBodyColor()));
            }

            if (!TextUtils.isEmpty(data.getContentBgColor())) {
                mContainer.setBackgroundColor(Color.parseColor(data.getContentBgColor()));
            }


            Log.e("URL", data.getUrl());
            if (!TextUtils.isEmpty(data.getUrl())) {
                if (data.getSourceType().equalsIgnoreCase("4") || data.getSourceType().equalsIgnoreCase("3")) {
                    Glide.with(itemView.getContext())
                            .load(data.getUrl())
                            .into(mImg);
                    mImg.setVisibility(View.VISIBLE);
                    mPlaceHolder.setVisibility(View.GONE);
                    mEDM.setVisibility(View.GONE);
                } else if (data.getSourceType().equalsIgnoreCase("1") || data.getSourceType().equalsIgnoreCase("2")) {

                    if (data.getSourceType().equalsIgnoreCase("1")) {
                        mImg.setVisibility(View.GONE);
                        mPlaceHolder.setVisibility(View.GONE);
                        mEDM.setVisibility(View.VISIBLE);
                        mEDM.getSettings().setLoadsImagesAutomatically(true);
                        mEDM.getSettings().setJavaScriptEnabled(true);
                        mEDM.setBackgroundColor(Color.TRANSPARENT);
                        mEDM.getSettings().setLoadWithOverviewMode(true);
                        mEDM.getSettings().setUseWideViewPort(true);
                        mEDM.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                        mEDM.setWebViewClient(new MyWebViewClient(progressBar));
                        mEDM.loadUrl(data.getUrl());
                    } else if (data.getSourceType().equalsIgnoreCase("2")) {
                        mImg.setVisibility(View.VISIBLE);
                        mPlaceHolder.setVisibility(View.VISIBLE);
                        mEDM.setVisibility(View.GONE);
                        Glide.with(itemView.getContext())
                                .load(data.getUrl())
                                .into(mImg);
                    }
                } else {
                    mMedia.setVisibility(View.GONE);
                    mEDM.setVisibility(View.GONE);
                    mPlaceHolder.setVisibility(View.GONE);
                }
                Log.e("Custom Params" + getAdapterPosition(), "" + data.getCustomParams());
            } else {
                mMedia.setVisibility(View.GONE);
            }

            try {
                JSONArray customAction = new JSONArray(data.getCustomActions());
                if (customAction.length() > 0) {
                    for (int i = 0; i < customAction.length(); i++) {
                        JSONObject jsonObject = customAction.getJSONObject(i);
                        Button view;
                        if (i == 0) {
                            view = mOption1;
                            view.setVisibility(View.VISIBLE);
                        } else {
                            view = mOption2;
                            mOption2.setVisibility(View.VISIBLE);
                        }

                        if (jsonObject.has("actionTextColor")) {
                            view.setTextColor(Color.parseColor(jsonObject.getString("actionTextColor")));
                        }
                        if (jsonObject.has("actionBgColor")) {
                            view.setBackgroundColor(Color.parseColor(jsonObject.getString("actionBgColor")));
                        }

                        view.setText(jsonObject.getString("actionName"));
                    }
                }
            } catch (Exception ignored) {
                io.mob.resu.reandroidsdk.error.Log.e("error", ignored.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindHolder() {
        try {
            mTitle = itemView.findViewById(R.id.tv_title);
            mSubTitle = itemView.findViewById(R.id.tv_sub_title);
            mItem = itemView.findViewById(R.id.list_item);
            mEDM = itemView.findViewById(R.id.iv_web_view);
            mPlaceHolder = itemView.findViewById(R.id.iv_image_place_holder);
            mImg = itemView.findViewById(R.id.iv_image);
            mdelete = itemView.findViewById(R.id.tv_delete);
            mOption1 = itemView.findViewById(R.id.option1);
            mOption2 = itemView.findViewById(R.id.option2);
            mContainer = itemView.findViewById(R.id.item);
            mContent = itemView.findViewById(R.id.content);
            mMedia = itemView.findViewById(R.id.banner);
            progressBar = itemView.findViewById(R.id.progressbar);
            mOption1.setOnClickListener(this);
            mOption2.setOnClickListener(this);
            mdelete.setOnClickListener(this);
            mContainer.setOnClickListener(this);
            mItem.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        try {
            if (id == R.id.option1) {
                JSONArray customAction = new JSONArray(data.getCustomActions());

                customActions((Activity) view.getContext(), customAction, 0, getIntent(view.getContext(), getBundle(data)));
            } else if (id == R.id.option2) {
                JSONArray customAction = new JSONArray(data.getCustomActions());
                customActions((Activity) view.getContext(), customAction, 1, getIntent(view.getContext(), getBundle(data)));
            } else if (id == R.id.tv_delete) {
                listener.onClickDelete(data, getAdapterPosition());
            } else if (id == R.id.item || id == R.id.list_item) {
                listener.onClick(data, getAdapterPosition());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private class MyWebViewClient extends WebViewClient {
        ProgressBar progressBar;
        MyWebViewClient(ProgressBar progressBar) {
            try {
                this.progressBar = progressBar;
                if (progressBar != null)
                    progressBar.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView webView, String url) {
            try {
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            webView.loadUrl("javascript:(function() { " +
                    "document.getElementsByClassName('main-wrapper')[0].style.display='none'; " + "})()");

        }
    }

    private void customActions(Activity context, JSONArray finalCustomAction, int action, Intent intent) {

        try {
            if (action < finalCustomAction.length()) {
                JSONObject jsonObject = finalCustomAction.getJSONObject(action);
                ReAndroidSDK.getInstance(context).readNotification(intent.getExtras().getString("campaignId"));
                new OfflineCampaignTrack(context, intent.getExtras().getString("campaignId"), jsonObject.optString("actionId"), "NOTIFICATION_"+jsonObject.optString("actionType").toLowerCase(), false, null, null, DataNetworkHandler.getInstance()).execute();
                Bundle bundle = intent.getExtras();

                switch (jsonObject.optString("actionType")) {
                    case "call":

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + jsonObject.getString("url")));
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CALL_PHONE}, 100);
                        } else {
                            //change the number
                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                Toast.makeText(context, "Please allow Call permission", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            context.startActivity(callIntent);
                        }
                        break;
                    case "maybelater":
                        //Toast.makeText(context, "Maybe Later Clicked", Toast.LENGTH_LONG).show();
                        scheduleNotification(context, jsonObject.getString("duration"), intent.getExtras());
                        break;
                    case "dismiss":
                        Toast.makeText(context, "Dismiss Clicked", Toast.LENGTH_LONG).show();
                        break;
                    case "weburl":
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.getString("url")));
                        context.startActivity(browserIntent);
                        break;
                    case "share":
                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TITLE, intent.getStringExtra("title"));
                        sendIntent.putExtra(Intent.EXTRA_TEXT, jsonObject.getString("url"));
                        sendIntent.setType("text/plain");
                        context.startActivity(Intent.createChooser(sendIntent, "Share"));
                        break;
                    case "smartlink":

                        Intent intent1;
                        try {
                            intent1 = new Intent(context, Class.forName(jsonObject.getString("activityName").trim()));
                        } catch (Exception e) {
                            intent1 = new Intent(context, Class.forName(getLauncherActivityName(context)));
                        }
                        bundle.putString("navigationScreen", jsonObject.getString("activityName"));
                        bundle.putString("customParams", jsonObject.getString("customParams"));
                        bundle.putString("category", jsonObject.getString("fragmentName"));
                        bundle.putString("fragmentName", jsonObject.getString("fragmentName"));
                        bundle.putString("MobileFriendlyUrl", jsonObject.getString("MobileFriendlyUrl"));
                        intent1.putExtras(bundle);
                        actions(context, intent1);
                        break;

                    case "resumejourney":

                        Intent intent2;
                        try {
                            intent2 = new Intent(context, Class.forName(intent.getExtras().getString("navigationScreen").trim()));
                        } catch (Exception e) {
                            intent2 = new Intent(context, Class.forName(getLauncherActivityName(context)));
                        }
                        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        bundle.putString("resumeJourney", Util.getResumeJourneyData(context));
                        intent2.putExtras(bundle);
                        actions(context, intent2);
                        break;

                    default:
                        Toast.makeText(context, jsonObject.optString("actionName")+" Clicked", Toast.LENGTH_LONG).show();
                        break;
                }

            }


        } catch (Exception ignored) {

        }
    }


    public void scheduleNotification(Context context, String duration, Bundle bundle) {

        try {
            if (!duration.isEmpty()) {
                Intent notificationIntent = new Intent(context, ScheduleNotification.class);


                String interval;
                long delay = 0;

                if (duration.contains("Minute(s)")) {
                    interval = duration.replace("Minute(s)", "").trim();
                    delay = TimeUnit.MINUTES.toMillis(Integer.parseInt(interval));
                } else if (duration.contains("Hour(s)")) {
                    interval = duration.replace("Hour(s)", "").trim();
                    delay = TimeUnit.HOURS.toMillis(Integer.parseInt(interval));
                } else if (duration.contains("Day(s)")) {
                    interval = duration.replace("Day(s)", "").trim();
                    delay = TimeUnit.DAYS.toMillis(Integer.parseInt(interval));
                }

                io.mob.resu.reandroidsdk.error.Log.e("mili", "" + delay);
                // bundle.putString("duration", "");
                // bundle.putString("actionName", "");
                notificationIntent.putExtras(bundle);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, getRandom(), notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                Long futureInMillis = SystemClock.elapsedRealtime() + delay;
                io.mob.resu.reandroidsdk.error.Log.e("Time", "" + futureInMillis);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
                }
            }
        } catch (Exception e) {
            io.mob.resu.reandroidsdk.error.Log.e("scheduleNotification", "" + e.getMessage());
        }

    }

    public int getRandom() {
        long lowerLimit = 123L;
        long upperLimit = 234L;
        Random r = new Random();
        long number = lowerLimit + ((int) (r.nextDouble() * (upperLimit - lowerLimit)));
        return (int) number;
    }

    private void actions(Activity context, Intent intent) {
        try {
            /*if (exoPlayer != null)
                exoPlayer.release();*/
            intent.putExtra("notificationViewed", true);
            context.startActivity(intent);
        } catch (Exception er) {
            er.printStackTrace();
        }
    }

    @NonNull
    private Bundle getBundle(RNotification data) {

        Bundle intent = new Bundle();
        JSONObject map = new JSONObject();
        try {
            map.put("body", data.getBody());
            map.put("title", data.getTitle());
            map.put("subTitle", data.getSubTitle());
            map.put("notificationImageUrl", data.getMobileFriendlyUrl());
            map.put("activityName", data.getActivityName());
            map.put("fragmentName", data.getFragmentName());
            map.put("campaignId", data.getCampaignId());
            map.put("customParams", data.getCustomParams());
            map.put("notificationId", data.getNotificationId());
            map.put("MobileFriendlyUrl", data.getMobileFriendlyUrl());
            map.put("customActions", data.getCustomActions());
            map.put("pushType", data.getPushType());
            map.put("bannerStyle", data.getBannerStyle());
            map.put("sourceType", data.getSourceType());
            map.put("channelName", data.getChannelName());
            map.put("channelID", data.getChannelID());
            map.put("ttl", data.getTtl());
            map.put("url", data.getUrl());
            map.put("tag", data.getTag());

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            Iterator<?> keys = map.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                intent.putString(key, map.getString(key));
            }
            return intent;
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
        return intent;

    }

    @NonNull
    private Intent getIntent(Context context, Bundle map) {
        Intent intent = new Intent();
        try {

            try {
                intent = new Intent(context, Class.forName(map.getString("activityName").trim()));
            } catch (Exception e) {
                intent = new Intent(context, Class.forName(getLauncherActivityName(context)));
            }
            intent.putExtras(map);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            return intent;
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
        return intent;

    }


}
