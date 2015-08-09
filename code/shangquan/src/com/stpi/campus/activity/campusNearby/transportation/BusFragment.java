package com.stpi.campus.activity.campusNearby.transportation;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.stpi.campus.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyl on 2014/11/18.
 */
public class BusFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transportation_bus,container,false);


        return view;

    }



}