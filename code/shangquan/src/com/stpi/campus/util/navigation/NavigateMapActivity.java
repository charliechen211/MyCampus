package com.stpi.campus.util.navigation;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.search.core.AMapException;
import com.amap.api.search.core.LatLonPoint;
import com.amap.api.search.route.Route;
import com.stpi.campus.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2014/8/16.
 */
public class NavigateMapActivity extends FragmentActivity {
    public static final int ROUTE_SEARCH_RESULT = 2002;
    public static final int ROUTE_SEARCH_ERROR = 2004;
    public static final int MY_LOCATION = 2005;
    private AMap mMap;
    private LatLonPoint startPoint = null;
    private LatLonPoint endPoint = null;
    private ProgressDialog progDialog;
    private List<Route> routeResult;
    private int mode = Route.DrivingDefault;
    private Route route;
    private Messenger uiMessenger;
    private RouteOverlay routeOverlay;
    private Handler routeHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == ROUTE_SEARCH_RESULT) {
                progDialog.dismiss();
                if (routeResult != null && routeResult.size() > 0) {
                    route = routeResult.get(0);
                    if (route != null) {
                        routeOverlay = new RouteOverlay(NavigateMapActivity.this,
                                mMap, route);
                        routeOverlay.removeFormMap();
                        routeOverlay.addMarkerLine();
                        routeNav.setVisibility(View.VISIBLE);
                        routePre.setBackgroundResource(R.drawable.prev_disable);
                        routeNext
                                .setBackgroundResource(R.drawable.btn_route_next);
                    }
                }
            } else if (msg.what == ROUTE_SEARCH_ERROR) {
                progDialog.dismiss();
                showToast((String) msg.obj);
            }
        }
    };
    private LinearLayout routeNav;
    private ImageButton routePre, routeNext;
    private AutonaviLocationListener locListener;
    private LocationManagerProxy mAMapLocManager = null;
    private String longitude;
    private String latitude;
    private HashMap<String, String> locationMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigate_map);
        longitude = getIntent().getStringExtra("longitude");
        latitude = getIntent().getStringExtra("latitude");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable
                .actionbar_color_navigation));
        getActionBar().setDisplayShowHomeEnabled(false);
        ViewGroup home = (ViewGroup) findViewById(android.R.id.home).getParent();
        ((ImageView) home.getChildAt(0))
                .setImageResource(R.drawable.actionbar_back_icon);
        initView();
        setMyLocation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        // 主线程
        locationMap = new HashMap<String, String>();
        uiMessenger = new Messenger(new UIHandler());
        routeNav = (LinearLayout) findViewById(R.id.LinearLayoutLayout_index_bottom);
        routePre = (ImageButton) findViewById(R.id.pre_index);
        routePre.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (routeOverlay != null) {
                    boolean enablePre = routeOverlay.showPrePopInfo();
                    if (!enablePre) {
                        routePre.setBackgroundResource(R.drawable.prev_disable);
                        routeNext
                                .setBackgroundResource(R.drawable.btn_route_next);
                    } else {
                        routePre.setBackgroundResource(R.drawable.btn_route_pre);
                        routeNext
                                .setBackgroundResource(R.drawable.btn_route_next);
                    }
                }
            }
        });
        routeNext = (ImageButton) findViewById(R.id.next_index);
        routeNext.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (routeOverlay != null) {
                    boolean enableNext = routeOverlay.showNextPopInfo();
                    if (!enableNext) {
                        routePre.setBackgroundResource(R.drawable.btn_route_pre);
                        routeNext
                                .setBackgroundResource(R.drawable.next_disable);
                    } else {
                        routePre.setBackgroundResource(R.drawable.btn_route_pre);
                        routeNext
                                .setBackgroundResource(R.drawable.btn_route_next);
                    }
                }
            }
        });
    }

    private void setMyLocation() {
        String myLocalLat = locationMap.get("myLocalLat");
        String myLocalLong = locationMap.get("myLocalLong");

        if (null != myLocalLat && null != myLocalLong) {
            progDialog = ProgressDialog.show(NavigateMapActivity.this, null,
                    "正在获取线路....", true, true);
            startPoint = new LatLonPoint(Double.parseDouble(myLocalLat),
                    Double.parseDouble(myLocalLong));
            endPoint = new LatLonPoint(Double.parseDouble(latitude),
                    Double.parseDouble(longitude));
            searchRouteResult(startPoint, endPoint);
        } else {
            MyLocation();
        }

    }

    private void MyLocation() {
        progDialog = ProgressDialog.show(NavigateMapActivity.this, null, "正在获取线路，请稍等....",
                true, true);
        locListener = new AutonaviLocationListener(uiMessenger, MY_LOCATION);
        mAMapLocManager = LocationManagerProxy.getInstance(this);// 定位
        try {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mAMapLocManager.requestLocationUpdates(
                            LocationProviderProxy.AMapNetwork, 10 * 1000, 10,
                            locListener);
                }
            }, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {
        // progDialog = ProgressDialog.show(MainActivity.this, null,
        // "正在获取线路....",
        // true, true);
        final Route.FromAndTo fromAndTo = new Route.FromAndTo(startPoint,
                endPoint);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    routeResult = Route.calculateRoute(NavigateMapActivity.this,
                            fromAndTo, mode);
                    if (progDialog.isShowing()) {
                        if (routeResult != null || routeResult.size() > 0)
                            routeHandler.sendMessage(Message.obtain(
                                    routeHandler, ROUTE_SEARCH_RESULT));
                    }
                } catch (AMapException e) {
                    Message msg = new Message();
                    msg.what = ROUTE_SEARCH_ERROR;
                    msg.obj = e.getErrorMessage();
                    routeHandler.sendMessage(msg);
                }
            }
        });
        t.start();
    }

    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // mMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星显示
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void showToast(String showString) {
        Toast.makeText(getApplicationContext(), showString, Toast.LENGTH_SHORT)
                .show();
    }

    private class UIHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    // 定位
                    case MY_LOCATION:

                        AMapLocation location = (AMapLocation) msg.obj;

                        Double geoLat = location.getLatitude();
                        Double geoLong = location.getLongitude();
                        locationMap.put("myLocalLat", geoLat + "");
                        locationMap.put("myLocalLong", geoLong + "");

                        startPoint = new LatLonPoint(geoLat, geoLong);
                        endPoint = new LatLonPoint(Double.parseDouble(latitude),
                                Double.parseDouble(longitude));
                        // progDialog.dismiss();
                        searchRouteResult(startPoint, endPoint);
                        break;

                    default:
                        super.handleMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
