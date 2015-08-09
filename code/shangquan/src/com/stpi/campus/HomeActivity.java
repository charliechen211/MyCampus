package com.stpi.campus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.stpi.campus.R;
import com.stpi.campus.activity.personSetting.LoginActivity;
import com.stpi.campus.activity.personSetting.SelectSchoolActivity;
import com.stpi.campus.activity.personalService.ChangePresentActivity;
import com.stpi.campus.components.switcher.GuideGallery;
import com.stpi.campus.components.switcher.ImageAdapter;
import com.stpi.campus.items.user.UserInfo;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("HandlerLeak")
public class HomeActivity extends Activity {
    final String KeyPos = "pos";
    final Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            super.handleMessage(message);
            switch (message.what) {
                case 1:
                    mGuideGalleryAd.setSelection(message.getData().getInt(KeyPos));
                    break;
            }
        }
    };
    public GuideGallery mGuideGalleryAd;
    public ImageTimerTask mImgTimerTaskAd = null;
    public boolean mIsTimerFlag = true;

//	private EditText mEditTextContent;
//	private ImageButton mBtnSearch, mBtnWifi;
    // public HashMap<String,Bitmap> imagesCache=new HashMap<String,
    // Bitmap>();//ͼƬ����
    public List<String> mUrls;
    Timer mTimerAd = new Timer();
    Uri mUri;
    //private ImageView mBtnFood, mBtnBuy, mBtnMovie, mBtnKtv, mBtnChild, mBtnActivity, mBtnMap, mBtnIntegral;
    private ImageView mBtnAround, mBtnFood, mBtnInteractive, mBtnLv, mBtnNav, mBtnNews, mBtnSh, mBtnWork;
    private Button mBtnSelect;
    private TextView schoolText;
    private int mPosLast = 0;
    private Thread mThreadAd = null;

    public void changePointView(int posNew) {
        LinearLayout pointLinear = (LinearLayout) findViewById(R.id.gallery_point_linear);
        View viewLast = pointLinear.getChildAt(mPosLast);
        View viewNew = pointLinear.getChildAt(posNew);
        if (viewLast != null && viewNew != null) {
            ImageView imgViewLast = (ImageView) viewLast;
            ImageView imgViewNew = (ImageView) viewNew;

            imgViewLast.setBackgroundResource(R.drawable.feature_point);
            imgViewNew.setBackgroundResource(R.drawable.feature_point_cur);

            mPosLast = posNew;
        }
    }

    private void init() {
        initControl();
        initVar();
        initEvent();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            // 向左滑动
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            // 向右滑动
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initAd() {
        mGuideGalleryAd = (GuideGallery) findViewById(R.id.image_wall_gallery);
        mGuideGalleryAd.setImageActivity(this);
        ImageAdapter imageAdapter = new ImageAdapter(this);
        mGuideGalleryAd.setAdapter(imageAdapter);

        LinearLayout pointLinear = (LinearLayout) findViewById(R.id.gallery_point_linear);
        pointLinear.setBackgroundColor(Color.argb(200, 135, 135, 152));

        for (int i = 0; i < imageAdapter.getCount(); i++) {
            ImageView pointView = new ImageView(this);

            if (i == 0) {
                pointView.setBackgroundResource(R.drawable.feature_point_cur);
            } else {
                pointView.setBackgroundResource(R.drawable.feature_point);
            }
            pointLinear.addView(pointView);
        }
        mGuideGalleryAd.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                System.out.println(arg2 + "arg2");
                startFoodActivity();
            }
        });
    }

    private void initControl() {

        mBtnAround = (ImageView) findViewById(R.id.btn_navigation_around);
        mBtnFood = (ImageView) findViewById(R.id.btn_navigation_food);
        mBtnInteractive = (ImageView) findViewById(R.id.btn_navigation_interactive);
        mBtnLv = (ImageView) findViewById(R.id.btn_navigation_lv);
        mBtnNav = (ImageView) findViewById(R.id.btn_navigation_nav);
        mBtnNews = (ImageView) findViewById(R.id.btn_navigation_news);
        mBtnSh = (ImageView) findViewById(R.id.btn_navigation_sh);
        mBtnWork = (ImageView) findViewById(R.id.btn_navigation_work);
        schoolText = (TextView) findViewById(R.id.school);


//		mEditTextContent = (EditText) findViewById(R.id.edit_text_search_bar_content);
//		mBtnSearch = (ImageButton) findViewById(R.id.btn_search_bar_submit);
//		mBtnWifi = (ImageButton) findViewById(R.id.btn_search_bar_wifi);

    }

    private void initVar() {
        if (mImgTimerTaskAd == null) {
            mImgTimerTaskAd = new ImageTimerTask();
        }
        if (mTimerAd == null) {
            mTimerAd.scheduleAtFixedRate(mImgTimerTaskAd, 5000, 5000);
        }
        if (mThreadAd == null) {
            mThreadAd = new Thread() {
                public void run() {
                    while (isRun()) {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        synchronized (mImgTimerTaskAd) {
                            if (mIsTimerFlag) {
                                mImgTimerTaskAd.isTimerCondition = true;
                                mImgTimerTaskAd.notifyAll();
                            }
                        }
                    }
                }

                ;
            };
        }
    }

    private void initEvent() {

        mBtnAround = (ImageView) findViewById(R.id.btn_navigation_around);
        mBtnFood = (ImageView) findViewById(R.id.btn_navigation_food);
        mBtnInteractive = (ImageView) findViewById(R.id.btn_navigation_interactive);
        mBtnLv = (ImageView) findViewById(R.id.btn_navigation_lv);
        mBtnNav = (ImageView) findViewById(R.id.btn_navigation_nav);
        mBtnNews = (ImageView) findViewById(R.id.btn_navigation_news);
        mBtnSh = (ImageView) findViewById(R.id.btn_navigation_sh);
        mBtnWork = (ImageView) findViewById(R.id.btn_navigation_work);
        mBtnSelect = (Button) findViewById(R.id.selectSchool);

        mBtnAround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAroundActivity();
            }
        });
        mBtnFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFoodActivity();
            }
        });
        mBtnInteractive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startInteractiveActivity();
            }
        });
        mBtnLv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLvActivity();
            }
        });
        mBtnNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNavActivity();
            }
        });
        mBtnNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewsActivity();
            }
        });
        mBtnSh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startShActivity();
            }
        });
        mBtnWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWorkActivity();
            }
        });
        mBtnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSelectActivity();
            }
        });

//		mBtnSearch.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				startSearchActivity();
//			}
//		});
//		mBtnWifi.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				startLoginActivity();
//			}
//		});
    }

    private void startSelectActivity() {
        Intent intent = new Intent(this, SelectSchoolActivity.class);
        startActivity(intent);
    }

//	private void startSearchActivity() {
//		String keyword = mEditTextContent.getText().toString();
//		if (keyword.trim().length() <= 0) {
//			Intent intent = new Intent(this, SearchStartActivity.class);
//			startActivity(intent);
//		} else {
//            Constants.type = "1";
//            Intent intent = new Intent(this, SearchActivity.class);
//			Bundle bundle = new Bundle();
//			bundle.putBoolean("loadSearch", true);
//			bundle.putString("type", "one");
//			bundle.putString("keyword", keyword.trim());
//			bundle.putString("recommented", "0");
//			bundle.putString("id", "");
//			intent.putExtras(bundle);
//			startActivity(intent);
//		}
//	}

	/*
     * }
	 */

    private void startFoodActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("navigation", "今日推荐");
        startActivity(intent);
    }

    private void startAroundActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("navigation", "校园周边");
        startActivity(intent);
    }

    private void startInteractiveActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("navigation", "秘密空间");
        startActivity(intent);
    }

    private void startLvActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("navigation", "鹊桥相会");
        startActivity(intent);
    }

    private void startNavActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("navigation", "校园导航");
        startActivity(intent);
    }

    private void startNewsActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("navigation", "校园资讯");
        startActivity(intent);
    }

    private void startShActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("navigation", "二手市场");
        startActivity(intent);
    }

    private void startWorkActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("navigation", "兼职招聘");
        startActivity(intent);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void startChangePresentActivity() {
        Intent intent = new Intent(this, ChangePresentActivity.class);
        UserInfo.circleId = 1;
        startActivity(intent);
    }

    public boolean isRun() {
        if (this.isFinishing()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        initAd();
        mThreadAd.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        setPlayAd(false);
    }

    @Override
    protected void onResume() {
        // updateBtnContainerHeight();
        super.onResume();
        schoolText.setText(UserInfo.circleName);
        setPlayAd(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setPlayAd(true);
    }

    public void setPlayAd(boolean isTimeCondition) {
        mIsTimerFlag = isTimeCondition;
        mImgTimerTaskAd.isTimerCondition = isTimeCondition;
    }

    private void updateBtnContainerHeight() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_layout_btn_container);
        final ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
        int width = layoutParams.width;

        if (width <= 0) {
            Display currDisplay = getWindowManager().getDefaultDisplay();
            width = currDisplay.getWidth();
        }
        layoutParams.height = width / 4 * 15 / 14 * 2;
    }

    class ImageTimerTask extends TimerTask {
        public volatile boolean isTimerCondition = true;

        // int gallerypisition = 0;
        public void run() {
            synchronized (this) {
                while (!isTimerCondition) {
                    try {
                        wait(100 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                int posNew = mGuideGalleryAd.getSelectedItemPosition() + 1;
                Message msg = new Message();
                Bundle date = new Bundle();
                date.putInt(KeyPos, posNew);
                msg.setData(date);
                msg.what = 1;
                mHandler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
