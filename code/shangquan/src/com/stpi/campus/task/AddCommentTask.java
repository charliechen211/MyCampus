package com.stpi.campus.task;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.stpi.campus.items.user.PostResult;
import com.stpi.campus.util.JsonGetter;
import com.stpi.campus.R;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 13-12-27.
 */

public class AddCommentTask extends AsyncTask<String, Void, String> {

    private Context activity;
    private ProgressBar progressBar;

    public AddCommentTask(Context theActivity, ProgressBar theProgressBar) {
        activity = theActivity;
        progressBar = theProgressBar;
    }

    @Override
    protected String doInBackground(String... args) {
        String url = activity.getString(R.string.add_comment_head);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("entityId", args[0]));
        params.add(new BasicNameValuePair("userId", args[1]));
        params.add(new BasicNameValuePair("content", args[2]));
        params.add(new BasicNameValuePair("rate1", args[3]));
        params.add(new BasicNameValuePair("rate2", args[4]));
        params.add(new BasicNameValuePair("rate3", args[5]));
        params.add(new BasicNameValuePair("entityType", args[6]));
        params.add(new BasicNameValuePair("tags", args[7]));
        params.add(new BasicNameValuePair("consume", args[8]));

        PostResult res = new JsonGetter<PostResult>().getFromUrl(url, PostResult.class, params);
        if (res == null)
            return "error";
        if (res.getState().equals("success"))
            return "success";
        else
            return "error";
    }

    @Override
    protected void onPostExecute(final String retCode) {
        progressBar.setVisibility(View.INVISIBLE);
        System.out.println(retCode);

        if (retCode.equals("error"))
            Toast.makeText(activity, "网络异常", Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(activity, "评论成功", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCancelled() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}
