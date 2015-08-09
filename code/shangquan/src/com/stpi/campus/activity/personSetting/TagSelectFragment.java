package com.stpi.campus.activity.personSetting;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.stpi.campus.R;

/**
 * Created by cyc on 2014/11/19.
 */
public class TagSelectFragment extends Fragment {

    private View view = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_tag_select, container, false);
        return view;
    }

    public static Fragment newInstance() {
        Fragment fragment = new TagSelectFragment();
        return fragment;
    }
}