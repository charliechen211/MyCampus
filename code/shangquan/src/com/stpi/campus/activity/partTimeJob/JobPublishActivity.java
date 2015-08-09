package com.stpi.campus.activity.partTimeJob;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import org.apache.commons.lang3.StringUtils;
import com.stpi.campus.entity.ResultHelper;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.R;
import com.stpi.campus.util.HttpListGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyl on 2014/11/8.
 */
public class JobPublishActivity extends Activity {
    private EditText title;
    private EditText content;
    private EditText address;
    private EditText pay;
    private EditText requirement;
    private EditText linkway;
    private EditText name;
    private String jobType;
    private RadioGroup radioGroup;
    private RadioButton radioButton0;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_publish);
        title = (EditText) findViewById(R.id.title);
        content = (EditText) findViewById(R.id.content);
        address = (EditText) findViewById(R.id.address);
        pay = (EditText) findViewById(R.id.pay);
        name = (EditText) findViewById(R.id.name);
        requirement = (EditText) findViewById(R.id.requirement);
        linkway = (EditText) findViewById(R.id.linkway);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioButton0 = (RadioButton) findViewById(R.id.radioButton0);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
        ViewGroup home = (ViewGroup) findViewById(android.R.id.home).getParent();
        ((ImageView) home.getChildAt(0))
                .setImageResource(R.drawable.actionbar_cancel_icon);
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable
                .actionbar_color_parttime));
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == radioButton0.getId())
                    jobType = "0";
                else
                    jobType = "1";
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.confirm:
                confirm();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void confirm() {
        String titleString = title.getText().toString();
        String contentString = content.getText().toString();
        String requirementString = requirement.getText().toString();
        String linkwayString = linkway.getText().toString();
        if(titleString.equals("")){
            Toast.makeText(this, "请完善标题", Toast.LENGTH_SHORT).show();
            return;
        }
        if(contentString.equals("")){
            Toast.makeText(this, "请完善职位", Toast.LENGTH_SHORT).show();
            return;
        }
        if(requirementString.equals("")){
            Toast.makeText(this, "请完善要求", Toast.LENGTH_SHORT).show();
            return;
        }
        if(linkwayString.equals("")){
            Toast.makeText(this, "请完善联系方式", Toast.LENGTH_SHORT).show();
            return;
        }
        if(StringUtils.isNumeric(pay.getText().toString())) {
            Toast.makeText(this, "请正确填写待遇", Toast.LENGTH_SHORT).show();
            return;
        }
        new AsyncPublish().execute();
    }

    class AsyncPublish extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            String url = JobPublishActivity.this
                    .getString(R.string.part_time_add_job_head);
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("userId", UserInfo.userId));
            param.add(new BasicNameValuePair("title", title.getText().toString()));
            param.add(new BasicNameValuePair("content", content.getText().toString()));
            param.add(new BasicNameValuePair("jobtype", jobType));
            param.add(new BasicNameValuePair("jobname", name.getText().toString()));
            param.add(new BasicNameValuePair("place", address.getText().toString()));
            param.add(new BasicNameValuePair("pay", pay.getText().toString()));
            param.add(new BasicNameValuePair("requirement", requirement.getText().toString()));
            param.add(new BasicNameValuePair("linkway", linkway.getText().toString()));

            ResultHelper resultHelper = new HttpListGetter<ResultHelper>()
                    .getFromUrl(url, ResultHelper.class, param);

            if (resultHelper == null) {
                return 0;
            }
            if (resultHelper.getState().equals("success")) {
                return 1;
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer data) {
            progressBar.setVisibility(View.INVISIBLE);
            if (data == 0)
                Toast.makeText(JobPublishActivity.this,
                        "职位发布失败，请稍后重试", Toast.LENGTH_SHORT)
                        .show();
            else {
                Toast.makeText(JobPublishActivity.this,
                        "职位发布成功", Toast.LENGTH_SHORT)
                        .show();

            }
        }
    }
}