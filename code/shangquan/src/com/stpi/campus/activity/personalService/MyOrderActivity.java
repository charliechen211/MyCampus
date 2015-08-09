package com.stpi.campus.activity.personalService;

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
import com.stpi.campus.R;
import com.stpi.campus.activity.item.ItemDetailActivity;
import com.stpi.campus.entity.order.OrderInfo;
import com.stpi.campus.entity.order.OrderInfoHelper;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.util.HttpListGetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cyc on 14-1-1.
 */
public class MyOrderActivity extends Activity {

    private ListView orderListView = null;
    private ProgressBar progressBar = null;
    private List<OrderInfo> infos = null;

    private String picture_head = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        picture_head = this.getString(R.string.item_picture_head);

        orderListView = (ListView) this.findViewById(R.id.orderListView);
        progressBar = (ProgressBar) this.findViewById(R.id.progress);

        progressBar.setVisibility(View.VISIBLE);
        new LoadOrderTask().execute(UserInfo.userId);
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

    class LoadOrderTask extends
            AsyncTask<String, Integer, List<Map<String, Object>>> {

        @Override
        protected List<Map<String, Object>> doInBackground(String... params) {

            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();

            String url = MyOrderActivity.this.getString(R.string.fetch_order_list_head);
            url += "?userId=" + params[0];

            OrderInfoHelper tmp = new HttpListGetter<OrderInfoHelper>()
                    .getFromUrl(url, OrderInfoHelper.class);

            if (tmp == null || tmp.getState().equals("fail")) {
                return res;
            }

            System.out.println("aa - " + tmp.getResults().size());
            infos = tmp.getResults();
            System.out.println("result get = " + infos.size());

            for (OrderInfo order_item : infos) {
                Map<String, Object> line = new HashMap<String, Object>();
                if (order_item.getItemName() != null)
                    line.put("name", order_item.getItemName());
                else
                    line.put("name", "");
                if (order_item.getPicture() != null && order_item.getPicture().length() > 0)
                    line.put("picture", picture_head + order_item.getPicture());
                else
                    line.put("picture", picture_head + "000000.jpg");
                line.put("averageCost", "人均" + String.valueOf(order_item.getPrice()) + "元");
                if (order_item.getOrderTime() != null) {
                    String[] time = order_item.getOrderTime().split("T");
                    String times = "";
                    for (int i = 0; i < time.length; i++)
                        times = time[i] + "\n";
                    line.put("time", times.substring(0, times.length() - 1));
                } else
                    line.put("time", "");
                line.put("number", String.valueOf(order_item.getNumber()) + "份");
                line.put("tag1", "");
                line.put("tag2", "");
                line.put("tag3", "");
                if (order_item.getTags() != null) {
                    if (order_item.getTags().size() >= 1)
                        line.put("tag1", order_item.getTags().get(0));
                    if (order_item.getTags().size() >= 2)
                        line.put("tag2", order_item.getTags().get(1));
                    if (order_item.getTags().size() >= 3)
                        line.put("tag3", order_item.getTags().get(2));
                }
                res.add(line);
            }

            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {

            SimpleAdapter order_adapter = new SimpleAdapter(MyOrderActivity.this, data, R.layout.order_list_item, new String[]{"name",
                    "picture",
                    "number", "averageCost", "time", "tag1", "tag2", "tag3"}, new int[]{
                    R.id.name,
                    R.id.itemPicture,
                    R.id.number, R.id.consume, R.id.time, R.id.textTab1, R.id.textTab2, R.id.textTab3});

            order_adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
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
//                    else if ((view instanceof TextView) && (data instanceof String)) {
//                        String s = (String) data;
//                        if (s.length() <= 0) {
//                            view.setVisibility(View.INVISIBLE);
//                            return true;
//                        }
//                    }
                    return false;
                }
            });

            orderListView.setAdapter(order_adapter);
            orderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(MyOrderActivity.this,
                            ItemDetailActivity.class);
                    intent.putExtra("item_id", String.valueOf(infos.get(i).getItemId()));
                    startActivity(intent);
                }
            });
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}