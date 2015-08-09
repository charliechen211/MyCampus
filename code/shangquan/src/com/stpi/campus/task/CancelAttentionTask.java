package com.stpi.campus.task;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.R;
import com.stpi.campus.items.user.FriendResult;
import com.stpi.campus.util.JsonGetter;
import com.stpi.campus.util.UtilTaskCallBack;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 2014/11/20.
 */
public class CancelAttentionTask extends AsyncTask<String, Void, String> {

    private Context activity;
    private ProgressBar progressBar;
    private String friendId;
    private UtilTaskCallBack callBack;

    public CancelAttentionTask(Context theActivity, ProgressBar theProgressBar,
                               UtilTaskCallBack theCallBack) {
        activity = theActivity;
        progressBar = theProgressBar;
        callBack = theCallBack;
    }

    @Override
    protected String doInBackground(String... args) {
        String url = activity.getString(R.string.cancel_friend_head);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userId", args[0]));
        params.add(new BasicNameValuePair("friendId", args[1]));
        friendId = args[1];

        FriendResult res = new JsonGetter<FriendResult>().getFromUrl(url, FriendResult.class, params);
        if (res == null)
            return "error";
        if (res.getState().equals("success"))
            return "success";
        else
            return "unknown";
    }

    @Override
    protected void onPostExecute(final String retCode) {
        progressBar.setVisibility(View.INVISIBLE);
        System.out.println(retCode);

        if (retCode.equals("error"))
            Toast.makeText(activity, "网络异常", Toast.LENGTH_SHORT).show();
        else if (retCode.equals("unknown"))
            Toast.makeText(activity, "操作无效", Toast.LENGTH_SHORT).show();
        else {
            UserInfo.friendList.remove(friendId);
            callBack.taskCallBack();
            Toast.makeText(activity, "取消关注成功", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCancelled() {
        progressBar.setVisibility(View.INVISIBLE);
    }

}
