package com.stpi.campus.activity.campusNearby.transportation;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import com.stpi.campus.R;

/**
 * Created by lyl on 2014/11/16.
 */
public class CampusBusFragment extends Fragment {
    private ProgressBar progressBar;

    private WebView crossCampusBus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cross_campus_bus,container,false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        crossCampusBus = (WebView) view.findViewById(R.id.crossCampusBus);
        crossCampusBus.getSettings().setDefaultTextEncodingName("UTF-8");
        progressBar.setVisibility(View.VISIBLE);
        crossCampusBus.loadUrl("http://202.120.40.69:50080/BusinessCircle/innerbus.htm");
        progressBar.setVisibility(View.INVISIBLE);
        return view;
    }
}