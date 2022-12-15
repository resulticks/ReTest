package io.mob.resu.reandroidsdk;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;
import io.mob.resu.reandroidsdk.error.Log;


public class ReNotificationFragment extends Fragment {

    RecyclerView mNotificationView;
    RecyclerView mNotificationClassifications;
    TextView mTvNoDataFound;
    SwipeRefreshLayout mParent;
    ReNotificationAdapter adapter;
    ReClassificationAdapter reClassificationAdapter;
    SearchView searchView;
    ArrayList<ClassificationModel> classifications = new ArrayList<>();
    private ArrayList<RNotification> notificationList = new ArrayList<>();

    public ReClassifications reClassifications = new ReClassifications() {
        @Override
        public void itemClick(ClassificationModel model) {
            try {
                String selecteditem = model.getChannelName();
                ArrayList<RNotification> notificationList1 = getNotificationList();
                notificationList = new ArrayList<>();
                if (!model.getChannelName().equalsIgnoreCase("All")) {
                    for (RNotification notification : notificationList1) {
                        if (notification.getChannelName().equalsIgnoreCase(model.getChannelName()))
                            notificationList.add(notification);
                    }
                } else {
                    notificationList = notificationList1;
                }
                for (int i = 0; i < classifications.size(); i++) {
                    classifications.get(i).setSelected(classifications.get(i).getChannelName().equalsIgnoreCase(selecteditem));
                }
                reClassificationAdapter.resetItems(classifications);
                adapter = new ReNotificationAdapter(notificationList, listener);
                mNotificationView.setLayoutManager(new LinearLayoutManager(getActivity()));
                mNotificationView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public NotificationRecyclerAdapterListener listener = new NotificationRecyclerAdapterListener() {
        @Override
        public void onClickDelete(RNotification notificationDetails, int position) {
            try {
                if (notificationList.size() > position) {
                    notificationList.remove(position);
                    ReAndroidSDK.getInstance(getActivity()).deleteNotificationByCampaignId(notificationDetails.getCampaignId());
                    if (notificationList.size() == 0) {
                        adapter.resetItems(notificationList);
                        mTvNoDataFound.setVisibility(View.VISIBLE);
                    } else {
                        adapter.notifyDataSetChanged();
                        mTvNoDataFound.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onClick(RNotification notificationDetails, int position) {
            try {
                ReAndroidSDK.getInstance(getActivity()).readNotification(notificationDetails.getCampaignId());
                if (notificationDetails.getPushType().equalsIgnoreCase("2")) {
                    new AppWidgets().showBannerDialog(getActivity(), getBundle(notificationDetails));
                } else {
                    Intent intent = null;
                    try {
                        intent = new Intent(getActivity(), Class.forName(notificationDetails.getActivityName()));
                        intent.putExtra("activityName", notificationDetails.getActivityName());
                        intent.putExtra("navigationScreen", notificationDetails.getActivityName());
                        intent.putExtra("fragmentName", notificationDetails.getFragmentName());
                        intent.putExtra("category", notificationDetails.getFragmentName());
                        intent.putExtra("customParams", notificationDetails.getCustomParams());
                        intent.putExtra("MobileFriendlyUrl", notificationDetails.getMobileFriendlyUrl());
                        getActivity().startActivity(intent);
                        getActivity().finish();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.re_fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
    }

    private void initializeViews(View view) {
        try {
            mNotificationView = view.findViewById(R.id.rl_notification_list);
            mNotificationClassifications = view.findViewById(R.id.re_classification);
            searchView = view.findViewById(R.id.searchView);
            mTvNoDataFound = view.findViewById(R.id.tv_no_data_found);
            mParent = view.findViewById(R.id.swipe_refresh_layout);
            searchView.setBackgroundColor(Color.WHITE);
            searchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {/**/
                    searchView.setIconified(false);
                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.e("onQueryTextSubmit", query);
                    try {

                        ArrayList<RNotification> rNotifications = new ArrayList<>();

                        if (!TextUtils.isEmpty(query)) {
                            for (RNotification notification : notificationList) {

                                if (filtered(notification, query)) {
                                    rNotifications.add(notification);
                                }
                            }
                            adapter.resetItems(rNotifications);
                        } else {
                            adapter.resetItems(notificationList);
                        }

                    } catch (Exception e) {

                    }

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {

                    try {

                        ArrayList<RNotification> rNotifications = new ArrayList<>();

                        if (!TextUtils.isEmpty(query)) {
                            for (RNotification notification : notificationList) {
                                if (filtered(notification, query)) {
                                    rNotifications.add(notification);
                                }
                            }
                            adapter.resetItems(rNotifications);
                        } else {
                            adapter.resetItems(notificationList);
                        }

                    } catch (Exception e) {

                    }
                    return false;
                }
            });
            loadData();
            mParent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    try {
                        loadData();
                        mParent.setRefreshing(false);
                    } catch (Exception e) {

                    }

                }
            });
        } catch (Exception e) {
        }
    }

    private void loadData() {
        try {
            adapter = new ReNotificationAdapter(getNotificationList(), listener);
            mNotificationView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mNotificationView.setAdapter(adapter);

            reClassificationAdapter = new ReClassificationAdapter(classifications, reClassifications);
            mNotificationClassifications.setAdapter(reClassificationAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
            mNotificationClassifications.setLayoutManager(linearLayoutManager);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList<RNotification> getNotificationList() {
        try {
            notificationList = ReAndroidSDK.getInstance(getActivity()).getNotifications();
            if (notificationList == null || notificationList.size() == 0) {
                notificationList = new ArrayList<>();
                mTvNoDataFound.setVisibility(View.VISIBLE);
                mNotificationClassifications.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);
            } else {
                classifications = new ArrayList<>();
                ArrayList<String> channels = new ArrayList<>();
                for (RNotification rNotification : notificationList) {
                    Log.e("Inbox Classification", rNotification.getChannelName());
                    if (!TextUtils.isEmpty(rNotification.getChannelName())) {
                        if (!channels.contains(rNotification.getChannelName()))
                            channels.add(rNotification.getChannelName());
                    }
                }
                ClassificationModel model1 = new ClassificationModel();
                model1.setChannelName("All");
                model1.setSelected(true);
                classifications.add(model1);

                for (String channelName : channels) {
                    ClassificationModel model = new ClassificationModel();
                    model.setChannelName(channelName);
                    classifications.add(model);
                }

                mTvNoDataFound.setVisibility(View.GONE);
                mNotificationClassifications.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.VISIBLE);
            }

            Collections.reverse(notificationList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notificationList;
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
            map.put("isCarousel", data.getIsCarousel());
            map.put("carousel", data.getCarousel());
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

    private boolean filtered(RNotification notification, String query) {

        try {

            query = query.toLowerCase();

            return notification.getTitle().toLowerCase().startsWith(query) ||
                    notification.getSubTitle().toLowerCase().startsWith(query) ||
                    notification.getBody().toLowerCase().startsWith(query) ||
                    notification.getTag().toLowerCase().startsWith(query) ||
                    notification.getChannelName().toLowerCase().startsWith(query);

        } catch (Exception e) {
            return false;

        }

    }


    private void getListCampaignType(String channelType) {
        try {
            ArrayList<RNotification> rNotifications = new ArrayList<>();
            if (!TextUtils.isEmpty(channelType)) {
                for (RNotification notification : notificationList) {
                    if (channelType.equalsIgnoreCase(notification.getChannelName())) {
                        rNotifications.add(notification);
                    }
                }
                adapter.resetItems(rNotifications);
            }
        } catch (Exception e) {

        }

    }


}
