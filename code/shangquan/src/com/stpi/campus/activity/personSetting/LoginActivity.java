package com.stpi.campus.activity.personSetting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.stpi.campus.MainActivity;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.util.JsonGetter;
import com.stpi.campus.R;
import com.stpi.campus.items.user.LoginResult;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {

    SharedPreferences settings = null;
    private EditText phoneView = null;
    private EditText passwordView = null;
    private long mExitTime = 0;
    private ProgressBar progressBar = null;
    private LoginTask loginTask = null;
    private Button anonyLogin = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = (ProgressBar) this.findViewById(R.id.progress);
        phoneView = (EditText) this.findViewById(R.id.username);
        passwordView = (EditText) this.findViewById(R.id.password);

        settings = this.getSharedPreferences("TestXML", 0);
        if (!settings.getString("user", "").equals("") && !settings.getString("passwd", "").equals("")) {
            phoneView.setText(settings.getString("user", ""));
            passwordView.setText(settings.getString("passwd", ""));
        }
        Button loginButton = (Button) this.findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        /**
         *
         */
        anonyLogin = (Button) findViewById(R.id.anonyLogin);
        anonyLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMainPage();
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    if (UserInfo.userId.equals("0")) {
                        Toast.makeText(LoginActivity.this, "再按一次  退出程序", Toast.LENGTH_SHORT).show();
                        mExitTime = System.currentTimeMillis();
                    }
                    this.finish();
                } else {
                    int id = android.os.Process.myPid();
                    if (id != 0) {
                        android.os.Process.killProcess(id);
                    }
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void attemptLogin() {
        if (loginTask != null)
            return;

        phoneView.setError(null);
        passwordView.setError(null);

        String phone = phoneView.getText().toString();
        String password = passwordView.getText().toString();

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

        if (cancel) {
            focusView.requestFocus();
        } else {
            loginTask = new LoginTask();
            loginTask.execute(phone, password);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem submit = menu.add(0, 1, 0, "注册");
        submit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivityForResult(registerIntent, 0);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String loginResultUserId = bundle.getString("user_id");
            String loginResultPasswd = bundle.getString("passwd");
            new LoginTask().execute(loginResultUserId, loginResultPasswd);
//            if (loginResult != null) {
//                UserInfo.userId = loginResult;
//                startMainPage();
//            }
        }
    }

    private void startMainPage() {
        if (getIntent().getStringExtra("activity") != null && getIntent().getStringExtra("activity").equals("SlashActivity")) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            if (UserInfo.userId.equals("0")) {
                intent.putExtra("navigation", "校园周边");
            } else {
                intent.putExtra("navigation", "我的首页");
            }
            startActivity(intent);
        }
        this.finish();
    }

    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... args) {
            String url = LoginActivity.this.getString(R.string.login_head);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobilePhone", args[0]));
            params.add(new BasicNameValuePair("password", args[1]));
            LoginResult res = new JsonGetter<LoginResult>().getFromUrl(url, LoginResult.class, params);
            if (res == null)
                return "error";
            if (res.getState().equals("success")) {
                settings = LoginActivity.this.getSharedPreferences("TestXML", 0);
                SharedPreferences.Editor localEditor = settings.edit();
                localEditor.putString("user", args[0]);
                localEditor.putString("passwd", args[1]);
                localEditor.commit();
                UserInfo.userId = String.valueOf(res.getResult().getUserId());
                UserInfo.userName = res.getResult().getUserName();
                UserInfo.fanNum = res.getResult().getFanNum();
                UserInfo.followNum = res.getResult().getFollowNum();
                UserInfo.userPic = res.getResult().getPicture();
                UserInfo.campusName = res.getResult().getSchoolName() + " " + res.getResult().getRegionName();
                return String.valueOf(res.getResult().getUserId());
            } else
                return "fail";
        }

        @Override
        protected void onPostExecute(String retCode) {
            loginTask = null;
            progressBar.setVisibility(View.INVISIBLE);
            System.out.println(retCode);

            if (retCode.equals("error"))
                Toast.makeText(LoginActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            else if (retCode.equals("fail")) {
                Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                startMainPage();
            }
        }

        @Override
        protected void onCancelled() {
            loginTask = null;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}
