package com.stpi.campus.activity.personalService;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.stpi.campus.R;
import com.stpi.campus.entity.user.DetailUserInfo;
import com.stpi.campus.entity.user.PreviewUserInfoHelper;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.task.AddAttentionTask;
import com.stpi.campus.task.CancelAttentionTask;
import com.stpi.campus.util.ImageFileUtils;
import com.stpi.campus.util.JsonGetter;
import com.stpi.campus.util.UtilTaskCallBack;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cyc on 13-12-1.
 */
public class PersonHomepageActivity extends Activity {

    private ListView followListView = null;
    private ListView fanListView = null;

    private SimpleAdapter follow_list_adapter;
    private SimpleAdapter fan_list_adapter;

    private List<Map<String, Object>> followData = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> fanData = new ArrayList<Map<String, Object>>();

    private List<DetailUserInfo> followInfos = null;
    private List<DetailUserInfo> fanInfos = null;

    private ImageView headImageView = null;
    private TextView userNameView = null;
    private TextView schoolregionView = null;
    private TextView tag1View = null;
    private TextView tag2View = null;
    private TextView tag3View = null;
    private TabHost tabHost = null;

    private ProgressBar progressBar = null;

    private String person_picture_head = null;

    private String userIdIntent;
    private String userNameIntent;
    private String schoolNameIntent;
    private String regionNameIntent;
    private String tag1Intent;
    private String tag2Intent;
    private String tag3Intent;
    private String pictureIntent;

    private Menu menu = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_homepage);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        person_picture_head = this.getString(R.string.person_picture_head);

        loadIntent();
        initIcons();
        initFollowView();
        initFanView();
        initEvents();
        LoadUserInfo();
    }

    private void loadIntent() {
        userIdIntent = getIntent().getStringExtra("userId") != null ?
                getIntent().getStringExtra("userId") : "";
        userNameIntent = getIntent().getStringExtra("userName") != null ?
                getIntent().getStringExtra("userName") : "";
        schoolNameIntent = getIntent().getStringExtra("schoolName") != null ?
                getIntent().getStringExtra("schoolName") : "";
        regionNameIntent = getIntent().getStringExtra("regionName") != null ?
                getIntent().getStringExtra("regionName") : "";
        tag1Intent = getIntent().getStringExtra("tag1") != null ?
                getIntent().getStringExtra("tag1") : "";
        tag2Intent = getIntent().getStringExtra("tag2") != null ?
                getIntent().getStringExtra("tag2") : "";
        tag3Intent = getIntent().getStringExtra("tag3") != null ?
                getIntent().getStringExtra("tag3") : "";
        pictureIntent = getIntent().getStringExtra("picture") != null ?
                getIntent().getStringExtra("picture") : "";
    }

    private void initIcons() {
        progressBar = (ProgressBar) this.findViewById(R.id.progress);

        headImageView = (ImageView) this.findViewById(R.id.headImage);
        userNameView = (TextView) this.findViewById(R.id.name);
        schoolregionView = (TextView) this.findViewById(R.id.schoolregion);
        tag1View = (TextView) this.findViewById(R.id.personTab_1);
        tag2View = (TextView) this.findViewById(R.id.personTab_2);
        tag3View = (TextView) this.findViewById(R.id.personTab_3);

        tabHost = (TabHost) this.findViewById(R.id.tabHost);
    }

    private void initEvents() {
        progressBar.setVisibility(View.VISIBLE);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("TA的关注").setIndicator("TA的关注")
                .setContent(R.id.followListView));

        tabHost.addTab(tabHost.newTabSpec("TA的粉丝").setIndicator("TA的粉丝")
                .setContent(R.id.fanListView));
    }

    private void initFollowView() {

        followListView = (ListView) this.findViewById(R.id.followListView);

        followListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(PersonHomepageActivity.this,
                        PersonHomepageActivity.class);
                intent.putExtra("userId", String.valueOf(followInfos.get(i).getUserId()));
                intent.putExtra("userName", String.valueOf(followInfos.get(i).getUserName()));
                intent.putExtra("schoolName", String.valueOf(followInfos.get(i).getSchoolName()));
                intent.putExtra("regionName", String.valueOf(followInfos.get(i).getRegionName()));
                intent.putExtra("tag1", String.valueOf(followInfos.get(i).getTags().size() > 0 ?
                        followInfos.get(i).getTags().get(0) : ""));
                intent.putExtra("tag2", String.valueOf(followInfos.get(i).getTags().size() > 1 ?
                        followInfos.get(i).getTags().get(1) : ""));
                intent.putExtra("tag3", String.valueOf(followInfos.get(i).getTags().size() > 2 ?
                        followInfos.get(i).getTags().get(2) : ""));
                startActivity(intent);
            }
        });

        follow_list_adapter = new SimpleAdapter(PersonHomepageActivity.this,
                followData,
                R.layout.friend_list_item,
                new String[]{"name", "picture", "tag_1", "tag_2", "tag_3"},
                new int[]{R.id.name, R.id.headImage, R.id.tagTab1, R.id.tagTab2, R.id.tagTab3});

        follow_list_adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if ((view instanceof ImageView) && (data instanceof String)) {
                    ImageView imageView = (ImageView) view;
                    String imageUri = (String) data;
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .cacheInMemory().cacheOnDisc()
                            .preProcessor(new BitmapProcessor() {
                                @Override
                                public Bitmap process(Bitmap bitmap) {
                                    return ImageFileUtils.getRoundCornerImage(bitmap,
                                            R.dimen.size25);
                                }
                            })
                            .build();
                    imageLoader.displayImage(imageUri, imageView, options);
                    return true;
                }
                return false;
            }
        });
        followListView.setAdapter(follow_list_adapter);

    }

    private void initFanView() {

        fanListView = (ListView) this.findViewById(R.id.fanListView);

        fanListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(PersonHomepageActivity.this,
                        PersonHomepageActivity.class);
                intent.putExtra("userId", String.valueOf(fanInfos.get(i).getUserId()));
                intent.putExtra("userName", String.valueOf(fanInfos.get(i).getUserName()));
                intent.putExtra("schoolName", String.valueOf(fanInfos.get(i).getSchoolName()));
                intent.putExtra("regionName", String.valueOf(fanInfos.get(i).getRegionName()));
                intent.putExtra("tag1", String.valueOf(fanInfos.get(i).getTags().size() > 0 ?
                        fanInfos.get(i).getTags().get(0) : ""));
                intent.putExtra("tag2", String.valueOf(fanInfos.get(i).getTags().size() > 1 ?
                        fanInfos.get(i).getTags().get(1) : ""));
                intent.putExtra("tag3", String.valueOf(fanInfos.get(i).getTags().size() > 2 ?
                        fanInfos.get(i).getTags().get(2) : ""));
                startActivity(intent);
            }
        });

        fan_list_adapter = new SimpleAdapter(PersonHomepageActivity.this,
                fanData,
                R.layout.friend_list_item,
                new String[]{"name", "picture", "tag_1", "tag_2", "tag_3"},
                new int[]{R.id.name, R.id.headImage, R.id.tagTab1, R.id.tagTab2, R.id.tagTab3});

        fan_list_adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if ((view instanceof ImageView) && (data instanceof String)) {
                    ImageView imageView = (ImageView) view;
                    String imageUri = (String) data;
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .cacheInMemory().cacheOnDisc()
                            .preProcessor(new BitmapProcessor() {
                                @Override
                                public Bitmap process(Bitmap bitmap) {
                                    return ImageFileUtils.getRoundCornerImage(bitmap,
                                            R.dimen.size25);
                                }
                            })
                            .build();
                    imageLoader.displayImage(imageUri, imageView, options);
                    return true;
                }
                return false;
            }
        });
        fanListView.setAdapter(fan_list_adapter);

    }

    private void LoadUserInfo() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory().cacheOnDisc()
                .preProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        return ImageFileUtils.getRoundCornerImage(bitmap, R.dimen.size40);
                    }
                })
                .build();
        imageLoader.displayImage(pictureIntent, headImageView, options);
        userNameView.setText(userNameIntent);
        schoolregionView.setText(schoolNameIntent + "(" + regionNameIntent + ")");
        if (tag1Intent.equals(""))
            tag1View.setVisibility(View.INVISIBLE);
        else
            tag1View.setText(tag1Intent);
        if (tag2Intent.equals(""))
            tag2View.setVisibility(View.INVISIBLE);
        else
            tag2View.setText(tag2Intent);
        if (tag3Intent.equals(""))
            tag3View.setVisibility(View.INVISIBLE);
        else
            tag3View.setText(tag3Intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        new LoadFollowTask().execute(userIdIntent);
        new LoadFanTask().execute(userIdIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu = menu;
        resetMenu();
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void resetMenu() {
        this.menu.clear();

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayUseLogoEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.actionbar_back_icon);
        if(UserInfo.friendList.contains(userIdIntent))
            this.menu.add(0, 1, 0, "取消关注").setShowAsAction(MenuItem
                    .SHOW_AS_ACTION_IF_ROOM);
        else
            this.menu.add(0, 2, 0, "关注").setShowAsAction(MenuItem
                    .SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case 1:
                this.attemptCancelAttention();
                break;
            case 2:
                this.attemptAddAttention();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void attemptCancelAttention() {

        new CancelAttentionTask(this, progressBar, new UtilTaskCallBack() {
            @Override
            public void taskCallBack() {
                resetMenu();
            }
        }).execute(UserInfo.userId,
                userIdIntent);
        progressBar.setVisibility(View.VISIBLE);

    }

    private void attemptAddAttention() {

        new AddAttentionTask(this, progressBar, new UtilTaskCallBack() {
            @Override
            public void taskCallBack() {
                resetMenu();
            }
        }).execute(UserInfo.userId, userIdIntent);
        progressBar.setVisibility(View.VISIBLE);

    }

    class LoadFollowTask extends
            AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {

            String url = PersonHomepageActivity.this.getString(R.string.follow_list_head);
            url += "?userId=" + params[0];

            PreviewUserInfoHelper followListResult = new JsonGetter<PreviewUserInfoHelper>()
                    .getFromUrl(url, PreviewUserInfoHelper.class);

            if (followListResult == null || followListResult.getState().equals("fail"))
                return false;

            followInfos = followListResult.getResults();

            followData.clear();
            for (DetailUserInfo follow : followInfos) {
                Map<String, Object> line = new HashMap<String, Object>();
                if (follow.getUserName() != null)
                    line.put("name", follow.getUserName());
                else
                    line.put("name", "");
                if (follow.getPicture() != null)
                    line.put("picture", person_picture_head + follow.getPicture());
                else
                    line.put("picture", "");
                line.put("friendId", follow.getUserId());
                if (follow.getTags() == null) {
                    line.put("tag_1", "");
                    line.put("tag_2", "");
                    line.put("tag_3", "");
                } else {
                    if (follow.getTags().size() > 0)
                        line.put("tag_1", follow.getTags().get(0));
                    else
                        line.put("tag_1", "");
                    if (follow.getTags().size() > 1)
                        line.put("tag_2", follow.getTags().get(1));
                    else
                        line.put("tag_2", "");
                    if (follow.getTags().size() > 2)
                        line.put("tag_3", follow.getTags().get(2));
                    else
                        line.put("tag_3", "");
                }
                followData.add(line);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if(result) {
                progressBar.setVisibility(View.INVISIBLE);
                follow_list_adapter.notifyDataSetChanged();
            }

        }
    }

    class LoadFanTask extends
            AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {

            String url = PersonHomepageActivity.this.getString(R.string.fan_list_head);
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("userId", params[0]));

            PreviewUserInfoHelper fanListResult = new JsonGetter<PreviewUserInfoHelper>()
                    .getFromUrl(url, PreviewUserInfoHelper.class, param);

            if (fanListResult == null || fanListResult.getState().equals("fail"))
                return false;

            fanInfos = fanListResult.getResults();

            fanData.clear();
            for (DetailUserInfo follow : fanInfos) {
                Map<String, Object> line = new HashMap<String, Object>();
                if (follow.getUserName() != null)
                    line.put("name", follow.getUserName());
                else
                    line.put("name", "");
                if (follow.getPicture() != null)
                    line.put("picture", person_picture_head + follow.getPicture());
                else
                    line.put("picture", "");
                line.put("friendId", follow.getUserId());
                if (follow.getTags() == null) {
                    line.put("tag_1", "");
                    line.put("tag_2", "");
                    line.put("tag_3", "");
                } else {
                    if (follow.getTags().size() > 0)
                        line.put("tag_1", follow.getTags().get(0));
                    else
                        line.put("tag_1", "");
                    if (follow.getTags().size() > 1)
                        line.put("tag_2", follow.getTags().get(1));
                    else
                        line.put("tag_2", "");
                    if (follow.getTags().size() > 2)
                        line.put("tag_3", follow.getTags().get(2));
                    else
                        line.put("tag_3", "");
                }
                fanData.add(line);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if(result) {
                progressBar.setVisibility(View.INVISIBLE);
                fan_list_adapter.notifyDataSetChanged();
            }

        }
    }

}