package com.tang.guangshangquan;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

import com.tang.guangshangquan.components.TagBar;
import com.tang.guangshangquan.components.TagPool;
import com.tang.guangshangquan.items.user.DetailInfo;
import com.tang.guangshangquan.items.user.RegisterResult;
import com.tang.guangshangquan.utils.JsonGetter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends ActionBarActivity implements View.OnTouchListener, GestureDetector.OnGestureListener {

    private RadioButton boyCheck = null;
    private TagPool tagPool = null;
    private EditText tagContent = null;
    private EditText phoneView = null;
    private EditText passwordView = null;
    private EditText confirmPasswordView = null;
    private EditText nicknameView = null;
    private TabHost tabs = null;

    private Spinner ageSpinner = null;
    private Spinner jobSpinner = null;
    private Spinner homeSpinner = null;
    private Spinner incomeSpinner = null;

    private RegisterTask registerTask = null;
    private RecommendTask recommendTask = null;
    private ProgressBar progressBar = null;

    private GestureDetector gestureDetector = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 设置桌面标签
        tabs = (TabHost) this.findViewById(R.id.tabHost);
        tabs.setup();
        tabs.addTab(tabs.newTabSpec("tab1").setContent(R.id.tab1).setIndicator("注册资料"));
        tabs.addTab(tabs.newTabSpec("tab2").setContent(R.id.tab2).setIndicator("详细信息"));
        tabs.addTab(tabs.newTabSpec("tab3").setContent(R.id.tab3).setIndicator("我的标签"));

        // 设置性别选项
        boyCheck = (RadioButton) this.findViewById(R.id.check_boy);
        boyCheck.setChecked(true);

        // 设置年龄列表
        ageSpinner = (Spinner) this.findViewById(R.id.age);
        ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DetailInfo.age);
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(ageAdapter);

        // 设置职业列表
        jobSpinner = (Spinner) this.findViewById(R.id.job);
        ArrayAdapter<String> jobAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DetailInfo.job);
        jobAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobSpinner.setAdapter(jobAdapter);

        // 设置家乡列表
        homeSpinner = (Spinner) this.findViewById(R.id.hometown);
        ArrayAdapter<String> homeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DetailInfo.province);
        homeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        homeSpinner.setAdapter(homeAdapter);

        // 设置收入列表
        incomeSpinner = (Spinner) this.findViewById(R.id.income);
        ArrayAdapter<String> incomeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DetailInfo.income);
        incomeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        incomeSpinner.setAdapter(incomeAdapter);

        tagContent = (EditText) this.findViewById(R.id.tag_content);
        phoneView = (EditText) this.findViewById(R.id.username);
        passwordView = (EditText) this.findViewById(R.id.password);
        confirmPasswordView = (EditText) this.findViewById(R.id.confirm_password);
        nicknameView = (EditText) this.findViewById(R.id.nickname);
        progressBar = (ProgressBar) this.findViewById(R.id.progress);

        tagPool = (TagPool) this.findViewById(R.id.tag_container);
        attemptRecommend();

        Button addButton = (Button) this.findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tagContent.getText().toString().length() <= 0)
                    return;
                TagBar tagBar = new TagBar(RegisterActivity.this);
                tagBar.setParent(tagPool);
                tagBar.setText(tagContent.getText().toString());
                tagBar.setSelected(true);
                tagPool.addView(tagBar);
                tagContent.setText("");
            }
        });

        Button changeButton = (Button) this.findViewById(R.id.change);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRecommend();
            }
        });
        changeButton.performClick();

        gestureDetector = new GestureDetector(this, this);
        LinearLayout layout = (LinearLayout) this.findViewById(R.id.register_activity_layout);
        layout.setOnTouchListener(this);
        layout.setLongClickable(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem submit = menu.add(0, 1, 0, "提交");
        submit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            attemptRegister();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finishWithoutUserId();
        }
        return false;
    }

    private void attemptRegister() {
        if (registerTask != null)
            return;

        phoneView.setError(null);
        passwordView.setError(null);
        confirmPasswordView.setError(null);

        String phone = phoneView.getText().toString();
        String password = passwordView.getText().toString();
        String confirmPassword = confirmPasswordView.getText().toString();
        String sex = boyCheck.isChecked()? "0" : "1";
        String nickname = nicknameView.getText().toString();
        String age = String.valueOf(ageSpinner.getSelectedItemPosition());
        String job = String.valueOf(jobSpinner.getSelectedItemPosition());
        String hometown = String.valueOf(homeSpinner.getSelectedItemPosition());
        String income = String.valueOf(incomeSpinner.getSelectedItemPosition());
        String tags = tagPool.toString();

        boolean cancel = false;
        View focusView = null;

        if (phone.length() != 11) {
            phoneView.setError("手机号位数错误");
            focusView = phoneView;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            passwordView.setError("密码不能为空");
            focusView = passwordView;
            cancel = true;
        } else if (password.length() < 4) {
            passwordView.setError("密码长度不能小于四位");
            focusView = passwordView;
            cancel = true;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordView.setError("密码不一致");
            focusView = confirmPasswordView;
            cancel = true;
        }

        if (cancel) {
            tabs.setCurrentTab(0);
            focusView.requestFocus();
        } else {
//            registerTask = new RegisterTask();
//            registerTask.execute(phone, password, tags);
//            progressBar.setVisibility(View.VISIBLE);
            System.out.println(phone);
            System.out.println(password);
            System.out.println(sex);
            System.out.println(nickname);
            System.out.println(age);
            System.out.println(job);
            System.out.println(hometown);
            System.out.println(income);
            System.out.println(tags);
        }
    }

    private void attemptRecommend() {
        if (recommendTask != null || registerTask != null)
            return;

        recommendTask = new RecommendTask();
        recommendTask.execute();
        progressBar.setVisibility(View.VISIBLE);
    }

    public class RegisterTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {
            String url = RegisterActivity.this.getString(R.string.register_head);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobilePhone", args[0]));
            params.add(new BasicNameValuePair("password", args[1]));
            params.add(new BasicNameValuePair("tags", args[2]));
            RegisterResult res = new JsonGetter<RegisterResult>().getFromUrl(url, RegisterResult.class, params);
            if (res == null)
                return "fail";
            if (res.getState().equals("success"))
                return String.valueOf(res.getUserId());
            else
                return "duplicate";
        }

        @Override
        protected void onPostExecute(final String retCode) {
            registerTask = null;
            progressBar.setVisibility(View.INVISIBLE);
            System.out.println(retCode);

            if (retCode.equals("fail"))
                Toast.makeText(RegisterActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            else if (retCode.equals("duplicate")) {
                phoneView.setError("用户名已被注册");
                phoneView.requestFocus();
            } else {
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                finishWithUserId(retCode);
            }
        }

        @Override
        protected void onCancelled() {
            registerTask = null;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public class RecommendTask extends AsyncTask<Void, Void, List<TagBar>> {

        @Override
        protected List<TagBar> doInBackground(Void... voids) {
            List<TagBar> res = new ArrayList<TagBar>();
            TagBar bar1 = new TagBar(RegisterActivity.this);
            bar1.setParent(tagPool);
            bar1.setText("苹果");
            bar1.setSelected(false);
            res.add(bar1);
            TagBar bar2 = new TagBar(RegisterActivity.this);
            bar2.setParent(tagPool);
            bar2.setText("西瓜");
            bar2.setSelected(false);
            res.add(bar2);
            TagBar bar3 = new TagBar(RegisterActivity.this);
            bar3.setParent(tagPool);
            bar3.setText("香蕉");
            bar3.setSelected(false);
            res.add(bar3);
            TagBar bar4 = new TagBar(RegisterActivity.this);
            bar4.setParent(tagPool);
            bar4.setText("梨子");
            bar4.setSelected(false);
            res.add(bar4);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(List<TagBar> tagBars) {
            recommendTask = null;
            progressBar.setVisibility(View.INVISIBLE);

            tagPool.removeUnselected();
            for (TagBar bar : tagBars)
                tagPool.addView(bar);
        }

        @Override
        protected void onCancelled() {
            recommendTask = null;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void finishWithUserId(String userId) {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("user_id", userId);
        resultIntent.putExtras(bundle);
        this.setResult(RESULT_OK, resultIntent);
        this.finish();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
    }

    private void finishWithoutUserId() {
        this.setResult(RESULT_CANCELED, null);
        this.finish();
        overridePendingTransition(R.anim.in_from_left_direct, R.anim.out_of_right_direct);
    }

    // 滑动改变屏幕
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        if (motionEvent2.getX() - motionEvent.getX() > 50 && Math.abs(v) > 0) {
            int next = (tabs.getCurrentTab() + 2) % 3;
            tabs.setCurrentTab(next);
        } else if (motionEvent.getX() - motionEvent2.getX() > 50 && Math.abs(v) > 0) {
            int next = (tabs.getCurrentTab() + 1) % 3;
            tabs.setCurrentTab(next);
        }

            return false;
    }
}