package com.stpi.campus.activity.campusNearby;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.stpi.campus.activity.personSetting.LoginActivity;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.R;
import com.stpi.campus.task.CollectTask;
import com.stpi.campus.task.SubscribeTask;
import com.stpi.campus.util.navigation.NavigateMapActivity;

/**
 * Created by cyc on 2014/11/8.
 */
public class ItemDetailActivity extends Activity {

    private TextView itemLocationView = null;
    private TextView itemNameView = null;
    private TextView itemPlusView = null;
    private TextView itemTelView = null;
    private ImageView itemPicView = null;
    private ProgressBar progressBar = null;
    private RelativeLayout itemTelFrame = null;
    private RelativeLayout itemLocationFrame = null;

    private String itemLocation = "";
    private String itemName = "";
    private String itemPlus = "";
    private String itemTel = "";
    private String itemPic = null;
    private int itemId = 0;
    private String latitude = "-1";
    private String longitude = "-1";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        initLoadIntent();
        initIcons();
        initEvents();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayUseLogoEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.actionbar_back_icon);
        menu.add(0, 1, 0, "收藏").setIcon(R.drawable.actionbar_collect_icon).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
/*        menu.add(0, 2, 0, "订阅").setIcon(R.drawable.actionbar_subscribe_icon).setShowAsAction
                (MenuItem.SHOW_AS_ACTION_IF_ROOM);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case 1:
                this.collect();
                break;
/*            case 2:
                this.subscribe();
                break;*/
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void collect() {
        loginConfirm();

        CollectTask collectTask = new CollectTask(this, progressBar);
        collectTask.execute(UserInfo.userId, "1", String.valueOf(itemId));
        progressBar.setVisibility(View.VISIBLE);
    }

    private void subscribe() {
        loginConfirm();

        new SubscribeTask(ItemDetailActivity.this, progressBar).execute(UserInfo.userId, String.valueOf(itemId));
        progressBar.setVisibility(View.VISIBLE);
    }

    private void goPhoneCall(String telNum) {
        //用intent启动拨打电话
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telNum));
        startActivity(intent);
    }

    private void loginConfirm() {
        if (UserInfo.userId.equals("0")) {
            Toast.makeText(ItemDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ItemDetailActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void initLoadIntent() {

        if (this.getIntent().getStringExtra("itemLocation") != null)
            itemLocation = this.getIntent().getStringExtra("itemLocation");
        if (this.getIntent().getStringExtra("itemName") != null)
            itemName = this.getIntent().getStringExtra("itemName");
        if (this.getIntent().getStringExtra("itemPlus") != null)
            itemPlus = this.getIntent().getStringExtra("itemPlus");
        if (this.getIntent().getStringExtra("itemTel") != null)
            itemTel = this.getIntent().getStringExtra("itemTel");
        if (this.getIntent().getStringExtra("itemPic") != null)
            itemPic = this.getIntent().getStringExtra("itemPic");
        itemId = this.getIntent().getIntExtra("itemId", 0);
        latitude = this.getIntent().getStringExtra("latitude");
        longitude = this.getIntent().getStringExtra("longitude");

    }

    private void initIcons() {

        itemLocationView = (TextView) findViewById(R.id.itemLocation);
        itemNameView = (TextView) findViewById(R.id.itemName);
        itemPlusView = (TextView) findViewById(R.id.itemPlus);
        itemTelView = (TextView) findViewById(R.id.itemTel);
        itemPicView = (ImageView) findViewById(R.id.itemPic);
        itemLocationFrame = (RelativeLayout) findViewById(R.id.itemLocationFrame);
        itemTelFrame = (RelativeLayout) findViewById(R.id.itemTelFrame);
        progressBar = (ProgressBar) findViewById(R.id.progress);

    }

    private void initEvents() {

        itemLocationView.setText(itemLocation);
        itemNameView.setText(itemName);
        itemPlusView.setText(itemPlus);
        itemTelView.setText(itemTel);

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().build();
        imageLoader.displayImage(itemPic, itemPicView, options);

        itemLocationFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latitude.equals("-1") || longitude.equals("-1")) {
                    Toast.makeText(ItemDetailActivity.this, "服务器无经纬度信息", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(ItemDetailActivity.this, NavigateMapActivity.class);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    startActivity(intent);
                }
            }
        });

        itemTelFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemTel != null || itemTel.equals("")) {
                    goPhoneCall(itemTel);
                }
                else {
                    Toast.makeText(ItemDetailActivity.this, "无效的电话号码", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}