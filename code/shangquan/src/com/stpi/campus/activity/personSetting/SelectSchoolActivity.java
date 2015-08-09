package com.stpi.campus.activity.personSetting;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.R;
import com.stpi.campus.items.circle.CircleInfo;
import com.stpi.campus.items.circle.CircleInfoHelper;
import com.stpi.campus.util.HttpListGetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cyc on 14-8-30.
 */
public class SelectSchoolActivity extends Activity {

    private ListView schoolList = null;
    private List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_school);

        schoolList = (ListView) this.findViewById(R.id.schoollist);
        new LoadCircleInfo().execute();
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
    public void onResume() {
        super.onResume();
    }

    class LoadCircleInfo extends
            AsyncTask<String, Integer, List<Map<String, Object>>> {
        @Override
        protected List<Map<String, Object>> doInBackground(String... params) {
            String url = SelectSchoolActivity.this.getString(R.string.fetch_circle_head);
//            url += "?circleId=1";

            CircleInfoHelper tmp_0 = new HttpListGetter<CircleInfoHelper>().getFromUrl(url, CircleInfoHelper.class);
            if (tmp_0 == null || tmp_0.getState().equals("fail")) {
                return res;
            }
            List<CircleInfo> helper = tmp_0.getResults();
            for (CircleInfo circle : helper) {
                Map<String, Object> tmp = new HashMap<String, Object>();
                tmp.put("circleId", circle.getCircleId());
                tmp.put("circleName", circle.getCircleName());
                res.add(tmp);
            }
            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {
            SimpleAdapter adapter = new SimpleAdapter(SelectSchoolActivity.this, data,
                    R.layout.item_school_item, new String[]{"circleName"}, new int[]{R.id.schoolName});

            schoolList.setAdapter(adapter);
            schoolList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    UserInfo.circleId = Integer.valueOf(String.valueOf(res.get(arg2).get("circleId")));
                    UserInfo.circleName = String.valueOf(res.get(arg2).get("circleName"));
                    SelectSchoolActivity.this.finish();
                }
            });
        }
    }

}