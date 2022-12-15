package io.mob.resu.reandroidsdk;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;
import io.mob.resu.reandroidsdk.error.Log;

import static io.mob.resu.reandroidsdk.Util.getLauncherActivityName;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;


public class AppWidgets {

    WebView webView;
    PlayerView video;
    ImageView img;
    TextView title;
    TextView noMedia;
    TextView message;
    ExoPlayer exoPlayer;
    LinearLayout actionLay;
    TextView option1, option2;
    CardView contentLay;
    private RatingBar ratingBar;
    private WebView banner;
    ViewPager vpPager;


    /**
     * Rating wise share option
     *
     * @param dialogTitle
     * @param dialogMessage
     * @param context
     */
    private void shareIntent(String dialogTitle, String dialogMessage, Activity context) {

        try {
            Intent sharingIntent;
            sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, dialogTitle);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, dialogMessage);
            context.startActivity(Intent.createChooser(sharingIntent, "Share With"));
        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
    }


    /**
     * Promotion Banner Campaign
     *
     * @param context
     * @param intent
     */
    public void showBannerDialog(final Activity context, final Intent intent) {

        try {
            final int sourceType = Integer.parseInt(intent.getStringExtra("sourceType"));
            if (sourceType != 0) {

                int animation = 0;
                if (intent.hasExtra("animation")) {
                    animation = Integer.parseInt(intent.getStringExtra("animation"));
                    switch (animation) {

                        case 1:
                            // fade
                            animation = R.style.fadeAnimation;
                            break;
                        case 2:
                            //  from left
                            animation = R.style.leftAnimation;
                            break;
                        case 3:
                            animation = R.style.sideUpDialogAnimation;
                            break;

                        default:

                            break;

                    }
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FragmentTransaction ft = null;
                            Fragment prev = null;
                            if (context instanceof FragmentActivity) {
                                FragmentActivity appCompatActivity = (FragmentActivity) context;
                                ft = appCompatActivity.getSupportFragmentManager().beginTransaction();
                                prev = appCompatActivity.getSupportFragmentManager().findFragmentByTag("dialog");
                                if (prev != null) {
                                    ft.remove(prev);
                                }
                                ft.addToBackStack(null);
                            }
                            RePagerDialog dialogFragment = new RePagerDialog();
                            dialogFragment.setArguments(intent.getExtras());
                            dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                            dialogFragment.setCancelable(false);
                            if (ft != null) {
                                dialogFragment.show(ft, "dialog");
                            }
                        } catch (Exception e) {

                        }
                    }

                });
                return;
            }


        } catch (Exception e) {
            ExceptionTracker.track(e);
        }

    }


    /**
     * Promotion Banner Campaign
     *
     * @param context
     * @param intent
     */
    public void showBannerDialog(final Activity context, final Bundle intent) {

        try {
            final int sourceType = Integer.parseInt(intent.getString("sourceType"));
            if (sourceType != 0) {

                int animation = 0;
                if (intent.containsKey("animation")) {
                    animation = Integer.parseInt(intent.getString("animation"));
                    switch (animation) {

                        case 1:
                            // fade
                            animation = R.style.fadeAnimation;
                            break;
                        case 2:
                            //  from left
                            animation = R.style.leftAnimation;
                            break;
                        case 3:
                            animation = R.style.sideUpDialogAnimation;
                            break;

                        default:

                            break;

                    }
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FragmentTransaction ft = null;
                            Fragment prev = null;
                            if (context instanceof FragmentActivity) {
                                FragmentActivity appCompatActivity = (FragmentActivity) context;
                                ft = appCompatActivity.getSupportFragmentManager().beginTransaction();
                                prev = appCompatActivity.getSupportFragmentManager().findFragmentByTag("dialog");
                                if (prev != null) {
                                    ft.remove(prev);
                                }
                                ft.addToBackStack(null);
                            }
                            RePagerDialog dialogFragment = new RePagerDialog();
                            dialogFragment.setArguments(intent);
                            dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                            dialogFragment.setCancelable(false);
                            if (ft != null) {
                                dialogFragment.show(ft, "dialog");
                            }
                        } catch (Exception e) {

                        }
                    }
                });
                return;
            }


        } catch (Exception e) {
            ExceptionTracker.track(e);
        }

    }


    private void actions(Activity context, Intent intent, Dialog dialog) {
        try {
            if (exoPlayer != null)
                exoPlayer.release();
            try {
                new DataBase(context).markRead(intent.getExtras().getString(AppConstants.reApiParamId), DataBase.Table.NOTIFICATION_TABLE, true);

            } catch (Exception e) {

            }
            intent.putExtra("notificationViewed", true);
            context.startActivity(intent);
            dialog.dismiss();
        } catch (Exception er) {
            er.printStackTrace();
        }
    }

    private void customActions(Activity context, Dialog dialog, JSONArray finalCustomAction, int action, Intent intent) {

        try {

            if (exoPlayer != null)
                exoPlayer.release();

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
                        callIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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
                        actions(context, chooseIntent, dialog);
                        break;
                    case "smartlink":

                        Intent intent1;
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
                        actions(context, intent1, dialog);
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
                        actions(context, intent2, dialog);
                        break;

                    default:
                        break;
                }

            }

            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

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
            Log.e("MainAcvtivity", " exoplayer error " + e.toString());
        }
    }

    private void getAppIcon(Context context, NotificationCompat.Builder builder) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int icon = Util.getAppIcon(context, true);
                int iconColor = Util.getAppIconColor(context);
                if (icon != 0)
                    builder.setSmallIcon(icon);
                if (iconColor != 0) {
                    String val = "#" + Integer.toHexString(ContextCompat.getColor(context, iconColor));
                    builder.setColor(Color.parseColor(val));
                }

            } else {
                int icon = Util.getAppIcon(context, false);
                if (icon != 0)
                    builder.setSmallIcon(icon);
            }
        } catch (Exception e) {
            android.util.Log.e("getIcon", "" + e.getMessage());
        }
    }


   /* private MediaSource buildMediaSource(Uri uri) {
        try {
            return new ExtractorMediaSource.Factory(
                    new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                    createMediaSource(uri);
        } catch (Exception e) {
            return null;
        }

    }
*/

    /**
     * Server Update
     *
     * @param id
     * @param comments
     */
    private void userRating(String id, String comments) {

        try {
            JSONObject userDetail;
            userDetail = new JSONObject();
            userDetail.put("id", id);
            userDetail.put("rating", ratingBar.getRating());
            userDetail.put("status", "2");
            userDetail.put("comments", comments);
            // new DataExchanger("campaignTracking", userDetail.toString(), IResponseListener, AppConstants.SDK_USER_REGISTER).execute();

        } catch (Exception e) {
            ExceptionTracker.track(e);
        }

    }

    /**
     * Serve form webView MyWebViewClient
     */
    private class MyWebViewClient extends WebViewClient {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest url) {
            return super.shouldOverrideUrlLoading(view, url);

        }

        @Override
        public void onPageFinished(WebView webView, String url) {

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


}
