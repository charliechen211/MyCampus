package com.stpi.campus.activity.campusNearby.transportation;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.stpi.campus.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lyl on 2014/11/16.
 */
public class SubwayFragment extends Fragment {

    private ViewPager viewPager;
    private PagerTabStrip pagerTabStrip;
    private View view1, view2, view3, view4,view5,view6;
    private List<View> views = new ArrayList<View>();
    private List<String> stopTitles = new ArrayList<String>();
    private Map<Integer, Integer> subwayStop = new HashMap<Integer,Integer>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transportation_bus,container,false);
        viewPager = (ViewPager) view.findViewById(R.id.busViewPager);
        pagerTabStrip = (PagerTabStrip) view.findViewById(R.id.tabTitle);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.choose_tab_fold));
        pagerTabStrip.setDrawFullUnderline(true);
        view1 = inflater.inflate(R.layout.subway_5, null);
        view2 = inflater.inflate(R.layout.subway_5,null);
        view3 = inflater.inflate(R.layout.subway_1,null);
        view4 = inflater.inflate(R.layout.subway_9,null);
        view5 = inflater.inflate(R.layout.subway_10,null);
        view6 = inflater.inflate(R.layout.subway_11,null);


        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);
        views.add(view5);
        views.add(view6);
        initSubwayStop();
        viewPager.setAdapter(new MyViewAdapter());
        return view;
    }
    public class MyViewAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public  Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position), 0);
            return views.get(position);
        }

        @Override
        public  int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Drawable drawable = getResources().getDrawable(subwayStop.get(position));
            drawable.setBounds(0,0,300,100);
            SpannableString spannableString = new SpannableString(stopTitles.get(position));
            ImageSpan imageSpan = new ImageSpan(drawable,ImageSpan.ALIGN_BASELINE);
            spannableString.setSpan(imageSpan,0,stopTitles.get(position).length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
    }
    public  void  initSubwayStop() {
        stopTitles.add("东川路站");
        stopTitles.add("剑川路站");
        stopTitles.add("徐家汇站");
        stopTitles.add("徐家汇站");
        stopTitles.add("交通大学站");
        stopTitles.add("交通大学站");

        subwayStop.put(0,R.drawable.dongchuan_5);
        subwayStop.put(1,R.drawable.jianchuan_5);
        subwayStop.put(2,R.drawable.xujiahui_1);
        subwayStop.put(3,R.drawable.xujiahui_9);
        subwayStop.put(4,R.drawable.sjtu_10);
        subwayStop.put(5,R.drawable.sjtu_11);

    }
}