package com.stpi.campus.activity.interactiveWall;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
import com.stpi.campus.entity.interactionWall.TopicCommentHelper;
import com.stpi.campus.R;
import com.stpi.campus.activity.personSetting.LoginActivity;
import com.stpi.campus.entity.interactionWall.TopicCommentInfo;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.util.HttpListGetter;
import com.stpi.campus.util.JsonGetter;
import com.stpi.campus.util.StateResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2014/7/29.
 */
public class InteractiveDetailActivity extends Activity {
    private TextView content;
    private TextView timestamp;
    private TextView isFriend;
    private ListView commentList;
    private String topicId;
    private Button button;
    private EditText commentText;
    private List<TopicCommentInfo> comments = new ArrayList<TopicCommentInfo>();
    private List<Map<String, Object>> commentDatas = new ArrayList<Map<String, Object>>();
    private SimpleAdapter commentAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interactive_item_detail);
        commentList = (ListView) this.findViewById(R.id.commentList);
        content = (TextView) this.findViewById(R.id.content);
        timestamp = (TextView) this.findViewById(R.id.timestamp);
        isFriend = (TextView) this.findViewById(R.id.isFriend);
        button = (Button) this.findViewById(R.id.commentButton);
        commentText = (EditText) this.findViewById(R.id.commentText);
        content.setText(getIntent().getStringExtra("content"));
        timestamp.setText(getIntent().getStringExtra("timestamp"));
        isFriend.setText(getIntent().getStringExtra("isFriend"));
        topicId = getIntent().getStringExtra("topicId");
        initActionBar();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserInfo.userId.equals("0")) {
                    Toast.makeText(InteractiveDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(InteractiveDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                    return;
                }
                String comment = commentText.getText().toString();
                if (comment.equals("")) {
                    Toast.makeText(InteractiveDetailActivity.this, "请输入评论内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                commentText.setText("");
                new AddCommentTask().execute(comment, topicId);
            }
        });
        commentAdapter = new SimpleAdapter(InteractiveDetailActivity.this, commentDatas,
                R.layout.interaction_comment_list_item, new String[]{"content", "timestamp"}, new int[]{
                R.id.content, R.id.timestamp}
        );
        commentList.setAdapter(commentAdapter);
        new LoadInteractionTopicCommentTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void initActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        ViewGroup home = (ViewGroup) findViewById(android.R.id.home).getParent();
        ((ImageView) home.getChildAt(0))
                    .setImageResource(R.drawable.actionbar_back_icon);
    }
    class LoadInteractionTopicCommentTask extends
            AsyncTask<String, Integer, List<Map<String, Object>>> {
        @Override
        protected List<Map<String, Object>> doInBackground(String... params) {
            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
            String url = InteractiveDetailActivity.this
                    .getString(R.string.interaction_topicComment_head);
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("topicId", topicId));

            TopicCommentHelper topicCommentHelper = new HttpListGetter<TopicCommentHelper>()
                    .getFromUrl(url, TopicCommentHelper.class, param);

            if (topicCommentHelper == null || topicCommentHelper.getState().equals("fail")) {
                return res;
            }

            comments = topicCommentHelper.getResult();
            if (comments == null)
                return res;
            commentDatas.clear();
            for (TopicCommentInfo c : comments) {
                Map<String, Object> line = new HashMap<String, Object>();
                line.put("content", c.getContent());
                line.put("timestamp", c.getDate());
                commentDatas.add(line);
                res.add(line);
            }
            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {
            if(data.size() > 0)
                commentAdapter.notifyDataSetChanged();

        }
    }

    private class AddCommentTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... args) {
            String url = InteractiveDetailActivity.this.getString(R.string.interaction_addComment_head);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("content", args[0]));
            params.add(new BasicNameValuePair("topicId", args[1]));
            StateResponse res = new JsonGetter<StateResponse>().getFromUrl(url, StateResponse.class, params);
            return res.getState();

        }

        @Override
        protected void onPostExecute(String retCode) {
            new LoadInteractionTopicCommentTask().execute();
            if (retCode.equals("fail"))
                Toast.makeText(InteractiveDetailActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(InteractiveDetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
        }
    }


}