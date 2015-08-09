package com.stpi.campus.activity.booking;

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
import com.stpi.campus.items.queue.TableOrderInfo;
import com.stpi.campus.R;
import com.stpi.campus.activity.merchant.MerchantDetailActivity;
import com.stpi.campus.items.inqueue.InQueueInfo;
import com.stpi.campus.items.inqueue.InQueueInfohelper;
import com.stpi.campus.items.inqueue.InQueueOrder;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.util.HttpListGetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueueActivity extends Activity {

    private ListView queueList = null;
    private List<InQueueInfo> infos = null;
    private List<Integer> shopIds = null;

    private String shop_picture_head = null;

    private ProgressBar progressBar = null;
    private LoadInQueueTask lTask = null;
    private boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        shop_picture_head = this.getString(R.string.shop_picture_head);
        progressBar = (ProgressBar) this.findViewById(R.id.progress);
        queueList = (ListView) this.findViewById(R.id.queue_list);

        lTask = new LoadInQueueTask();
        lTask.execute();
    }

    @Override
    protected void onPause() {
        System.out.println("Paused");
        if (lTask != null) {
            System.out.println("stop..");
            lTask.stop();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (lTask != null)
            lTask.stop();
        lTask = new LoadInQueueTask();
        progressBar.setVisibility(View.VISIBLE);
        lTask.execute();
        super.onResume();
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

    private class LoadInQueueTask extends AsyncTask<Void, Integer, List<Map<String, Object>>> {

        private boolean going = true;

        public void stop() {
            going = false;
        }

        @Override
        protected List<Map<String, Object>> doInBackground(Void... voids) {

            if (firstTime) {
                firstTime = false;
            } else {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("pull..");
            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
            shopIds = new ArrayList<Integer>();

            String url = QueueActivity.this.getString(R.string.queue_user_head);
            url += "?userId=" + UserInfo.userId;

            InQueueInfohelper helper = new HttpListGetter<InQueueInfohelper>().getFromUrl(url, InQueueInfohelper.class);
            if (helper == null)
                return res;

            infos = helper.getResult();
            for (InQueueInfo info : infos) {
                for (InQueueOrder order : info.getQueueList()) {
                    for (TableOrderInfo table : info.getTableOrderList())
                        if (order.getTableType().equals(table.getTableType())) {
                            Map<String, Object> line = new HashMap<String, Object>();
                            if (info.getPic() != null)
                                line.put("picture", shop_picture_head + info.getPic());
                            else
                                line.put("picture", "");
                            if (info.getMerchantName() != null)
                                line.put("name", String.valueOf(info.getMerchantName()));
                            else
                                line.put("name", "");
                            if (table.getTableType() != null && table.getOrder() != 0 && order.getOrderNum() != 0)
                                line.put("info", table.getTableType() + " 目前" + table.getOrder() + "号 我是" + order.getOrderNum() + "号");
                            else
                                line.put("info", "");
                            res.add(line);
                            shopIds.add(info.getMerchantId());
                            break;
                        }
                }
            }

            if (going) {
                lTask = new LoadInQueueTask();
                lTask.execute();
            }

            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> maps) {
            if (!going)
                return;

            if (maps.size() <= 0)
                Toast.makeText(QueueActivity.this, "没有加入队列", Toast.LENGTH_SHORT).show();
            SimpleAdapter adapter = new SimpleAdapter(QueueActivity.this, maps, R.layout.inqueue_info_item, new String[]{"picture", "name", "info"}, new int[]{R.id.imageView, R.id.merchant_name, R.id.queue_info});
            queueList.setAdapter(adapter);
            adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
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
            queueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(QueueActivity.this,
                            MerchantDetailActivity.class);
                    intent.putExtra("shop_id", String.valueOf(shopIds.get(i)));
                    startActivity(intent);
                }
            });
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}
