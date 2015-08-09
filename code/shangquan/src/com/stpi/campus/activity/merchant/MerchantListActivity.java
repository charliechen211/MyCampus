package com.stpi.campus.activity.merchant;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.stpi.campus.activity.personalService.SearchMerchantActivity;
import com.stpi.campus.entity.item.RecItemInfo;
import com.stpi.campus.entity.item.RecItemInfoHelper;
import com.stpi.campus.entity.merchant.RecMerchantInfoHelper;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.R;
import com.stpi.campus.activity.item.ItemDetailActivity;
import com.stpi.campus.entity.merchant.RecMerchantInfo;
import com.stpi.campus.util.HttpListGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cyc on 14-1-5.
 */
public class MerchantListActivity extends Activity {

    private ListView merchantListView = null;
    private ListView itemListView = null;
    private Button moreButton = null;
    private List<RecMerchantInfo> merchant_infos = new ArrayList<RecMerchantInfo>();
    private List<RecItemInfo> item_infos = new ArrayList<RecItemInfo>();

    private String shop_picture_head;
    private String item_picture_head;

    private ProgressBar progressBar = null;
    private TabHost tabHost = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_find);

        shop_picture_head = this.getString(R.string.shop_picture_head);
        item_picture_head = this.getString(R.string.item_picture_head);

        merchantListView = (ListView) this.findViewById(R.id.merchant_recommend_items);
        itemListView = (ListView) this.findViewById(R.id.item_recommend_items);
        moreButton = (Button) this.findViewById(R.id.moreButton);
        progressBar = (ProgressBar) this.findViewById(R.id.progress);

        tabHost = (TabHost) this.findViewById(R.id.tabHost);
        // 如果没有继承TabActivity时，通过该种方法加载启动tabHost
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("推荐商家")
                .setContent(R.id.merchant_recommend_items));

        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("推荐商品")
                .setContent(R.id.item_recommend_items));

        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextSize(18);
        }

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MerchantListActivity.this,
                        SearchMerchantActivity.class);
                intent.putExtra("search", "*");
                startActivity(intent);
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        new LoadRecMerchantTask().execute(UserInfo.userId, "1", "1");
        new LoadRecItemTask().execute(UserInfo.userId, "1", "1");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                this.finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "返回").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    class LoadRecMerchantTask extends
            AsyncTask<String, Integer, List<Map<String, Object>>> {
        @Override
        protected List<Map<String, Object>> doInBackground(String... args) {
            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
            String url = MerchantListActivity.this
                    .getString(R.string.rec_merchant_head);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userId", args[0]));
            params.add(new BasicNameValuePair("longitude", UserInfo.longitude));
            params.add(new BasicNameValuePair("latitude", UserInfo.latitude));

            RecMerchantInfoHelper tmp = new HttpListGetter<RecMerchantInfoHelper>()
                    .getFromUrl(url, RecMerchantInfoHelper.class, params);

            if (tmp == null || tmp.getState().equals("fail")) {
                return res;
            }

            System.out.println("aa - " + tmp.getRecResult().size());
            merchant_infos = tmp.getRecResult();
            System.out.println("result get = " + merchant_infos.size());

            for (RecMerchantInfo merchant : merchant_infos) {
                Map<String, Object> line = new HashMap<String, Object>();
                if (merchant.getName() != null)
                    line.put("name", merchant.getName());
                else
                    line.put("name", "");
                line.put("average", Float.valueOf(merchant.getAverageRating()));
                line.put("consume", "人均" + String.valueOf(merchant.getAverageConsume()) + "元");
//                line.put("hot", String.valueOf(merchant.getWeight()));
                DecimalFormat df = new DecimalFormat("###.0");
                if (merchant.getDistance() < 1000) {
                    int distance = Math.round(merchant.getDistance());
                    line.put("distance", String.valueOf(distance) + "米");
                } else {
                    Double distance = new Double(0);
                    distance = Double.valueOf(df.format(merchant.getDistance() / 1000.0));
                    line.put("distance", String.valueOf(distance) + "千米");
                }
                if (merchant.getPicture() != null || merchant.getPicture().length() <= 0)
                    line.put("picture", shop_picture_head + merchant.getPicture());
                else
                    line.put("picture", "");
                line.put("reason", " ");
                line.put("tag_1", "");
                line.put("tag_2", "");
                line.put("tag_3", "");
                switch (merchant.getRecType()) {
                    case 1:
                        if (merchant.getReason() != null) {
                            if (merchant.getReason().length() < 20)
                                line.put("reason", "你最近去过 " + merchant.getReason());
                            else
                                line.put("reason", "你最近去过 " + merchant.getReason().substring(0, 17) + "...");
                        }
                        break;
                    case 2:
                        if (merchant.getReason() != null) {
                            if (merchant.getReason().length() < 20)
                                line.put("reason", merchant.getReason() + " 最近去过");
                            else
                                line.put("reason", merchant.getReason().substring(0, 17) + "... 最近去过");
                        }
                        break;
                    case 3:
                        String tags[] = merchant.getReason().split(",");
                        if (tags != null) {
                            if (tags.length > 0)
                                line.put("tag_1", tags[0]);
                            if (tags.length > 1)
                                line.put("tag_2", tags[1]);
                            if (tags.length > 2)
                                line.put("tag_3", tags[2]);
                        }
                        break;
                    default:
                        break;
                }

                res.add(line);
            }

            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {

            progressBar.setVisibility(View.INVISIBLE);

            SimpleAdapter adapter = new SimpleAdapter(MerchantListActivity.this, data,
                    R.layout.layout_business_item, new String[]{"name", "tag_1",
                    "tag_2", "tag_3", "reason", "average", "consume", "picture", "distance"}, new int[]{
                    R.id.merchant_name, R.id.merchant_tag_1, R.id.merchant_tag_2, R.id.merchant_tag_3,
                    R.id.merchant_detail_reason, R.id.merchant_ratting, R.id.average_cost, R.id.merchant_image, R.id.merchant_distance}
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
                    Intent intent = new Intent(MerchantListActivity.this,
                            MerchantDetailActivity.class);
                    intent.putExtra("shop_id", String.valueOf(merchant_infos.get(arg2).getEid()));
                    startActivity(intent);
                }
            });
        }
    }

    class LoadRecItemTask extends
            AsyncTask<String, Integer, List<Map<String, Object>>> {
        @Override
        protected List<Map<String, Object>> doInBackground(String... params) {
            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
            String url = MerchantListActivity.this
                    .getString(R.string.rec_item_head);
            url += "?userId=" + params[0];
            url += "&longitude=" + UserInfo.longitude;
            url += "&latitude=" + UserInfo.latitude;
//            url += "&merchantType=" + params[2];

            RecItemInfoHelper tmp = new HttpListGetter<RecItemInfoHelper>()
                    .getFromUrl(url, RecItemInfoHelper.class);

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
                line.put("merchantName", "来自 " + item.getMerchantName());
                line.put("consume", String.valueOf(item.getPrice()) + "元");
                if (item.getPicture() != null || item.getPicture().length() <= 0)
                    line.put("picture", item_picture_head + item.getPicture());
                else
                    line.put("picture", "");
                line.put("reason", " ");
                line.put("tag_1", "");
                line.put("tag_2", "");
                line.put("tag_3", "");
                switch (item.getRecType()) {

                    case 1:
                        if (item.getReason() != null) {
                            if (item.getReason().length() < 20)
                                line.put("reason", "你最近去过 " + item.getReason());
                            else
                                line.put("reason", "你最近去过 " + item.getReason().substring(0, 17) + "...");
                        }
                        break;
                    case 2:
                        if (item.getReason() != null) {
                            if (item.getReason().length() < 20)
                                line.put("reason", item.getReason() + " 最近去过");
                            else
                                line.put("reason", item.getReason().substring(0, 17) + "... 最近去过");
                        }
                        break;
                    case 3:
                        String tags[] = item.getReason().split(" ");
                        if (tags != null) {
                            if (tags.length > 0)
                                line.put("tag_1", tags[0]);
                            else
                                line.put("tag_1", "");
                            if (tags.length > 1)
                                line.put("tag_2", tags[1]);
                            else
                                line.put("tag_2", "");
                            if (tags.length > 2)
                                line.put("tag_3", tags[2]);
                            else
                                line.put("tag_3", "");
                        }
                        break;
                    default:
                        break;
                }

                res.add(line);
            }

            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {

            progressBar.setVisibility(View.INVISIBLE);

            SimpleAdapter adapter = new SimpleAdapter(MerchantListActivity.this, data,
                    R.layout.layout_business_item, new String[]{"name", "tag_1",
                    "tag_2", "tag_3", "reason", "average", "merchantName", "picture", "consume"}, new int[]{
                    R.id.merchant_name, R.id.merchant_tag_1, R.id.merchant_tag_2, R.id.merchant_tag_3,
                    R.id.merchant_detail_reason, R.id.merchant_ratting, R.id.average_cost, R.id.merchant_image, R.id.merchant_distance}
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
                    Intent intent = new Intent(MerchantListActivity.this,
                            ItemDetailActivity.class);
                    intent.putExtra("item_id", String.valueOf(item_infos.get(arg2).getEid()));
                    startActivity(intent);
                }
            });

        }
    }
//
//    private List<Map<String, Object>> getData() {
//
//        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//
//        Map<String, Object> map;
//
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
//
//        return list;
//
//    }
}