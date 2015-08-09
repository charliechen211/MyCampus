package com.stpi.campus.activity.personalService;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.stpi.campus.entity.user.DetailUserInfo;
import com.stpi.campus.entity.user.DetailUserInfoHelper;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.R;
import com.stpi.campus.util.HttpListGetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cyc on 13-12-5.
 */
public class ChangePresentActivity extends Activity {

    private GridView gridview = null;
    private ImageView headImageView = null;
    private TextView userNameView = null;
    private TextView pointView = null;
    private TextView tag1View = null;
    private TextView tag2View = null;
    private TextView tag3View = null;

    private ProgressBar progressBar = null;
    private String picture_head = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_present);

        picture_head = this.getString(R.string.person_picture_head);

        GridView gridview = (GridView) findViewById(R.id.presents);
        headImageView = (ImageView) this.findViewById(R.id.image);
        userNameView = (TextView) this.findViewById(R.id.name);
        pointView = (TextView) this.findViewById(R.id.score);
        tag1View = (TextView) this.findViewById(R.id.merchantTab_1);
        tag2View = (TextView) this.findViewById(R.id.merchantTab_2);
        tag3View = (TextView) this.findViewById(R.id.merchantTab_3);
        progressBar = (ProgressBar) this.findViewById(R.id.progress);

        progressBar.setVisibility(View.VISIBLE);
        new LoadUserInfoTask().execute(UserInfo.userId);

        SimpleAdapter adapter = new SimpleAdapter(this, getData(), R.layout.present_list_item, new String[]{"name", "score", "picture"}, new int[]{R.id.textName, R.id.score, R.id.image});
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if ((view instanceof ImageView) && (data instanceof String)) {
                    ImageView imageView = (ImageView) view;
                    String imageUri = (String) data;
                    if (imageUri.length() <= 0)
                        return true;
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().build();
                    imageLoader.displayImage(imageUri, imageView, options);
                    return true;
                }
                return false;
            }
        });
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(ChangePresentActivity.this, "兑换成功", Toast.LENGTH_SHORT).show();
            }
        });
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

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();

        Map<String, Object> line = new HashMap<String, Object>();
        line.put("name", "茶杯");
        line.put("score", "1500积分");
        line.put("picture", R.drawable.present_cup);
        res.add(line);

        line = new HashMap<String, Object>();
        line.put("name", "U盘");
        line.put("score", "3000积分");
        line.put("picture", R.drawable.present_udisk);
        res.add(line);

        line = new HashMap<String, Object>();
        line.put("name", "笔记本");
        line.put("score", "1500积分");
        line.put("picture", R.drawable.present_notebook);
        res.add(line);

        line = new HashMap<String, Object>();
        line.put("name", "手套");
        line.put("score", "3000积分");
        line.put("picture", R.drawable.present_glove);
        res.add(line);

        line = new HashMap<String, Object>();
        line.put("name", "路由器");
        line.put("score", "6000积分");
        line.put("picture", R.drawable.present_router);
        res.add(line);

        line = new HashMap<String, Object>();
        line.put("name", "手机壳");
        line.put("score", "5000积分");
        line.put("picture", R.drawable.present_surface);
        res.add(line);

        line = new HashMap<String, Object>();
        line.put("name", "电子称");
        line.put("score", "5000积分");
        line.put("picture", R.drawable.present_cup);
        res.add(line);

        line = new HashMap<String, Object>();
        line.put("name", "网球拍");
        line.put("score", "15000积分");
        line.put("picture", R.drawable.present_tennis);
        res.add(line);

        line = new HashMap<String, Object>();
        line.put("name", "时尚板鞋");
        line.put("score", "25000积分");
        line.put("picture", R.drawable.present_shoe);
        res.add(line);

        return res;
    }

    class LoadUserInfoTask extends
            AsyncTask<String, Integer, List<Map<String, Object>>> {

        private String picture = "";
        private String name = "";
        private float score = 0;
        private String tag_1 = "";
        private String tag_2 = "";
        private String tag_3 = "";

        @Override
        protected List<Map<String, Object>> doInBackground(String... params) {

            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
            String url = ChangePresentActivity.this.getString(R.string.detail_user_head);
            url += "?userId=" + params[0];

            DetailUserInfoHelper user_0 = new HttpListGetter<DetailUserInfoHelper>()
                    .getFromUrl(url, DetailUserInfoHelper.class);

            if (user_0 == null || user_0.getState().equals("fail")) {
//                Toast.makeText(PersonHomepageFragment.this.getActivity(), "获取异常", Toast.LENGTH_SHORT).show();
                return res;
            }

            DetailUserInfo theUserInfo = user_0.getResult();

            if (theUserInfo == null) {
                return res;
            }
            if (theUserInfo.getPicture() != null)
                picture = picture_head + theUserInfo.getPicture();
            if (theUserInfo.getUserName() != null)
                name = theUserInfo.getUserName();
            score = theUserInfo.getPoint();
            if (theUserInfo.getTags() != null) {
                if (theUserInfo.getTags().size() > 0)
                    tag_1 = theUserInfo.getTags().get(0);
                if (theUserInfo.getTags().size() > 1)
                    tag_2 = theUserInfo.getTags().get(1);
                if (theUserInfo.getTags().size() > 2)
                    tag_3 = theUserInfo.getTags().get(2);
            }

            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {

            progressBar.setVisibility(View.INVISIBLE);

            userNameView.setText(name);
            pointView.setText(String.valueOf(Math.round(score)));
            if (tag_1.equals(""))
                tag1View.setVisibility(View.INVISIBLE);
            else
                tag1View.setText(tag_1);
            if (tag_2.equals(""))
                tag2View.setVisibility(View.INVISIBLE);
            else
                tag2View.setText(tag_2);
            if (tag_3.equals(""))
                tag3View.setVisibility(View.INVISIBLE);
            else
                tag3View.setText(tag_3);

            String imageUri = (String) picture;
            if (imageUri.length() <= 0)
                return;
            ImageLoader imageLoader = ImageLoader.getInstance();
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().build();
            imageLoader.displayImage(imageUri, headImageView, options);
            return;
        }
    }
}