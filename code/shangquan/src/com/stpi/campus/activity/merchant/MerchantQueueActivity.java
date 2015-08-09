package com.stpi.campus.activity.merchant;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.stpi.campus.items.queue.*;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.R;
import com.stpi.campus.util.HttpListGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 13-12-12.
 */
public class MerchantQueueActivity extends Activity {

    private ListView queueListView = null;
    private MyAdapter adapter = null;
    private List<QueueInfo> queueList = null;
    private Button joinButton = null;
    private JoinQueueTask mTask = null;
    private ListView inQueueListView = null;

    private SearchQueueTask sTask = null;
    private boolean firstTime = true;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_info);

        TextView merchantName = (TextView) this.findViewById(R.id.merchant_name);
        merchantName.setText(this.getIntent().getStringExtra("shop_name"));
        String merchant_picture = this.getIntent().getStringExtra("shop_picture");

        queueListView = (ListView) this.findViewById(R.id.queue_list);
        inQueueListView = (ListView) this.findViewById(R.id.in_queue_list);

        joinButton = (Button) this.findViewById(R.id.join_button);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptJoin();
            }
        });
        joinButton.setEnabled(false);

        ImageView pictureView = (ImageView) this.findViewById(R.id.merchant_picture);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(merchant_picture, pictureView);

        new QueueInfoTask().execute(this.getIntent().getStringExtra("shop_id"));
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

    @Override
    protected void onPause() {
        System.out.println("Paused");
        if (sTask != null) {
            System.out.println("stop..");
            sTask.stop();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        sTask = new SearchQueueTask();
        sTask.execute(this.getIntent().getStringExtra("shop_id"));
        super.onResume();
    }

    private void attemptJoin() {
        if (adapter.getSelectedIndex() < 0) {
            Toast.makeText(this, "请选择队伍", Toast.LENGTH_SHORT).show();
            return;
        }
        if (UserInfo.userId.equals("0")) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mTask != null) {
            return;
        }
        mTask = new JoinQueueTask();
        mTask.execute(queueList.get(adapter.getSelectedIndex()));
    }

    class QueueInfoTask extends AsyncTask<String, Integer, List<Map<String, Object>>> {

        @Override
        protected List<Map<String, Object>> doInBackground(String... args) {
            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();

            String url = MerchantQueueActivity.this.getString(R.string.queue_info_head);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("merchantId", args[0]));
            QueueInfoHelper helper = new HttpListGetter<QueueInfoHelper>().getFromUrl(url, QueueInfoHelper.class, params);

            if (helper == null)
                return res;

            queueList = helper.getResult();
            for (QueueInfo queueInfo : queueList) {
                Map<String, Object> line = new HashMap<String, Object>();
                line.put("title", queueInfo.getTableType());
                line.put("description", queueInfo.getDescription());
                line.put("start", queueInfo.getStartTime());
                line.put("end", queueInfo.getEndTime());

                res.add(line);
            }
            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> maps) {
            if (maps.size() <= 0) {
                Toast.makeText(MerchantQueueActivity.this, "没有排队服务", Toast.LENGTH_SHORT).show();
                return;
            }
            adapter = new MyAdapter(
                    MerchantQueueActivity.this, maps,
                    R.layout.queue_info_item, new String[]{"title", "description", "start", "end"},
                    new int[]{R.id.queue_title, R.id.queue_info, R.id.queue_start_time, R.id.queue_end_time});
            queueListView.setAdapter(adapter);
            queueListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    adapter.setSelectedIndex(arg2);
                    adapter.notifyDataSetInvalidated();
                }
            });
            joinButton.setEnabled(true);
        }
    }

    class MyAdapter extends SimpleAdapter {

        private int selectedIndex = -1;

        public MyAdapter(Context context, List<? extends Map<String, ?>> data,
                         int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        public int getSelectedIndex() {
            return selectedIndex;
        }

        public void setSelectedIndex(int selectedIndex) {
            this.selectedIndex = selectedIndex;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View res = super.getView(position, convertView, parent);
            if (position == selectedIndex)
                res.setBackgroundColor(getResources().getColor(R.color.main_color_dark));
            else
                res.setBackgroundColor(Color.TRANSPARENT);
            return res;
        }

    }

    class JoinQueueTask extends AsyncTask<QueueInfo, Integer, JoinQueueResult> {

        @Override
        protected JoinQueueResult doInBackground(QueueInfo... args) {
            String url = MerchantQueueActivity.this.getString(R.string.queue_join_head);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userId", UserInfo.userId));
            params.add(new BasicNameValuePair("merchantId", String.valueOf(args[0].getMerchantId())));
            params.add(new BasicNameValuePair("tableType", args[0].getTableType()));
            JoinQueueResult res = new HttpListGetter<JoinQueueResult>().getFromUrl(url, JoinQueueResult.class, params);
            return res;
        }

        @Override
        protected void onPostExecute(JoinQueueResult joinQueueResult) {
            mTask = null;
            if (joinQueueResult == null) {
                Toast.makeText(MerchantQueueActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            } else if (joinQueueResult.getOrderNum() < 0) {
                Toast.makeText(MerchantQueueActivity.this, "重复排队", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MerchantQueueActivity.this, "取号" + joinQueueResult.getOrderNum() + "，现在排到" + joinQueueResult.getCurrentOrder(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mTask = null;
        }
    }

    class SearchQueueTask extends AsyncTask<String, Integer, List<String>> {
        private boolean going = true;

        public void stop() {
            going = false;
        }

        @Override
        protected List<String> doInBackground(String... args) {
            if (firstTime)
                firstTime = false;
            else {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("pull..");
            List<String> result = new ArrayList<String>();
            if (!UserInfo.userId.equals("0")) {
                String url = MerchantQueueActivity.this.getString(R.string.queue_merchant_head);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("userId", UserInfo.userId));
                params.add(new BasicNameValuePair("merchantId", args[0]));
                MerchantQueueResult inQueue = new HttpListGetter<MerchantQueueResult>().getFromUrl(url, MerchantQueueResult.class, params);
                if (inQueue == null) {
                    result.add("网络异常");
                } else {
                    for (UserInQueueInfo userInfo : inQueue.getResult()) {
                        for (TableOrderInfo tableInfo : inQueue.getCurrentOrder()) {
                            if (userInfo.getTableType().equals(tableInfo.getTableType())) {
                                result.add(tableInfo.getTableType() + " 目前" + tableInfo.getOrder() + " 我是" + userInfo.getOrderNum());
                                break;
                            }
                        }
                    }
                }
            } else
                result.add("未登录");
            if (going) {
                sTask = new SearchQueueTask();
                sTask.execute(args);
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            if (!going)
                return;
            if (strings.size() <= 0)
                strings.add("没有参与排队");
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MerchantQueueActivity.this, android.R.layout.simple_list_item_1, strings);
            inQueueListView.setAdapter(arrayAdapter);
        }
    }
}