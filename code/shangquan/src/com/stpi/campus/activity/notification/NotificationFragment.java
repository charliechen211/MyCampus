package com.stpi.campus.activity.notification;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.stpi.campus.entity.merchant.SubscribeMerchantInfo;
import com.stpi.campus.entity.merchant.SubscribeMerchantInfoHelper;
import com.stpi.campus.task.ShareTask;
import com.stpi.campus.R;
import com.stpi.campus.activity.RefreshableFragment;
import com.stpi.campus.activity.merchant.MerchantDetailActivity;
import com.stpi.campus.entity.dynamicShare.AroundShareInfo;
import com.stpi.campus.entity.dynamicShare.ShareInfoHelper;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.util.HttpListGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 13-12-11.
 */
public class NotificationFragment extends RefreshableFragment {

    private View view = null;
    private TabHost tabHost = null;

    private ListView nearbyNewsList = null;
    private ListView friendNewsList = null;
    private ListView subscribeNewsList = null;
    private TextView commentText = null;

    private List<AroundShareInfo> around_infos = null;
    private List<AroundShareInfo> friend_infos = null;
    private List<SubscribeMerchantInfo> subscribe_infos = null;

    private ProgressBar progressBar = null;
    private String shop_picture_head = null;
    private String item_picture_head = null;
    private String subscribe_picture_head = null;

    private ShareTask shareTask = null;
    private LoadSubscribeTask subscribeTask = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_notification, container, false);

        shop_picture_head = this.getString(R.string.shop_picture_head);
        item_picture_head = this.getString(R.string.item_picture_head);
        subscribe_picture_head = this.getString(R.string.subscribe_picture_head);

        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        tabHost = (TabHost) view.findViewById(R.id.tabHost);
        tabHost.setup();

        tabHost.addTab(
                tabHost.newTabSpec("tab1").setIndicator("订阅信息").setContent(R.id.subscribe_news_list)
        );
        tabHost.addTab(
                tabHost.newTabSpec("tab2").setIndicator("好友动态").setContent(R.id.friend_news_list)
        );
        tabHost.addTab(
                tabHost.newTabSpec("tab3").setIndicator("商圈动态").setContent(R.id.nearby_news_list)
        );

        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextSize(15);
        }

        nearbyNewsList = (ListView) view.findViewById(R.id.nearby_news_list);
        friendNewsList = (ListView) view.findViewById(R.id.friend_news_list);
        subscribeNewsList = (ListView) view.findViewById(R.id.subscribe_news_list);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        progressBar.setVisibility(View.VISIBLE);
//        new LoadSubscribeTask().execute(UserInfo.userId);
//        new LoadAroundTask().execute(UserInfo.userId, "1");
//        new LoadFriendTask().execute(UserInfo.userId, "1");
    }

    @Override
    public void refresh() {
        progressBar.setVisibility(View.VISIBLE);
        new LoadSubscribeTask().execute(UserInfo.userId);
        new LoadAroundTask().execute(UserInfo.userId);
        new LoadFriendTask().execute(UserInfo.userId, "1");
    }

    private void showShareDialog(final int share_type, final int share_num) {
        LayoutInflater inflater = NotificationFragment.this.getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_share,
                (ViewGroup) view.findViewById(R.id.share_dialog));

        commentText = (TextView) layout.findViewById(R.id.shareComment);

//        String[] locations = {"肯德基", "麦当劳", "棒约翰"};
//        Spinner spinner = (Spinner) layout.findViewById(R.id.locationInfo);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(NotificationFragment.this.getActivity(),android.R.layout.simple_spinner_item, locations);
//        //设置下拉列表的风格
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        //将adapter 添加到spinner中
//        spinner.setAdapter(adapter);
//        //设置默认值
//        spinner.setVisibility(View.VISIBLE);

        new AlertDialog.Builder(NotificationFragment.this.getActivity()).setTitle("分享").setView(layout)
                .setPositiveButton("分享", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        attemptShare(share_type, share_num);
//                        Toast.makeText(NotificationFragment.this.getActivity(), "分享成功", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    private void attemptShare(int share_type, int share_num) {
        if (shareTask != null)
            return;
        progressBar.setVisibility(View.VISIBLE);

        String userId = UserInfo.userId;
        String entityId = String.valueOf(share_num);
        String typeId = String.valueOf(share_type);
        String content = commentText.getText().toString();
        String shareTo = "2";

        shareTask = new ShareTask(NotificationFragment.this.getActivity(), progressBar);
        shareTask.execute(userId, entityId, typeId, content, shareTo);
    }

    class LoadAroundTask extends
            AsyncTask<String, Integer, List<Map<String, Object>>> {
        @Override
        protected List<Map<String, Object>> doInBackground(String... params) {
            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();

            String url = NotificationFragment.this
                    .getString(R.string.around_share_head);
            url += "?userId=" + params[0];
            url += "&circleId=" + String.valueOf(UserInfo.circleId);

            ShareInfoHelper tmp = new HttpListGetter<ShareInfoHelper>()
                    .getFromUrl(url, ShareInfoHelper.class);

            if (tmp == null || tmp.getState().equals("fail"))
                return res;

            around_infos = tmp.getResults();
            System.out.println("result get = " + around_infos.size());

            if (around_infos == null)
                return res;


            for (AroundShareInfo share : around_infos) {
                Map<String, Object> line = new HashMap<String, Object>();
                line.put("name", share.getUserName());
                line.put("averageRate", Float.valueOf(share.getRate()));
                line.put("place", (share.getEntityType() == 1) ? share.getMerchantName() : share.getItemName());
                line.put("time", share.getTimestamp());
                line.put("comment", share.getContent());
                line.put("type", share.getEntityType());
                if (share.getPicture() != null && share.getPicture().length() > 0)
                    line.put("picture", ((share.getEntityType() == 1) ? shop_picture_head : item_picture_head) + share.getPicture());
                else
                    line.put("picture", ((share.getEntityType() == 1) ? shop_picture_head : item_picture_head) + "000000.jpg");

                res.add(line);
            }

            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {
            SimpleAdapter adapter = new SimpleAdapter(NotificationFragment.this.getActivity(), data,
                    R.layout.aroundactivity_list_item, new String[]{"name", "averageRate",
                    "place", "time", "comment", "type", "picture"}, new int[]{
                    R.id.aroundName, R.id.ratingBar, R.id.place, R.id.time, R.id.comment, R.id.entity_type, R.id.aroundImageView}
            );

            adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    if ((view instanceof RatingBar) && (data instanceof Float)) {
                        RatingBar rb = (RatingBar) view;
                        Float r = (Float) data;
                        rb.setRating(r);
                        return true;
                    } else if ((view instanceof TextView) && (data instanceof Integer)) {
                        TextView typeView = (TextView) view;
                        Integer typeValue = (Integer) data;
                        if (typeValue == 1) {
                            typeView.setText("店");
                            typeView.setTextColor(getResources().getColor(R.color.tsuyukusa));
                        } else if (typeValue == 2) {
                            typeView.setText("菜");
                            typeView.setTextColor(getResources().getColor(R.color.byakuroku));
                        } else {
                            typeView.setVisibility(View.INVISIBLE);
                        }
                        return true;
                    } else if ((view instanceof ImageView) && (data instanceof String)) {
                        ImageView imageView = (ImageView) view;
                        String imageUri = (String) data;
                        ImageLoader imageLoader = ImageLoader.getInstance();
                        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().build();
                        imageLoader.displayImage(imageUri, imageView, options);
                        return true;
                    }
                    return false;
                }
            });
            nearbyNewsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    showShareDialog(around_infos.get(i).getEntityType(), around_infos.get(i).getEntityId());
                }
            });
            nearbyNewsList.setAdapter(adapter);
            progressBar.setVisibility(View.INVISIBLE);
        }

    }

//
//    private List<Map<String, Object>> getSubscribeData() {
//        List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
//
//        Map<String, Object> line = null;
//
//        line = new HashMap<String, Object>();
//        line.put("title", "麦当劳");
//        line.put("content", "回馈客户全场七折起");
//        res.add(line);
//
//        line = new HashMap<String, Object>();
//        line.put("title", "麦当劳");
//        line.put("content", "两人同行有人免单");
//        res.add(line);
//
//        line = new HashMap<String, Object>();
//        line.put("title", "麦当劳");
//        line.put("content", "超辣鸡腿堡新上市优惠活动");
//        res.add(line);
//
//        return res;
//    }

    class LoadSubscribeTask extends AsyncTask<String, Integer, List<Map<String, Object>>> {

        @Override
        protected List<Map<String, Object>> doInBackground(String... args) {

            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
            String url = NotificationFragment.this.getString(R.string.subscribe_merchant_list_head);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userId", args[0]));

            SubscribeMerchantInfoHelper subscribe_tmp = new HttpListGetter<SubscribeMerchantInfoHelper>()
                    .getFromUrl(url, SubscribeMerchantInfoHelper.class, params);

            if (subscribe_tmp == null || subscribe_tmp.getState().equals("fail"))
                return res;


            subscribe_infos = subscribe_tmp.getResults();

            if (subscribe_infos == null)
                return res;

            System.out.println("result get = " + subscribe_infos.size());

            for (SubscribeMerchantInfo subscribe : subscribe_infos) {
                Map<String, Object> line = new HashMap<String, Object>();

                if (subscribe.getMerchantName() != null)
                    line.put("merchant_name", subscribe.getMerchantName());
                else
                    line.put("merchant_name", "");
                if (subscribe.getFromdate() != null)
                    line.put("time", subscribe.getFromdate());
                else
                    line.put("time", "");
                if (subscribe.getContent() != null) {
                    if (subscribe.getContent().length() < 20)
                        line.put("content", subscribe.getContent());
                    else
                        line.put("content", subscribe.getContent().substring(0, 17) + "...");
                } else
                    line.put("content", "");
                if (subscribe.getPicture() != null && subscribe.getPicture().length() > 0)
                    line.put("picture", subscribe_picture_head + subscribe.getPicture());
                else
                    line.put("picture", subscribe_picture_head + "000000.jpg");
                res.add(line);
            }

            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {
            subscribeTask = null;
            progressBar.setVisibility(View.INVISIBLE);

            SimpleAdapter sa = new SimpleAdapter(NotificationFragment.this.getActivity(), data, R.layout.subscribe_news_item,
                    new String[]{"picture", "merchant_name", "content", "time"},
                    new int[]{R.id.news_image, R.id.news_title, R.id.news_content, R.id.news_time});
            sa.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    if ((view instanceof ImageView) && (data instanceof String)) {
                        ImageView imageView = (ImageView) view;
                        String imageUri = (String) data;
                        ImageLoader imageLoader = ImageLoader.getInstance();
                        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().build();
                        imageLoader.displayImage(imageUri, imageView, options);
                        return true;
                    }
                    return false;
                }
            });
            subscribeNewsList.setAdapter(sa);
            subscribeNewsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(NotificationFragment.this.getActivity(),
                            MerchantDetailActivity.class);
                    intent.putExtra("shop_id", String.valueOf(subscribe_infos.get(i).getMerchantId()));
                    startActivity(intent);
                }
            });
        }

        @Override
        protected void onCancelled() {
            subscribeTask = null;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    class LoadFriendTask extends
            AsyncTask<String, Integer, List<Map<String, Object>>> {
        @Override
        protected List<Map<String, Object>> doInBackground(String... params) {
            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();

            String url = NotificationFragment.this
//                    .getString(R.string.around_share_head);
                    .getString(R.string.friend_share_head);
            url += "?userId=" + params[0];
            url += "&circleId=" + params[1];

            ShareInfoHelper tmp = new HttpListGetter<ShareInfoHelper>()
                    .getFromUrl(url, ShareInfoHelper.class);

            if (tmp == null || tmp.getState().equals("fail")) {
//                Toast.makeText(NotificationFragment.this.getActivity(), "获取异常", Toast.LENGTH_SHORT).show();
                return res;
            }

            System.out.println("aa - " + tmp.getResults().size());
            friend_infos = tmp.getResults();
            System.out.println("result get = " + friend_infos.size());

            if (friend_infos == null)
                return res;

            System.out.println("result get = " + friend_infos.size());

            for (AroundShareInfo share : friend_infos) {
                Map<String, Object> line = new HashMap<String, Object>();
                line.put("name", share.getUserName());
                line.put("averageRate", Float.valueOf(share.getRate()));
                line.put("place", (share.getEntityType() == 1) ? share.getMerchantName() : share.getItemName());
                line.put("time", share.getTimestamp());
                line.put("comment", share.getContent());
                line.put("type", share.getEntityType());
                if (share.getPicture() != null && share.getPicture().length() > 0)
                    line.put("picture", ((share.getEntityType() == 1) ? shop_picture_head : item_picture_head) + share.getPicture());
                else
                    line.put("picture", ((share.getEntityType() == 1) ? shop_picture_head : item_picture_head) + "000000.jpg");
                res.add(line);
            }

            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {
            SimpleAdapter sa_friend = new SimpleAdapter(NotificationFragment.this.getActivity(), data, R.layout.aroundactivity_list_item,
                    new String[]{"name", "averageRate",
                            "place", "time", "comment", "type", "picture"},
                    new int[]{R.id.aroundName, R.id.ratingBar, R.id.place, R.id.time, R.id.comment, R.id.entity_type, R.id.aroundImageView}
            );
            friendNewsList.setAdapter(sa_friend);
            sa_friend.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    if ((view instanceof RatingBar) && (data instanceof Float)) {
                        RatingBar rb = (RatingBar) view;
                        Float r = (Float) data;
                        rb.setRating(r);
                        return true;
                    } else if ((view instanceof TextView) && (data instanceof Integer)) {
                        TextView typeView = (TextView) view;
                        Integer typeValue = (Integer) data;
                        if (typeValue == 1) {
                            typeView.setText("店");
                            typeView.setTextColor(getResources().getColor(R.color.tsuyukusa));
                        } else if (typeValue == 2) {
                            typeView.setText("菜");
                            typeView.setTextColor(getResources().getColor(R.color.byakuroku));
                        } else {
                            typeView.setVisibility(View.INVISIBLE);
                        }
                        return true;
                    } else if ((view instanceof ImageView) && (data instanceof String)) {
                        ImageView imageView = (ImageView) view;
                        String imageUri = (String) data;
                        ImageLoader imageLoader = ImageLoader.getInstance();
                        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().build();
                        imageLoader.displayImage(imageUri, imageView, options);
                        return true;
                    }
                    return false;
                }
            });
            friendNewsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    showShareDialog(friend_infos.get(i).getEntityType(), friend_infos.get(i).getEntityId());
                }
            });
            friendNewsList.setAdapter(sa_friend);
            progressBar.setVisibility(View.INVISIBLE);
        }

    }
}