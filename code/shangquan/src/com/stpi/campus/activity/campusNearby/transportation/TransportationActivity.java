package com.stpi.campus.activity.campusNearby.transportation;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import com.stpi.campus.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lyl on 2014/11/16.
 */
public class TransportationActivity extends Activity {
    private Map<Integer, Fragment> transportationFragments = new HashMap<Integer, Fragment>();
    private Map<Integer, Fragment> serviceFragments = new HashMap<Integer, Fragment>();
    private Map<Integer, Fragment> currentFragments = new HashMap<Integer, Fragment>();
    private  Integer typeId;
    private Integer objectId;
    private ActionBar actionBar;
    private ImageView backHome;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transportation);
        typeId = getIntent().getIntExtra("typeId",1);
        objectId = getIntent().getIntExtra("objectId", 0);
        actionBar = getActionBar();
        initFragments();
        setActionBar();
        currentFragments = ((typeId ==1) ? transportationFragments : serviceFragments);
        getFragmentManager().beginTransaction().replace(R.id.transportation_layout, currentFragments.get(objectId)).commit();
    }

    public void setActionBar() {
        View view;
        if(typeId == 1)
            view = LayoutInflater.from(this).inflate(R.layout.transportation_spinner, null);
        else
            view = LayoutInflater.from(this).inflate(R.layout.service_spinner, null);

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.setSelection(objectId);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                           TransportationActivity.this.getFragmentManager().beginTransaction().replace(R.id.transportation_layout, currentFragments.get(i)).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        backHome = (ImageView) view.findViewById(R.id.backHome);
        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransportationActivity.this.finish();
            }
        });
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable
                .actionbar_color_campusnearby));
        actionBar.setCustomView(view);
    }

    public void initFragments() {
        transportationFragments.put(0,new SubwayFragment());
        transportationFragments.put(1,new BusFragment());
        transportationFragments.put(2,new CampusBusFragment());
        transportationFragments.put(3,new CrossCampusBusFragment());

        serviceFragments.put(0,new ServiceFragment());
        serviceFragments.put(1,new ServiceFragment());
        serviceFragments.put(2,new ServiceFragment());
        serviceFragments.put(3,new ServiceFragment());
        serviceFragments.put(4,new ServiceFragment());
        serviceFragments.put(5,new ServiceFragment());
    }


}