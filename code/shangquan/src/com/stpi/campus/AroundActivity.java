package com.stpi.campus;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import com.stpi.campus.R;
import com.stpi.campus.entity.dynamicShare.AroundShareInfo;
import com.stpi.campus.entity.dynamicShare.ShareInfoHelper;
import com.stpi.campus.util.HttpListGetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AroundActivity extends Activity {

    private String user_id = "1";
    private ListView aroundActivityView = null;
    private ListView aroundFriendView = null;
    private List<AroundShareInfo> infos = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_around);

        // get context
        aroundActivityView = (ListView) this.findViewById(R.id.aroundActivityView);

        aroundFriendView = (ListView) this.findViewById(R.id.aroundFriendView);
        SimpleAdapter friend_adapter = new SimpleAdapter(this, getFriendData(),
                R.layout.aroundfriend_list_item, new String[]{"name", /* "averageRate",*/
                "place", "time", "comment"}, new int[]{
                R.id.friendName,/* R.id.ratingBar,*/ R.id.place, R.id.time, R.id.comment}
        );
        aroundFriendView.setAdapter(friend_adapter);

        if (this.getIntent().getStringExtra("user_id") != null)
            user_id = this.getIntent().getStringExtra("user_id");

        // execute
        new LoadRecMerchantTask().execute(user_id, "1");

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        // 如果没有继承TabActivity时，通过该种方法加载启动tabHost
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("附近人动态")
                .setContent(R.id.aroundActivityView));

        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("好友动态")
                .setContent(R.id.aroundFriendView));
//        myCollectionView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//                                    long arg3) {
//                Intent intent = new Intent(FavoriteActivity.this,
//                        MerchantDetailActivity.class);
//                startActivity(intent);
//            }});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.around, menu);
        return true;
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < 5; i++) {
            Map<String, Object> line = new HashMap<String, Object>();
            res.add(line);
        }

        return res;
    }

    private List<Map<String, Object>> getAroundData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();

        map.put("name", "FXY");
        map.put("place", "肯德基");
        map.put("time", "在2013-11-15 : 12:30");
        map.put("comment", "大家好，我叫房新宇");
//        map.put("averageRate", "24");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("name", "LZK");
        map.put("place", "肯德基");
        map.put("time", "在2013-11-15 : 12:30");
        map.put("comment", "大家好，我叫栾志坤");
//        map.put("averageRate", "24");
        list.add(map);

        return list;
    }

    private List<Map<String, Object>> getFriendData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();

        map.put("name", "LYL");
        map.put("place", "棒约翰");
        map.put("time", "在2013-11-15 : 12:30");
        map.put("comment", "搞基ING");
//        map.put("averageRate", "24");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("name", "WZL");
        map.put("place", "棒约翰");
        map.put("time", "在2013-11-15 : 12:30");
        map.put("comment", "搞基ING");
//        map.put("averageRate", "24");
        list.add(map);

        return list;
    }

    class LoadRecMerchantTask extends
            AsyncTask<String, Integer, List<Map<String, Object>>> {
        @Override
        protected List<Map<String, Object>> doInBackground(String... params) {
            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();

            String url = AroundActivity.this
                    .getString(R.string.around_share_head);
            url += "?userId=" + params[0];
            url += "&circleId=" + params[1];

            ShareInfoHelper tmp = new HttpListGetter<ShareInfoHelper>()
                    .getFromUrl(url, ShareInfoHelper.class);

            if (tmp == null || tmp.getState().equals("fail")) {
//                Toast.makeText(AroundActivity.this, "获取异常", Toast.LENGTH_SHORT).show();
                return res;
            }

            System.out.println("aa - " + tmp.getResults().size());
            infos = tmp.getResults();
            System.out.println("result get = " + infos.size());

            if (infos == null)
                return res;

            System.out.println("result get = " + infos.size());

            for (AroundShareInfo share : infos) {
                Map<String, Object> line = new HashMap<String, Object>();
                line.put("name", share.getUserName());
                line.put("averageRate", Float.valueOf(share.getRate()));
                line.put("place", share.getMerchantName());
                line.put("time", share.getTimestamp());
                line.put("comment", share.getContent());
                res.add(line);
            }

            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {
            SimpleAdapter adapter = new SimpleAdapter(AroundActivity.this, data,
                    R.layout.aroundactivity_list_item, new String[]{"name", "averageRate",
                    "place", "time", "comment"}, new int[]{
                    R.id.aroundName, R.id.ratingBar, R.id.place, R.id.time, R.id.comment}
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
                    }
                    return false;
                }
            });
            aroundActivityView.setAdapter(adapter);
        }

    }

}
