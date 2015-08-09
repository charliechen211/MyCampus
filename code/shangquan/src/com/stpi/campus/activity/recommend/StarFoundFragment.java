package com.stpi.campus.activity.recommend;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.stpi.campus.activity.personalService.SearchMerchantActivity;
import com.stpi.campus.entity.item.RecItemInfo;
import com.stpi.campus.entity.item.RecItemInfoHelper;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.R;
import com.stpi.campus.activity.RefreshableFragment;
import com.stpi.campus.activity.item.ItemDetailActivity;
import com.stpi.campus.activity.merchant.MerchantDetailActivity;
import com.stpi.campus.entity.merchant.RecMerchantInfo;
import com.stpi.campus.entity.merchant.RecMerchantInfoHelper;
import com.stpi.campus.util.HttpListGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cyc on 13-12-12.
 */
public class StarFoundFragment extends RefreshableFragment {

    private View view = null;

    private ListView merchantListView = null;
    private ListView itemListView = null;
    private Button moreButton = null;
    private List<RecMerchantInfo> merchant_infos = null;
    private List<RecItemInfo> item_infos = null;

    private String shop_picture_head;
    private String item_picture_head;

    private ProgressBar progressBar = null;
    private TabHost tabHost = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_star_find, container, false);

        shop_picture_head = this.getString(R.string.shop_picture_head);
        item_picture_head = this.getString(R.string.item_picture_head);

        merchantListView = (ListView) view.findViewById(R.id.merchant_recommend_items);
        itemListView = (ListView) view.findViewById(R.id.item_recommend_items);
        moreButton = (Button) view.findViewById(R.id.moreButton);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StarFoundFragment.this.getActivity(),
                        SearchMerchantActivity.class);
                intent.putExtra("search", "*");
                startActivity(intent);
            }
        });

        tabHost = (TabHost) view.findViewById(R.id.tabHost);
        // 如果没有继承TabActivity时，通过该种方法加载启动tabHost
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("最热商家")
                .setContent(R.id.merchant_recommend_items));

        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("最热商品")
                .setContent(R.id.item_recommend_items));

        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextSize(18);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

//        progressBar.setVisibility(View.VISIBLE);
//        new LoadRecMerchantTask().execute(UserInfo.userId, "1", "1");
//        new LoadRecItemTask().execute(UserInfo.userId, "1", "1");
    }

    @Override
    public void refresh() {
        progressBar.setVisibility(View.VISIBLE);
        new LoadRecMerchantTask().execute(UserInfo.userId, "1", "1");
        new LoadRecItemTask().execute(UserInfo.userId, "1", "1");
    }

    private List<Map<String, Object>> tempGetData() {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();

//        map = new HashMap<String, Object>();
//        map.put("name", "BLACK MAGIC CHOCOLATE");
//        map.put("tag_1", "情侣约会");
//        map.put("tag_2", "巧克力");
//        map.put("tag_3", "休闲点心");
//        map.put("reason", "95");
//        map.put("average", Float.valueOf(5));
//        map.put("title", "热门指数");
//        map.put("cost", "人均48元");
//        map.put("image", R.drawable.magic);
//        list.add(map);
//
//        map = new HashMap<String, Object>();
//        map.put("name", "西贝西北菜");
//        map.put("tag_1", "家庭聚餐");
//        map.put("tag_2", "生日优惠");
//        map.put("tag_3", "西北菜");
//        map.put("reason", "88");
//        map.put("average", Float.valueOf((float) 5));
//        map.put("title", "热门指数");
//        map.put("cost", "人均81元");
//        map.put("image", R.drawable.xibei);
//        list.add(map);
//
//        map = new HashMap<String, Object>();
//        map.put("name", "潘多拉 Hair Salon");
//        map.put("tag_1", "美发");
//        map.put("tag_2", "免费点心");
//        map.put("tag_3", "服务一流");
//        map.put("reason", "82");
//        map.put("average", Float.valueOf(3));
//        map.put("title", "热门指数");
//        map.put("cost", "人均171元");
//        map.put("image", R.drawable.panduola);
//        list.add(map);
//
//        map = new HashMap<String, Object>();
//        map.put("name", "好乐迪");
//        map.put("tag_1", "KTV");
//        map.put("tag_2", "聚会");
//        map.put("tag_3", "通宵");
//        map.put("reason", "76");
//        map.put("average", Float.valueOf(4));
//        map.put("title", "热门指数");
//        map.put("cost", "人均50元");
//        map.put("image", R.drawable.haoledi);
//        list.add(map);
//
//        map = new HashMap<String, Object>();
//        map.put("name", "汤姆熊");
//        map.put("tag_1", "电玩");
//        map.put("tag_2", "人气高");
//        map.put("tag_3", "欢乐");
//        map.put("reason", "68");
//        map.put("average", Float.valueOf(4));
//        map.put("title", "热门指数");
//        map.put("cost", "人均69元");
//        map.put("image", R.drawable.tangmuxiong);
        list.add(map);

//        map = new HashMap<String, Object>();
//        map.put("name", "Sushi Bar");
//        map.put("tag_1", "日本料理");
//        map.put("tag_2", "商务宴请");
//        map.put("tag_3", "新鲜");
//        map.put("reason", "a吃过赤坂亭");
//        map.put("average", Float.valueOf((float) 3.5));
//        map.put("title", "推荐理由");
//        map.put("cost", "人均428元");
//        map.put("image", R.drawable.sushi);
//        list.add(map);

        return list;
    }

    private List<Map<String, Object>> getData() {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map;

//        map = new HashMap<String, Object>();
//        map.put("name", "奶油鲷鱼烧");
//        map.put("reason", "92");
//        map.put("price", "单价：8元");
//        map.put("rating", Float.valueOf((float) 5));
//        map.put("merchant", "大一梦薄皮鲷鱼烧");
//        map.put("picture", R.drawable.chouyushao);
//        list.add(map);
//
//        map = new HashMap<String, Object>();
//        map.put("name", "芝士蛋糕");
//        map.put("reason", "85");
//        map.put("price", "单价：22元");
//        map.put("rating", Float.valueOf((float) 4.5));
//        map.put("merchant", "面包新语");
//        map.put("picture", R.drawable.zhishidangao);
//        list.add(map);
//
//        map = new HashMap<String, Object>();
//        map.put("name", "提拉米苏");
//        map.put("reason", "80");
//        map.put("price", "单价：20元");
//        map.put("rating", Float.valueOf((float) 4.5));
//        map.put("merchant", "面包新语");
//        map.put("picture", R.drawable.tilamisu);
//        list.add(map);
//
//        map = new HashMap<String, Object>();
//        map.put("name", "三月间炒饭");
//        map.put("reason", "73");
//        map.put("price", "单价：38元");
//        map.put("rating", Float.valueOf(4));
//        map.put("merchant", "三月间生活餐厅");
//        map.put("picture", R.drawable.chaofan);
//        list.add(map);
//
//        map = new HashMap<String, Object>();
//        map.put("name", "冰淇淋火锅");
//        map.put("reason", "70");
//        map.put("price", "单价：248元");
//        map.put("rating", Float.valueOf((float) 4.5));
//        map.put("merchant", "哈根达斯");
//        map.put("picture", R.drawable.bingqilinhuoguo);
//        list.add(map);
//
//        map = new HashMap<String, Object>();
//        map.put("name", "抹茶冰淇淋");
//        map.put("reason", "69");
//        map.put("price", "单价：78元");
//        map.put("rating", Float.valueOf((float) 4));
//        map.put("merchant", "哈根达斯");
//        map.put("picture", R.drawable.mochabing);
//        list.add(map);

        return list;

    }

    class LoadRecMerchantTask extends
            AsyncTask<String, Integer, List<Map<String, Object>>> {
        @Override
        protected List<Map<String, Object>> doInBackground(String... args) {
            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
            String url = StarFoundFragment.this
                    .getString(R.string.hot_merchant_head);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userId", args[0]));
            params.add(new BasicNameValuePair("longitude", String.valueOf(121.439813)));
            params.add(new BasicNameValuePair("latitude", String.valueOf(31.194009)));
//            url += "&merchantType=" + params[2];

            RecMerchantInfoHelper tmp = new HttpListGetter<RecMerchantInfoHelper>()
                    .getFromUrl(url, RecMerchantInfoHelper.class, params);

            if (tmp == null || tmp.getState().equals("fail")) {
                return res;
            }

            merchant_infos = tmp.getRecResult();
            System.out.println("StarFoundFragment result get = " + merchant_infos.size());

            for (RecMerchantInfo merchant : merchant_infos) {
                Map<String, Object> line = new HashMap<String, Object>();
                if (merchant.getName() != null)
                    line.put("name", merchant.getName());
                else
                    line.put("name", "");
                line.put("average", Float.valueOf(merchant.getAverageRating()));
                line.put("consume", "人均" + String.valueOf(merchant.getAverageConsume()) + "元");
                line.put("hot", String.valueOf(merchant.getWeight()));
                DecimalFormat df = new DecimalFormat("###.0");
                if (merchant.getDistance() < 1000) {
                    int distance = Math.round(merchant.getDistance());
                    line.put("distance", String.valueOf(distance) + "米");
                } else {
                    Double distance = new Double(0);
                    distance = Double.valueOf(df.format(merchant.getDistance() / 1000.0));
                    line.put("distance", String.valueOf(distance) + "千米");
                }
                if (merchant.getPicture() != null && merchant.getPicture().length() > 0)
                    line.put("picture", shop_picture_head + merchant.getPicture());
                else
                    line.put("picture", shop_picture_head + "000000.jpg");

                res.add(line);
            }

            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {

            progressBar.setVisibility(View.INVISIBLE);

            SimpleAdapter adapter = new SimpleAdapter(StarFoundFragment.this.getActivity(), data,
                    R.layout.layout_hot_item, new String[]{"name", "hot", "average", "consume", "picture", "distance"}, new int[]{
                    R.id.merchant_name, R.id.merchant_hot, R.id.merchant_ratting, R.id.average_cost, R.id.merchant_image, R.id.merchant_distance}
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

            merchantListView.setAdapter(adapter);
            merchantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    Intent intent = new Intent(StarFoundFragment.this.getActivity(),
                            MerchantDetailActivity.class);
                    intent.putExtra("shop_id", String.valueOf(merchant_infos.get(arg2).getEid()));
                    startActivity(intent);
                }
            });

            SimpleAdapter item_adapter = new SimpleAdapter(StarFoundFragment.this.getActivity(), getData(),
                    R.layout.layout_businessitem_item, new String[]{"name", "reason", "price", "rating", "merchant", "picture"}, new int[]{
                    R.id.item_name, R.id.item_reason, R.id.item_price, R.id.item_ratting, R.id.item_merchant, R.id.item_image}
            );

            item_adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    if ((view instanceof RatingBar) && (data instanceof Float)) {
                        RatingBar rb = (RatingBar) view;
                        Float r = (Float) data;
                        rb.setRating(r);
                        return true;
                    }
                    return false;
                }
            });

            itemListView.setAdapter(item_adapter);
        }
    }

    class LoadRecItemTask extends
            AsyncTask<String, Integer, List<Map<String, Object>>> {
        @Override
        protected List<Map<String, Object>> doInBackground(String... args) {
            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
            String url = StarFoundFragment.this
                    .getString(R.string.hot_item_head);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userId", args[0]));
            params.add(new BasicNameValuePair("longitude", String.valueOf(121.439813)));
            params.add(new BasicNameValuePair("latitude", String.valueOf(31.194009)));

            RecItemInfoHelper tmp = new HttpListGetter<RecItemInfoHelper>()
                    .getFromUrl(url, RecItemInfoHelper.class, params);

            if (tmp == null || tmp.getState().equals("fail")) {
                return res;
            }

            System.out.println("aa - " + tmp.getRecResult().size());
            item_infos = tmp.getRecResult();
            System.out.println("result get = " + item_infos.size());

            for (RecItemInfo item : item_infos) {
                Map<String, Object> line = new HashMap<String, Object>();
                if (item.getName() != null)
                    line.put("name", item.getName());
                else
                    line.put("name", "");
                line.put("average", Float.valueOf(item.getAverageRating()));
                line.put("consume", String.valueOf(item.getPrice()) + "元");
                line.put("hot", String.valueOf(item.getWeight()));
                DecimalFormat df = new DecimalFormat("###.0");
                if (item.getMerchantName() != null) {
                    if (item.getMerchantName().length() <= 7)
                        line.put("distance", item.getMerchantName());
                    else
                        line.put("distance", item.getMerchantName().substring(0, 6) + "...");
                } else
                    line.put("distance", "");
                if (item.getPicture() != null || item.getPicture().length() <= 0)
                    line.put("picture", item_picture_head + item.getPicture());
                else
                    line.put("picture", "");

                res.add(line);
            }

            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {

            progressBar.setVisibility(View.INVISIBLE);

            SimpleAdapter adapter = new SimpleAdapter(StarFoundFragment.this.getActivity(), data,
                    R.layout.layout_hot_item, new String[]{"name", "hot", "average", "consume", "picture", "distance"}, new int[]{
                    R.id.merchant_name, R.id.merchant_hot, R.id.merchant_ratting, R.id.average_cost, R.id.merchant_image, R.id.merchant_distance}
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

            itemListView.setAdapter(adapter);
            itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    Intent intent = new Intent(StarFoundFragment.this.getActivity(),
                            ItemDetailActivity.class);
                    intent.putExtra("item_id", String.valueOf(item_infos.get(arg2).getEid()));
                    startActivity(intent);
                }
            });

        }
    }
}