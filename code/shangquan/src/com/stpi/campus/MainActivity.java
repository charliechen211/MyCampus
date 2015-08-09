package com.stpi.campus;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.stpi.campus.R;
import com.stpi.campus.activity.RefreshableFragment;
import com.stpi.campus.activity.personSetting.LoginActivity;
import com.stpi.campus.items.Fragments;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.task.LoadUserInfoTask;
import com.stpi.campus.util.ImageFileUtils;
import com.stpi.campus.util.UtilTaskCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    public final static String ITEM_MESSAGE = "com.stpi.shangquan.ITEM_MESSAGE";
    private SlidingMenu leftMenu;
    private TabHost tabHost;
    private long mExitTime = 0;
    private TextView fragmentTitle = null;
    private Menu menu;

    private ImageView leftMenuImage = null;
    private TextView leftMenuUserName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragments);
        showActionBar();

        fragmentTitle = (TextView) findViewById(R.id.fragmentTitle);
        FragmentManager manager = this.getFragmentManager();
        Fragments.fragments = new HashMap<String, RefreshableFragment>();
        Fragments.fragments.put("我的首页", (RefreshableFragment) manager.findFragmentById(R.id.tab_recommend));
        Fragments.fragments.put("校园周边", (RefreshableFragment) manager.findFragmentById(R.id.tab_campusNearby));
        Fragments.fragments.put("校园导航", (RefreshableFragment) manager.findFragmentById(R.id.tab_navigate));
        Fragments.fragments.put("校园资讯", (RefreshableFragment) manager.findFragmentById(R.id.tab_campusNews));
        Fragments.fragments.put("兼职招聘", (RefreshableFragment) manager.findFragmentById(R.id.tab_partTimeJob));
        Fragments.fragments.put("二手市场", (RefreshableFragment) manager.findFragmentById(R.id.tab_shmarket));
        Fragments.fragments.put("鹊桥相会", (RefreshableFragment) manager.findFragmentById(R.id.tab_lvbridge));
        Fragments.fragments.put("互动墙壁", (RefreshableFragment) manager.findFragmentById(R.id.tab_secret));
        Fragments.fragments.put("我的人脉", (RefreshableFragment) manager.findFragmentById(R.id.tab_friends));
        Fragments.fragments.put("个人设置", (RefreshableFragment) manager.findFragmentById(R.id.tab_setting));

        // configure the TabHost
        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec("我的首页").setIndicator("我的首页").setContent(R.id.tab_recommend));
        tabHost.addTab(tabHost.newTabSpec("校园周边").setIndicator("校园周边").setContent(R.id.tab_campusNearby));
        tabHost.addTab(tabHost.newTabSpec("校园导航").setIndicator("校园导航").setContent(R.id.tab_navigate));
        tabHost.addTab(tabHost.newTabSpec("校园资讯").setIndicator("校园资讯").setContent(R.id.tab_campusNews));
        tabHost.addTab(tabHost.newTabSpec("兼职招聘").setIndicator("兼职招聘").setContent(R.id.tab_partTimeJob));
        tabHost.addTab(tabHost.newTabSpec("二手市场").setIndicator("二手市场").setContent(R.id.tab_shmarket));
        tabHost.addTab(tabHost.newTabSpec("鹊桥相会").setIndicator("鹊桥相会").setContent(R.id.tab_lvbridge));
        tabHost.addTab(tabHost.newTabSpec("互动墙壁").setIndicator("互动墙壁").setContent(R.id.tab_secret));
        tabHost.addTab(tabHost.newTabSpec("我的人脉").setIndicator("我的人脉").setContent(R.id.tab_friends));
        tabHost.addTab(tabHost.newTabSpec("个人设置").setIndicator("个人设置").setContent(R.id.tab_setting));

        // configure the left SlidingMenu
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        this.leftMenu = new SlidingMenu(this);
        this.leftMenu.setMode(SlidingMenu.LEFT);
        this.leftMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        this.leftMenu.setShadowWidthRes(R.dimen.shadow_width);
        this.leftMenu.setBehindOffset(size.x * 1 / 3);
        this.leftMenu.setFadeDegree(0.70f);
        this.leftMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        this.leftMenu.setMenu(R.layout.left_sliding_menu);

        leftMenuImage = (ImageView) this.leftMenu.findViewById(R.id.headImage);
        leftMenuUserName = (TextView) this.leftMenu.findViewById(R.id.userName);
        LinearLayout leftMenuHomePage = (LinearLayout) this.leftMenu.findViewById(R.id.homepage);
        leftMenuHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.leftMenu.showContent();
                tabHost.setCurrentTabByTag("我的首页");
                fragmentTitle.setText("");
                menu.clear();
                Fragments.fragments.get("我的首页").refresh();
            }
        });
        ListView leftMenuView = (ListView) this.leftMenu.findViewById(R.id.merchant_recommend_items);

        SimpleAdapter adapter = new SimpleAdapter(this, getData(), R.layout.sliding_menu_item, new String[]{"title", "image"}, new int[]{R.id.item_title, R.id.imageView});
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if ((view instanceof ImageView) && (data instanceof Integer)) {
                    ImageView imageView = (ImageView) view;
                    Integer imageUri = (Integer) data;
                    imageView.setImageResource(imageUri);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    return true;
                }
                return false;
            }
        });
        leftMenuView.setAdapter(adapter);

        leftMenuView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // selected item
                String selected = ((TextView) view.findViewById(R.id.item_title)).getText().toString();

                if (selected != null) {
                    if (UserInfo.userId.equals("0") && selected.equals("我的首页") || UserInfo.userId.equals("0") && selected.equals("我的人脉")) {
                        Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        menu.clear();
                        changeTab(selected);
                    }
                }
            }
        });
        final ImageButton leftButton = (ImageButton) findViewById(R.id.leftButton);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.this.leftMenu.isMenuShowing())
                    MainActivity.this.leftMenu.showContent();
                else
                    MainActivity.this.leftMenu.showMenu();
            }
        });
        /**
         * navigate to this fragment from other page
         */
        String selected = getIntent().getStringExtra("navigation");
        changeTab(selected);
    }

    @Override
    public void onResume() {
        super.onResume();
        reLoadUserInfo();
    }

    private void reLoadUserInfo() {
        new LoadUserInfoTask(MainActivity.this, new UtilTaskCallBack() {
            @Override
            public void taskCallBack() {
                ImageLoader imageLoader = ImageLoader.getInstance();
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .cacheInMemory().cacheOnDisc()
                        .preProcessor(new BitmapProcessor() {
                            @Override
                            public Bitmap process(Bitmap bitmap) {
                                return ImageFileUtils.getRoundCornerImage(bitmap, R.dimen.size25);
                            }
                        })
                        .build();
                imageLoader.displayImage(getString(R.string.person_picture_head) + String.valueOf
                                (UserInfo.userPic),
                        leftMenuImage, options
                );
                leftMenuUserName.setText(UserInfo.userName);
            }
        }).execute(UserInfo.userId);
    }

    private void changeTab(String selected) {
        if(selected == null)
            return;
        MainActivity.this.leftMenu.showContent();
        tabHost.setCurrentTabByTag(selected);
        if(! selected.equals("我的首页"))
            fragmentTitle.setText(selected);
        if (Fragments.fragments.get(selected) != null) {
            Fragments.fragments.get(selected).refresh();
        }
    }

    private void showActionBar() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.custom_actionbar, null);
        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(v);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event
                    .getRepeatCount() == 0) {
                    if (! this.leftMenu.isMenuShowing()) {
                       if ((System.currentTimeMillis() - mExitTime) > 2000) {
                        Toast.makeText(MainActivity.this, "再按一次  退出程序", Toast.LENGTH_SHORT).show();
                        mExitTime = System.currentTimeMillis();
                    } else {
                        int id = android.os.Process.myPid();
                        if (id != 0) {
                            android.os.Process.killProcess(id);
                        }
                    }
                } else {
                    this.leftMenu.showContent();
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (this.leftMenu.isMenuShowing()) {
            this.leftMenu.showContent();
            // } else if (this.rightMenu.isMenuShowing()) {
            // this.rightMenu.showContent();
        } else {
            // this.showExitDialog();
            this.finish();
        }
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String titleArray[] = new String[]{"校园周边", "校园导航", "校园资讯", "兼职招聘", "二手市场", "鹊桥相会", "互动墙壁", "我的人脉", "个人设置"};
        Integer imageArray[] = new Integer[]{
//                R.drawable.stars,
                R.drawable.leftbar_nearby_icon,
                R.drawable.leftbar_navi_icon,
                R.drawable.leftbar_news_icon,
                R.drawable.leftbar_job_icon,
                R.drawable.leftbar_sh_icon,
                R.drawable.leftbar_lv_icon,
                R.drawable.leftbar_wall_icon,
                R.drawable.leftbar_friend_icon,
                R.drawable.leftbar_setting_icon
        };
        int column = titleArray.length;
        for (int i = 0; i < column; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", titleArray[i]);
            map.put("image", imageArray[i]);
            list.add(map);
        }
        return list;
    }

}
