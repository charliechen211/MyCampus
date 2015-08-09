package com.stpi.campus.activity.homepage;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import com.stpi.campus.R;
import com.stpi.campus.items.user.PostResult;
import com.stpi.campus.util.JsonGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/8/4.
 */
public class CollectionDelAsyTask extends AsyncTask<String, Void, String> {

    private Context activity;

    public CollectionDelAsyTask(Context theActivity) {
        activity = theActivity;
    }

    @Override
    protected String doInBackground(String... args) {
        String url = activity.getString(R.string.homepage_collectiond_del_head);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userId", args[0]));
        params.add(new BasicNameValuePair("moduleId", args[1]));
        params.add(new BasicNameValuePair("itemId", args[2]));

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

        if (retCode.equals("error"))
            Toast.makeText(activity, "网络异常", Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(activity, "删除成功", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCancelled() {

    }
}
