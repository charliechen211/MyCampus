package com.stpi.campus.activity.campusNews;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.stpi.campus.activity.personSetting.LoginActivity;
import com.stpi.campus.configuration.CampusModule;
import com.stpi.campus.entity.campusNews.CampusNewsWebContentHelper;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.task.CollectTask;
import com.stpi.campus.R;
import com.stpi.campus.util.HttpListGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/8/5.
 */
public class CampusNewsDetailActivity extends Activity {
    private TextView newsTitle;
    private TextView newsTime;
    private WebView newsContent;
    private String topicId;
    private ProgressBar progressBar = null;
    private ImageButton backHome;
    private ImageButton collectButton;
    private TextView title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campus_news_item);
        topicId = getIntent().getStringExtra("topicId");
        newsTitle = (TextView) findViewById(R.id.newsTitle);
        newsTime = (TextView) findViewById(R.id.newsTime);
        newsContent = (WebView) findViewById(R.id.newsContent);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        newsTitle.setText(getIntent().getStringExtra("newsTitle"));
        newsTime.setText(getIntent().getStringExtra("newsTime"));
        setActionBar();
        new LoadNewsContent().execute();
    }

    public void setActionBar() {
        ActionBar actionBar = getActionBar();
        View view = LayoutInflater.from(this).inflate(R.layout.actionbar_backhome_collection, null);

        backHome = (ImageButton) view.findViewById(R.id.backHome);
        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CampusNewsDetailActivity.this.finish();
            }
        });
/*        collectButton = (ImageButton) view.findViewById(R.id.collect);
            collectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collect();
            }
        });*/
        title = (TextView) view.findViewById(R.id.title);
        title.setText("资讯详情");
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable
                .actionbar_color_campus_news));
        actionBar.setCustomView(view);
    }
    public void collect() {
        loginConfirm();
        progressBar.setVisibility(View.VISIBLE);
        new CollectTask(this, progressBar).execute(UserInfo.userId, CampusModule.campus_news, topicId);
    }

    private void loginConfirm() {
        if (UserInfo.userId.equals("0")) {
            Toast.makeText(CampusNewsDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CampusNewsDetailActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    class LoadNewsContent extends
            AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = CampusNewsDetailActivity.this
                    .getString(R.string.campus_news_content_head);
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("topicId", topicId));

            CampusNewsWebContentHelper newsContentHelper = new HttpListGetter<CampusNewsWebContentHelper>()
                    .getFromUrl(url, CampusNewsWebContentHelper.class, param);

            if (newsContentHelper == null || newsContentHelper.getState().equals("fail")) {
                return "";
            }
            if (newsContentHelper.getResult() != null)
                return newsContentHelper.getResult();
            else
                return "";
        }

        @Override
        protected void onPostExecute(String data) {
            newsContent.getSettings().setDefaultTextEncodingName("UTF-8");
            newsContent.loadData(data, "text/html; charset=UTF-8", null);
        }
    }

}