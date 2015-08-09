package com.stpi.campus.activity.partTimeJob;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.stpi.campus.activity.personSetting.LoginActivity;
import com.stpi.campus.configuration.CampusModule;
import com.stpi.campus.entity.partTimeJob.PartTimeJobContentHelper;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.task.CollectTask;
import com.stpi.campus.R;
import com.stpi.campus.util.HttpListGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/8/11.
 */
public class PartTimeJobDetailActivity extends Activity {
    private TextView jobTitle;
    private TextView jobContent;
    private TextView jobTime;
    private String jobId;
    private ProgressBar progressBar;
    private ImageButton backHome;
    private ImageButton collectButton;
    private TextView title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parttime_job_item_detail);
        jobTitle = (TextView) findViewById(R.id.jobTitle);
        jobContent = (TextView) findViewById(R.id.jobContent);
        jobTime = (TextView) findViewById(R.id.jobTime);
        jobTitle.setText(getIntent().getStringExtra("title"));
        jobTime.setText(getIntent().getStringExtra("createTime"));
        jobId = getIntent().getStringExtra("itemId");
        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        setActionBar();
        new LoadjobContent().execute();
    }

    public void setActionBar() {
        ActionBar actionBar = getActionBar();
        View view = LayoutInflater.from(this).inflate(R.layout.actionbar_backhome_parttime, null);

        backHome = (ImageButton) view.findViewById(R.id.backHome);
        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PartTimeJobDetailActivity.this.finish();
            }
        });
        collectButton = (ImageButton) view.findViewById(R.id.collect);
        collectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collect();
            }
        });
        title = (TextView) view.findViewById(R.id.title);
        title.setText("职位详情");
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable
                .actionbar_color_parttime));
        actionBar.setCustomView(view);
    }

    public void collect() {
        loginConfirm();
        progressBar.setVisibility(View.VISIBLE);
        new CollectTask(this, progressBar).execute(UserInfo.userId, CampusModule.campus_parttimeJob, jobId);
    }

    private void loginConfirm() {
        if (UserInfo.userId.equals("0")) {
            Toast.makeText(PartTimeJobDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PartTimeJobDetailActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    class LoadjobContent extends
            AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = PartTimeJobDetailActivity.this
                    .getString(R.string.part_time_job_detail_head);
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("jobId", jobId));

            PartTimeJobContentHelper partTimeJobContentHelper = new HttpListGetter<PartTimeJobContentHelper>()
                    .getFromUrl(url, PartTimeJobContentHelper.class, param);

            if (partTimeJobContentHelper == null || partTimeJobContentHelper.getState().equals("fail")) {
                return "";
            }
            if (partTimeJobContentHelper.getResult() != null){
                String job = null;
                if(partTimeJobContentHelper.getResult().getContent() != null)
                    job = "职位描述: "+ partTimeJobContentHelper.getResult().getContent() + "\n";
                if(partTimeJobContentHelper.getResult().getRequirement() != null)
                    job +="职位要求: "+ partTimeJobContentHelper.getResult().getRequirement() + "\n";
                if(partTimeJobContentHelper.getResult().getPay() != null)
                    job +="待遇: "+ partTimeJobContentHelper.getResult().getPay() + "\n";
                if(partTimeJobContentHelper.getResult().getLinkway() != null)
                    job +="联系方式: "+ partTimeJobContentHelper.getResult().getLinkway() + "\n";
                return job;
            }

            else
                return "暂无信息";
        }

        @Override
        protected void onPostExecute(String data) {
            progressBar.setVisibility(View.INVISIBLE);
            jobContent.setText(data);
        }
    }
}