package com.stpi.campus.activity.Common;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.stpi.campus.items.user.PostResult;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.R;
import com.stpi.campus.util.JsonGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 2014/11/6.
 */
public class CommentActivity extends Activity {

    private String objectId = "";
    private int type = 0;
    private EditText commentEdit = null;
    private CommentTask commentTask = null;
    private ProgressBar progressBar = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        commentEdit = (EditText) findViewById(R.id.commentEdit);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (this.getIntent().getStringExtra("objectId") != null) {
            objectId = this.getIntent().getStringExtra("objectId");
        }
        type = this.getIntent().getIntExtra("type", 0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "完成").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case 1:
                this.attemptSubmitComment();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void attemptSubmitComment() {
        if (commentTask != null)
            return;
        commentTask = new CommentTask();
        String comment = commentEdit.getText().toString();
        if (objectId.equals("") || type == 0)
            return;
        commentTask.execute(UserInfo.userId, objectId, comment, String.valueOf(type));
        progressBar.setVisibility(View.VISIBLE);
    }

    public class CommentTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {
            String url = CommentActivity.this.getString(R.string.add_comment_head);
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
                Toast.makeText(CommentActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(CommentActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                CommentActivity.this.finish();
            }
        }

        @Override
        protected void onCancelled() {
            commentTask = null;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}