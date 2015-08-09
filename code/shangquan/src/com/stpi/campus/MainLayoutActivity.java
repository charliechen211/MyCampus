package com.stpi.campus;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.Toast;
import com.stpi.campus.activity.personSetting.LoginActivity;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.R;

public class MainLayoutActivity extends TabActivity {
    private long mExitTime = 0;
    private RadioButton mTabHome, mTabWallet, mTabMessage, mTabMember;
    private TabHost mTabHost;
    private String mTagHome = "home", mTagWallet = "wallet", mTagMessage = "message", mTagMember = "member";

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Toast.makeText(MainLayoutActivity.this, "再按一次  退出程序", Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
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

    @SuppressWarnings("deprecation")
    private void init() {
        initControl();
        initEvent();
    }

    @SuppressWarnings("deprecation")
    private void initControl() {
        mTabHome = (RadioButton) findViewById(R.id.btn_navigation_home);
        mTabWallet = (RadioButton) findViewById(R.id.btn_navigation_wallet);
        mTabMessage = (RadioButton) findViewById(R.id.btn_navigation_message);
        mTabMember = (RadioButton) findViewById(R.id.btn_navigation_member);
    }

    private void initEvent() {
        final MyOnPageChangeListener myOnPageChangeListener = new MyOnPageChangeListener();
        mTabHost = getTabHost();

        final Intent intentHome = new Intent(this, HomeActivity.class);
        mTabHost.addTab(mTabHost.newTabSpec(mTagHome).setIndicator(mTagHome).setContent(intentHome));

//		mTabHost.addTab(mTabHost.newTabSpec(mTagWallet).setIndicator(mTagWallet).setContent(intentWallet));

        // mTabHost.addTab(mTabHost.newTabSpec(mTagMessage).setIndicator(mTagMessage).setContent(intentMessage));

        // mTabHost.addTab(mTabHost.newTabSpec(mTagMember).setIndicator(mTagMember).setContent(intentMember));

        mTabHome.setOnClickListener(myOnPageChangeListener);
        mTabWallet.setOnClickListener(myOnPageChangeListener);
        mTabMessage.setOnClickListener(myOnPageChangeListener);
        mTabMember.setOnClickListener(myOnPageChangeListener);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        init();
    }

    private boolean loginChecked() {
        if (UserInfo.userId.equals("0")) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return false;
        }
        return true;
    }

    private class MyOnPageChangeListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_navigation_home:
                    mTabHost.setCurrentTabByTag(mTagHome);
                    break;
                case R.id.btn_navigation_wallet:
                    if (loginChecked()) {
                        final Intent intentWallet = new Intent(MainLayoutActivity.this, MainActivity.class);
                        intentWallet.putExtra("navigation", "我的服务");
                        mTabWallet.getContext().startActivity(intentWallet);
                    }
                    mTabHost.setCurrentTabByTag(mTagWallet);
                    break;
                case R.id.btn_navigation_message:
                    if (loginChecked()) {
                        final Intent intentMessage = new Intent(MainLayoutActivity.this, MainActivity.class);
                        intentMessage.putExtra("navigation", "我的好友");
                        mTabMessage.getContext().startActivity(intentMessage);
                    }
                    mTabHost.setCurrentTabByTag(mTagMessage);
                    break;
                case R.id.btn_navigation_member:
                    if (loginChecked()) {
                        final Intent intentMember = new Intent(MainLayoutActivity.this, MainActivity.class);
                        intentMember.putExtra("navigation", "个人主页");
                        mTabMember.getContext().startActivity(intentMember);
                    }
                    mTabHost.setCurrentTabByTag(mTagMember);
                    break;
            }

        }
    }
}