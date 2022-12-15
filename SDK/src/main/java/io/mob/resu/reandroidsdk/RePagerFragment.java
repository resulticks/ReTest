package io.mob.resu.reandroidsdk;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

import org.json.JSONObject;

import io.mob.resu.reandroidsdk.error.Log;

public class RePagerFragment extends Fragment {
    private JSONObject jsonObject;
    private final String mUrl = "";
    WebView webView;
    PlayerView video;
    ImageView img;
    TextView noMedia;
    ExoPlayer exoPlayer;
    NotificationActions notificationActions;
    ProgressBar progressBar;

    public static RePagerFragment createInstance(JSONObject jsonObject, NotificationActions notificationActions) {
        RePagerFragment fragment = new RePagerFragment();
        fragment.jsonObject = jsonObject;
        fragment.notificationActions = notificationActions;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.vp_notification_item, container, false);
        try {
            img = v.findViewById(R.id.vp_img_banner);
            progressBar = v.findViewById(R.id.progressbar);
            webView = v.findViewById(R.id.vp_web_view);
            video = v.findViewById(R.id.vp_video_view);
            noMedia = v.findViewById(R.id.vp_no_media);
            final int sourceType = Integer.parseInt(jsonObject.getString("sourceType"));
            final String url = jsonObject.getString("url").trim();
            webView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    notificationActions.mediaClick();

                }
            });
            video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    notificationActions.mediaClick();
                }
            });
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    notificationActions.mediaClick();
                }
            });

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
                        img.setBackgroundColor(Color.BLACK);
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


        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

    private void initializePlayer(Context context, PlayerView exoPlayerView, String videoURL) {

        try {

             exoPlayer = new ExoPlayer.Builder(context).build();
            Uri videoURI = Uri.parse(videoURL);
            exoPlayerView.setPlayer(exoPlayer);

            MediaItem mediaItem = MediaItem.fromUri(videoURI);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();

        } catch (Exception e) {
            Log.e("MainAcvtivity", " exoplayer error " + e.toString());
        }
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


}