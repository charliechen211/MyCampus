package com.stpi.campus.components.switcher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;
import com.stpi.campus.HomeActivity;

import java.util.TimerTask;

@SuppressWarnings("deprecation")
public class GuideGallery extends Gallery {

    private HomeActivity mImgActivity;

    public GuideGallery(Context context) {
        super(context);
    }

    public GuideGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GuideGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
        System.out.println(this.getSelectedItemPosition());
        return e2.getX() > e1.getX();

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // TODO Auto-generated method stub
        int kEvent;

        if (isScrollingLeft(e1, e2)) { // Check if scrolling left
            kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
        } else { // Otherwise scrolling right
            kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
        }
        onKeyDown(kEvent, null);
        new java.util.Timer().schedule(new TimerTask() {
            public void run() {
                mImgActivity.setPlayAd(true);
                this.cancel();
            }
        }, 3000);
        return true;

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mImgActivity.setPlayAd(false);
        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    public void setImageActivity(HomeActivity iact) {
        mImgActivity = iact;
    }

}
