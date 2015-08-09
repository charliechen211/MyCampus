package com.tang.guangshangquan;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tang.guangshangquan.items.user.LoginResult;
import com.tang.guangshangquan.utils.JsonGetter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {

    private EditText phoneView = null;
    private EditText passwordView = null;

    private ProgressBar progressBar = null;
    private LoginTask loginTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = (ProgressBar) this.findViewById(R.id.progress);
        phoneView = (EditText) this.findViewById(R.id.username);
        passwordView = (EditText) this.findViewById(R.id.password);

        Button registerButton = (Button) this.findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(registerIntent, 0);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
            }
        });

        Button loginButton = (Button) this.findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                attemptLogin();
                startMainPage();
            }
        });
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


    private void startMainPage() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivityForResult(mainIntent, 0);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
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
                return "fail";
            if (res.getState().equals("success"))
                return String.valueOf(res.getUserId());
            else
                return "error";
        }

        @Override
        protected void onPostExecute(String retCode) {
            loginTask = null;
            progressBar.setVisibility(View.INVISIBLE);
            System.out.println(retCode);

            if (retCode.equals("fail"))
                Toast.makeText(LoginActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            else if (retCode.equals("error")) {
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
