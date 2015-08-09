package com.stpi.campus.activity.lvBridge;

/**
 * Created by cyc on 14-8-2.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.stpi.campus.activity.Common.CommentActivity;
import com.stpi.campus.activity.personSetting.LoginActivity;
import com.stpi.campus.entity.sh_lv.CommentInfo;
import com.stpi.campus.entity.sh_lv.CommentInfoHelper;
import com.stpi.campus.items.user.PostResult;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.util.JsonGetter;
import com.stpi.campus.R;
import com.stpi.campus.task.CollectTask;
import com.stpi.campus.util.HttpListGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cyc on 14-7-27.
 */
public class lvCommentActivity extends Activity {

    private ProgressBar progressBar = null;
    private String person_picture_head = null;
    private String common_picture_head = null;
    private ListView commentList = null;

    private String p_title = null;
    private String p_createTime = null;
    private String p_picture = null;
    private String p_description = null;
    private String p_shId = "";
    private String p_replyNum = null;
    private String p_contact = null;

    private ImageView imageView = null;
    private TextView titleView = null;
    private TextView timeView = null;
    private TextView descriptionView = null;
    private TextView replyNumView = null;
    private TextView contactView = null;
    private EditText commentText = null;
    private Button commentSubmitBtn = null;

    private CommentTask commentTask = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sh_comment);

        person_picture_head = this.getString(R.string.person_picture_head);
        common_picture_head = this.getString(R.string.common_picture_head);

        initIcons();
        initLoad();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        new LoadCommentTask().execute();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayUseLogoEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.actionbar_back_icon);
//        menu.add(0, 1, 0, "评论").setIcon(R.drawable.actionbar_comment_icon).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 1, 0, "收藏").setIcon(R.drawable.actionbar_collect_icon).setShowAsAction
                (MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
//            case 1:
//                this.showAddCommentDialog();
//                break;
            case 1:
                this.collect();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void collect() {
        loginConfirm();

        CollectTask collectTask = new CollectTask(this, progressBar);
        collectTask.execute(UserInfo.userId, "5", p_shId);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showAddCommentDialog() {
        loginConfirm();

        Intent intent = new Intent(lvCommentActivity.this, CommentActivity.class);
        intent.putExtra("objectId", p_shId);
        intent.putExtra("type", 2);
        startActivity(intent);
    }

    private void attemptAddComment() {

        if(commentTask != null)
            return;

        commentTask = new CommentTask();
        String comment = commentText.getText().toString();
        if (p_shId.equals(""))
            return;
        commentTask.execute(UserInfo.userId, p_shId, comment, "2");
        progressBar.setVisibility(View.VISIBLE);

    }

    private void loginConfirm() {
        if (UserInfo.userId.equals("0")) {
            Toast.makeText(lvCommentActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(lvCommentActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    class LoadCommentTask extends
            AsyncTask<String, Integer, List<Map<String, Object>>> {
        @Override
        protected List<Map<String, Object>> doInBackground(String... args) {
            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();

            String url = lvCommentActivity.this.getString(R.string.fetch_comment_head);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("objectId", p_shId));
            params.add(new BasicNameValuePair("type", "2"));

            CommentInfoHelper tmp = new HttpListGetter<CommentInfoHelper>()
                    .getFromUrl(url, CommentInfoHelper.class, params);

            if (tmp == null || tmp.getState().equals("fail"))
                return res;

            System.out.println("aa - " + tmp.getResults().size());
            List<CommentInfo> sh_infos = tmp.getResults();
            System.out.println("result get = " + sh_infos.size());

            if (sh_infos == null)
                return res;

            System.out.println("result get = " + sh_infos.size());

            for (CommentInfo info : sh_infos) {
                Map<String, Object> line = new HashMap<String, Object>();
                line.put("username", info.getUserName());
                line.put("comment", info.getComment());
                line.put("createTime", info.getTimestamp());
                if (info.getPicture() != null && info.getPicture().length() > 0)
                    line.put("picture", person_picture_head + info.getPicture());
                else
                    line.put("picture", person_picture_head + "000000.jpg");

                res.add(line);
            }

            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {

            if(data == null)
                return;

            SimpleAdapter adapter = new SimpleAdapter(lvCommentActivity.this, data, R.layout.shcomment_list_item,
                    new String[]{"picture", "username", "comment", "createTime"},
                    new int[]{R.id.picture, R.id.username, R.id.comment, R.id.createTime});

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

            commentList.setAdapter(adapter);
            setListViewHeightBasedOnChildren(commentList);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void initIcons() {

        progressBar = (ProgressBar) this.findViewById(R.id.progress);
        commentList = (ListView) this.findViewById(R.id.sh_comment_list);

        imageView = (ImageView) this.findViewById(R.id.itemPicture);
        titleView = (TextView) this.findViewById(R.id.title);
        descriptionView = (TextView) this.findViewById(R.id.description);
        timeView = (TextView) this.findViewById(R.id.createTime);
        replyNumView = (TextView) this.findViewById(R.id.replyNum);
        contactView = (TextView) this.findViewById(R.id.contact);

        commentText = (EditText) this.findViewById(R.id.commentText);
        commentSubmitBtn = (Button) this.findViewById(R.id.commentButton);
        commentSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAddComment();
            }
        });
        
    }

    private void initLoad () {
        if (this.getIntent().getStringExtra("title") != null) {
            p_title = this.getIntent().getStringExtra("title");
            titleView.setText(p_title);
        }
        else {
            titleView.setText("暂无标题");
        }
        if (this.getIntent().getStringExtra("createTime") != null) {
            p_createTime = this.getIntent().getStringExtra("createTime");
            timeView.setText(p_createTime);
        }
        else {
            titleView.setText("");
        }
        if (this.getIntent().getStringExtra("contact") != null) {
            p_contact = this.getIntent().getStringExtra("contact");
            contactView.setText(p_contact);
        }
        else {
            contactView.setText("暂无");
        }
        if (this.getIntent().getStringExtra("description") != null) {
            p_description = this.getIntent().getStringExtra("description");
            descriptionView.setText(p_description);
        }
        else {
            descriptionView.setText("");
        }
        if (this.getIntent().getStringExtra("itemId") != null) {
            p_shId = this.getIntent().getStringExtra("itemId");
        }
        if (this.getIntent().getStringExtra("replyNum") != null) {
            p_replyNum = this.getIntent().getStringExtra("replyNum");
        }
        replyNumView.setText("评论(" + p_replyNum + ")");

        if (this.getIntent().getStringExtra("picture") != null)
            p_picture = this.getIntent().getStringExtra("picture");
        String imageUri = null;
        if (p_picture == null || p_picture.equals("")) {
            imageUri = common_picture_head + "000000.jpg";
        } else
            imageUri = common_picture_head + p_picture;
        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().build();
        imageLoader.displayImage(imageUri, imageView, options);
    }

    private static void setListViewHeightBasedOnChildren(ListView listView) {
        if(listView == null) return;

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public class CommentTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {
            String url = lvCommentActivity.this.getString(R.string.add_comment_head);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userId", args[0]));
            params.add(new BasicNameValuePair("objectId", args[1]));
            params.add(new BasicNameValuePair("comment", args[2]));
            params.add(new BasicNameValuePair("type", args[3]));

            PostResult res = new JsonGetter<PostResult>().getFromUrl(url, PostResult.class, params);
            if (res == null)
                return "error";
            if (res.getState().equals("success"))
                return "ok";
            else
                return "error";
        }

        @Override
        protected void onPostExecute(final String retCode) {
            commentTask = null;
            progressBar.setVisibility(View.INVISIBLE);
            System.out.println(retCode);

            if (retCode.equals("error"))
                Toast.makeText(lvCommentActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(lvCommentActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                new LoadCommentTask().execute();
            }
        }

        @Override
        protected void onCancelled() {
            commentTask = null;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}
