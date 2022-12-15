package io.mob.resu.reandroidsdk;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
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
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;
import io.mob.resu.reandroidsdk.error.Log;

import static io.mob.resu.reandroidsdk.Util.getLauncherActivityName;

public class RePagerDialog extends DialogFragment implements NotificationActions {
    WebView webView;
    PlayerView video;
    ImageView img;
    ImageView leftArrow;
    ImageView rightArrow;
    TextView title;
    TextView noMedia;
    TextView message;
    ExoPlayer exoPlayer;
    LinearLayout actionLay;
    Button option1, option2;
    LinearLayout contentLay;
    RelativeLayout rootParent;
    ViewPager vpPager;
    ProgressBar progressBar;
    Bundle intent;
    JSONArray jsonArray;
    JSONArray finalCustomAction;
    int lastPosition = 0;
    ArrayList<RePagerFragment> rePagerFragments = new ArrayList<>();
    int style;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialog = null;
        try {
            intent = getArguments();
            dialog = new View(getActivity());
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

            final int sourceType = Integer.parseInt("" + intent.get("sourceType"));
            if (intent.containsKey("bannerStyle")) {
                style = Integer.parseInt("" + intent.get("bannerStyle"));
            } else {
                if (TextUtils.isEmpty(intent.getString("url").trim())) {
                    style = 4;
                }
            }
            final String dialogTitle = intent.getString("title");
            final String dialogMessage = intent.getString("body");
            final String url = intent.getString("url").trim();
            JSONArray customAction = null;
            boolean action = false;
            try {
                customAction = new JSONArray("" + intent.get("customActions"));
                if (customAction.length() > 0) {
                    action = true;
                }
            } catch (Exception ignored) {
                customAction = new JSONArray();
                Log.e("error", ignored.getMessage());
            }
            final boolean finalAction = action;
            finalCustomAction = customAction;
            boolean isContent = true;
            int layout = 0;
            if (intent.containsKey("isContent"))
                isContent = Boolean.parseBoolean("" + intent.get("isContent"));

            switch (style) {
                case 1:
                    layout = R.layout.inpage_notifications_large;
                    break;
                case 2:
                    layout = R.layout.inpage_notifications_medium;
                    break;
                case 3:
                    layout = R.layout.inpage_notifications_small_top;
                    break;
                case 4:
                    layout = R.layout.inpage_notifications_small_bottom;
                    break;
                default:
                    layout = R.layout.inpage_notifications_large;
                    break;
            }
            dialog = inflater.inflate(layout, container, false);
            vpPager = dialog.findViewById(R.id.re_viewpager);
            webView = dialog.findViewById(R.id.web_view);
            video = dialog.findViewById(R.id.video_view);
            img = dialog.findViewById(R.id.img_banner);
            actionLay = dialog.findViewById(R.id.actions);
            message = dialog.findViewById(R.id.descriptions);
            title = dialog.findViewById(R.id.title);
            progressBar = dialog.findViewById(R.id.progressbar);
            noMedia = dialog.findViewById(R.id.no_media);
            option1 = dialog.findViewById(R.id.option1);
            contentLay = dialog.findViewById(R.id.contentLay);
            rootParent = dialog.findViewById(R.id.rootParent);
            option2 = dialog.findViewById(R.id.option2);
            leftArrow = dialog.findViewById(R.id.iv_left);
            rightArrow = dialog.findViewById(R.id.iv_right);

            if (!finalAction) {
                actionLay.setVisibility(View.GONE);
            } else {
                if (finalCustomAction.length() > 0) {
                    try {

                        for (int i = 0; i < finalCustomAction.length(); i++) {
                            JSONObject jsonObject = finalCustomAction.getJSONObject(i);
                            TextView view;

                            if (i == 0) {
                                view = option1;
                                view.setVisibility(View.VISIBLE);
                                option2.setVisibility(View.GONE);
                            } else {
                                view = option2;
                                option2.setVisibility(View.VISIBLE);
                            }

                            if (jsonObject.has("actionTextColor")) {
                                if (!TextUtils.isEmpty(jsonObject.getString("actionTextColor")))
                                    view.setTextColor(Color.parseColor(jsonObject.getString("actionTextColor")));
                            }

                            if (jsonObject.has("actionBgColor")) {
                                if (!TextUtils.isEmpty(jsonObject.getString("actionBgColor")))
                                    view.setBackgroundColor(Color.parseColor(jsonObject.getString("actionBgColor")));

                            }

                            view.setText(jsonObject.getString("actionName"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (TextUtils.isEmpty(dialogTitle) || url.contains("EmailHeader")) {
                contentLay.setVisibility(View.GONE);
            } else {
                contentLay.setVisibility(View.VISIBLE);
            }

           /* if (!isContent) {
                contentLay.setVisibility(View.GONE);
            } else {*/
            title.setText(dialogTitle);
            message.setText(dialogMessage);

            if (intent.containsKey("titleColor")) {
                if (!TextUtils.isEmpty(intent.getString("titleColor"))) {
                    title.setTextColor(Color.parseColor(intent.getString("titleColor")));
                }
            }
            if (intent.containsKey("bodyColor")) {
                if (!TextUtils.isEmpty(intent.getString("bodyColor"))) {
                    message.setTextColor(Color.parseColor(intent.getString("bodyColor")));
                }
            }
            if (intent.containsKey("contentBgColor")) {
                if (!TextUtils.isEmpty(intent.getString("contentBgColor"))) {
                    rootParent.setBackgroundColor(Color.parseColor(intent.getString("contentBgColor")));
                }
            }
            // }
            webView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaClick(intent);

                }
            });
            video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaClick(intent);
                }
            });
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaClick(intent);
                }
            });
            option1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customActions(getActivity(), finalCustomAction, 0, getIntent(intent));

                }
            });
            option2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customActions(getActivity(), finalCustomAction, 1, getIntent(intent));
                }
            });
            leftArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vpPager.setCurrentItem(vpPager.getCurrentItem() - 1, true);
                }
            });
            rightArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vpPager.setCurrentItem(vpPager.getCurrentItem() + 1, true);
                }
            });
            boolean carousel = false;
            if (intent.containsKey("isCarousel")) {
                carousel = Boolean.parseBoolean("" + intent.get("isCarousel"));
            }

            if (!carousel) {
                if (!TextUtils.isEmpty(url)) {
                    noMedia.setVisibility(View.GONE);
                    switch (sourceType) {
                        // Web view
                        case 1:
                            webView.setVisibility(View.VISIBLE);
                            video.setVisibility(View.GONE);
                            img.setVisibility(View.GONE);
                            webView.getSettings().setLoadsImagesAutomatically(true);
                            webView.getSettings().setJavaScriptEnabled(true);
                            webView.setBackgroundColor(Color.TRANSPARENT);
                            webView.getSettings().setLoadWithOverviewMode(true);
                            webView.getSettings().setUseWideViewPort(true);
                            webView.addJavascriptInterface(new JavaScriptInterface(), "android");
                            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                            webView.setWebViewClient(new MyWebViewClient(progressBar));
                            webView.loadUrl(url);
                            break;

                        // video view
                        case 2:
                            webView.setVisibility(View.GONE);
                            video.setVisibility(View.VISIBLE);
                            initializePlayer(getActivity(), video, url);
                            img.setVisibility(View.GONE);
                            break;

                        // Gif view
                        case 3:
                            webView.setVisibility(View.GONE);
                            video.setVisibility(View.GONE);
                            img.setVisibility(View.VISIBLE);
                            Glide.with(getActivity())
                                    .load(url)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .into(img);
                            break;

                        // Image view
                        case 4:
                            webView.setVisibility(View.GONE);
                            video.setVisibility(View.GONE);
                            img.setVisibility(View.VISIBLE);

                            Glide.with(getActivity())
                                    .load(url)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .into(img);
                            break;


                        default:
                            break;

                    }
                } else {
                    noMedia.setVisibility(View.VISIBLE);
                }
            } else {
                webView.setVisibility(View.GONE);
                video.setVisibility(View.GONE);
                noMedia.setVisibility(View.GONE);
                img.setVisibility(View.GONE);
                vpPager.setVisibility(View.VISIBLE);
                leftArrow.setVisibility(View.VISIBLE);
                rightArrow.setVisibility(View.VISIBLE);
                jsonArray = new JSONArray(intent.getString("carousel"));
                RePagerAdapter adapter = new RePagerAdapter(getChildFragmentManager());
                rePagerFragments = new ArrayList<>();
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        RePagerFragment rePagerFragment = RePagerFragment.createInstance(jsonObject, this);
                        rePagerFragments.add(rePagerFragment);
                        adapter.addFragment("" + i, rePagerFragment);
                    }
                }
                vpPager.setAdapter(adapter);
                vpPager.setOffscreenPageLimit(4);
                vpPager.setClipToPadding(false);
                if (vpPager.getCurrentItem() == 0) {
                    rightArrow.setVisibility(View.VISIBLE);
                    leftArrow.setVisibility(View.GONE);
                }
            }

            // view Pager
            vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    setPageChangeContent(position);
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            View close = dialog.findViewById(R.id.iv_close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        new OfflineCampaignTrack(getActivity(), intent.getString(AppConstants.reApiParamId), "3", "Dismissed", true, null, null, DataNetworkHandler.getInstance()).execute();
                        mediaStopPlaying();
                        dialogDismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }


    private void setPageChangeContent(int position) {
        try {
            if (vpPager.getAdapter().getCount() - 1 == position) {
                rightArrow.setVisibility(View.GONE);
                leftArrow.setVisibility(View.VISIBLE);
            } else if (vpPager.getCurrentItem() == 0) {
                rightArrow.setVisibility(View.VISIBLE);
                leftArrow.setVisibility(View.GONE);
            } else {
                rightArrow.setVisibility(View.VISIBLE);
                leftArrow.setVisibility(View.VISIBLE);
            }
            JSONObject obj = jsonArray.getJSONObject(position);
            title.setText(obj.getString("title"));
            message.setText(obj.getString("body"));
            String url = obj.getString("url");

            if (obj.has("titleColor")) {
                if (!TextUtils.isEmpty(obj.optString("titleColor"))) {
                    title.setTextColor(Color.parseColor(obj.getString("titleColor")));
                }
            }
            if (obj.has("bodyColor")) {
                if (!TextUtils.isEmpty(obj.getString("bodyColor"))) {
                    message.setTextColor(Color.parseColor(obj.getString("bodyColor")));
                }
            }
            if (obj.has("contentBgColor")) {
                if (!TextUtils.isEmpty(obj.getString("contentBgColor"))) {
                    rootParent.setBackgroundColor(Color.parseColor(obj.getString("contentBgColor")));
                }
            }

            if (TextUtils.isEmpty(obj.getString("title")) || url.contains("EmailHeader")) {
                contentLay.setVisibility(View.GONE);
            } else {
                contentLay.setVisibility(View.VISIBLE);
            }

            try {
                finalCustomAction = obj.getJSONArray("customActions");
            } catch (Exception e1) {
                finalCustomAction = new JSONArray();
                actionLay.setVisibility(View.GONE);
                e1.printStackTrace();
            }
            if (finalCustomAction.length() > 0) {
                try {
                    actionLay.setVisibility(View.VISIBLE);
                    for (int i = 0; i < finalCustomAction.length(); i++) {
                        JSONObject jsonObject = finalCustomAction.getJSONObject(i);
                        Button view;

                        if (i == 0) {
                            view = option1;
                            view.setVisibility(View.VISIBLE);
                            option2.setVisibility(View.GONE);
                        } else {
                            view = option2;
                            option2.setVisibility(View.VISIBLE);
                        }

                        if (jsonObject.has("actionTextColor"))
                            view.setTextColor(Color.parseColor(jsonObject.getString("actionTextColor")));

                        if (jsonObject.has("actionBgColor"))
                            view.setBackgroundColor(Color.parseColor(jsonObject.getString("actionBgColor")));

                        view.setText(jsonObject.getString("actionName"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                actionLay.setVisibility(View.GONE);
            }
            if (rePagerFragments.size() > 0) {
                int sourceType = obj.getInt("sourceType");
                if (sourceType == 2) {
                    rePagerFragments.get(position).exoPlayer.setPlayWhenReady(true);
                }
                try {
                    ExoPlayer exoPlayer = rePagerFragments.get(lastPosition).exoPlayer;
                    if (lastPosition != position && exoPlayer != null) {
                        exoPlayer.setPlayWhenReady(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lastPosition = position;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mediaClick(Bundle intent) {
        try {
            new OfflineCampaignTrack(getActivity(), intent.getString(AppConstants.reApiParamId), "2", "Opened", false, null, null, DataNetworkHandler.getInstance()).execute();
            actions(getActivity(), getIntent(intent));
        } catch (Exception e) {

        }

    }


    private void actions(Activity context, Intent intent) {
        try {
            mediaStopPlaying();
            try {
                new DataBase(context).markRead(intent.getExtras().getString(AppConstants.reApiParamId), DataBase.Table.NOTIFICATION_TABLE, true);

            } catch (Exception e) {

            }
            intent.putExtra("notificationViewed", true);
            context.startActivity(intent);
            dialogDismiss();
        } catch (Exception er) {
            er.printStackTrace();
        }
    }

    private void customActions(Activity context, JSONArray finalCustomAction, int action, Intent intent) {

        try {

            mediaStopPlaying();
            Bundle bundle = intent.getExtras();

            if (action < finalCustomAction.length()) {
                JSONObject jsonObject = finalCustomAction.getJSONObject(action);
                try {
                    new DataBase(context).markRead(intent.getExtras().getString(AppConstants.reApiParamId), DataBase.Table.NOTIFICATION_TABLE, true);
                    new OfflineCampaignTrack(context, intent.getExtras().getString(AppConstants.reApiParamId), jsonObject.optString("actionId"), jsonObject.optString("actionName"), false, null, null, DataNetworkHandler.getInstance()).execute();

                } catch (Exception e) {

                }
                switch (jsonObject.optString("actionType")) {
                    case "call":

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        callIntent.setData(Uri.parse("tel:" + jsonObject.getString("url")));//change the number
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
                        break;
                    case "maybelater":
                        scheduleNotification(context, jsonObject.getString("duration"), intent.getExtras());
                        break;
                    case "dismiss":
                        break;
                    case "weburl":
                        String url = jsonObject.getString("url");
                        if (!url.startsWith("http://") && !url.startsWith("https://")) {
                            url = "http://" + url;
                        }
                        Intent sharingIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(sharingIntent);
                        break;
                    case "share":
                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TITLE, intent.getStringExtra("title"));
                        sendIntent.putExtra(Intent.EXTRA_TEXT, jsonObject.getString("url"));
                        sendIntent.setType("text/plain");
                        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Intent chooseIntent = Intent.createChooser(sendIntent, "Share");
                        chooseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //context.startActivity(chooseIntent);
                        actions(context, chooseIntent);
                        break;
                    case "smartlink":

                        Intent intent1; //= new Intent(context, Class.forName(jsonObject.getString("activityName").trim()));
                        try {
                            intent1 = new Intent(context, Class.forName(jsonObject.getString("activityName").trim()));
                        } catch (Exception e) {
                            intent1 = new Intent(context, Class.forName(getLauncherActivityName(context)));
                        }

                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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
                        break;
                }

            }


            dialogDismiss();


        } catch (Exception ignored) {
        }
    }

    private void initializePlayer(Context context, PlayerView exoPlayerView, String videoURL) {

        try {
            exoPlayer = new ExoPlayer.Builder(context).build();
            Uri videoURI = Uri.parse(videoURL);
            exoPlayerView.setPlayer(exoPlayer);

            MediaItem mediaItem = MediaItem.fromUri(videoURI);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.setPlayWhenReady(true);
        } catch (Exception e) {
            Log.e("MainAcvtivity", " exoplayer err  or " + e.toString());
        }
    }


    private void mediaStopPlaying() {
        try {
            if (rePagerFragments.size() > 0) {
                for (RePagerFragment rePagerFragment : rePagerFragments) {
                    if (rePagerFragment.exoPlayer != null)
                        rePagerFragment.exoPlayer.release();
                }
            }
            if (exoPlayer != null)
                exoPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void dialogDismiss() {
        try {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            dismiss();
        } catch (Exception e) {

        }

    }

    @Override
    public void mediaClick() {
        try {
            mediaClick(intent);
        } catch (Exception e) {

        }

    }

    @Override
    public void CTAClick(JSONArray jsonArray, int action) {

    }

    /**
     * Serve form webView MyWebViewClient
     */
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

                Log.e("mili", "" + delay);
                // bundle.putString("duration", "");
                // bundle.putString("actionName", "");
                notificationIntent.putExtras(bundle);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, getRandom(), notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);


                Long futureInMillis = SystemClock.elapsedRealtime() + delay;


                Log.e("Time", "" + futureInMillis);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
                }
            }
        } catch (Exception e) {
            Log.e("scheduleNotification", "" + e.getMessage());
        }

    }

    public int getRandom() {
        long lowerLimit = 123L;
        long upperLimit = 234L;
        Random r = new Random();
        long number = lowerLimit + ((int) (r.nextDouble() * (upperLimit - lowerLimit)));
        return (int) number;
    }


    @NonNull
    private Intent getIntent(Bundle map) {
        Intent intent1 = new Intent();
        try {

            try {
                intent1 = new Intent(getActivity(), Class.forName(map.getString("activityName").trim()));
            } catch (Exception e) {
                intent1 = new Intent(getActivity(), Class.forName(getLauncherActivityName(getActivity())));
            }

            Object[] parameters = map.keySet().toArray();
            JSONObject jsonObject = new JSONObject();
            for (Object o : parameters) {
                String key = "" + o;
                String value = "" + map.get(key);
                //Log.e("key", "" + o);
                //Log.e("values", "" + map.get(key));
                intent1.putExtra(key, value);
                jsonObject.put(key, value);
            }
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            return intent1;
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
        return intent1;

    }


    public class JavaScriptInterface {

        @JavascriptInterface
        public void ResPopupClose() {
            try {
                dismiss();
            } catch (Exception e) {

            }

        }


    }


}

