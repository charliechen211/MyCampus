package com.stpi.campus.activity.personalService;

import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.stpi.campus.activity.CampusApplication;
import com.stpi.campus.items.distance.Distances;
import com.stpi.campus.items.search.SearchResultInfo;
import com.stpi.campus.util.JsonGetter;
import com.stpi.campus.R;
import com.stpi.campus.activity.RefreshableFragment;
import com.stpi.campus.activity.merchant.MerchantDetailActivity;
import com.stpi.campus.items.search.SearchResultHelper;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cyc on 13-12-28.
 */
public class NearbyMerchantFragment extends RefreshableFragment {

    private View view = null;

    private ListView merchantListView = null;
    private Spinner distanceSpinner = null;
    private TextView positionView = null;
    private ProgressBar progressBar = null;
    private SearchMerchantTask mTask = null;

    private LocationManager lm = null;
    private LocationClient locationClient = null;

    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    private SimpleAdapter adapter = null;
    private int pageId = 0;
    private int pageSize = 5;
    private boolean have_more = true;
    private List<SearchResultInfo> merchants = new ArrayList<SearchResultInfo>();

    private String picture_head = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_nearby_merchant, container, false);

        picture_head = this.getString(R.string.shop_picture_head);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, Distances.distanceStrs);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distanceSpinner = (Spinner) view.findViewById(R.id.distance_spinner);
        distanceSpinner.setAdapter(arrayAdapter);
        distanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pageId = 0;
                have_more = true;
                data.clear();
                merchants.clear();
                startTask();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        merchantListView = (ListView) view.findViewById(R.id.merchant_list);
        adapter = new SimpleAdapter(this.getActivity(), data, R.layout.nearby_merchant_list_item,
                new String[]{"name", "rate", "address", "phone", "price", "distance", "picture"},
                new int[]{R.id.merchant_name, R.id.merchant_ratting, R.id.merchant_address, R.id.merchant_phone, R.id.merchant_price, R.id.merchant_distance, R.id.merchant_picture});
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if ((view instanceof RatingBar) && (data instanceof Float)) {
                    RatingBar rb = (RatingBar) view;
                    Float r = (Float) data;
                    rb.setRating(r);
                    return true;
                } else if ((view instanceof ImageView) && (data instanceof String)) {
                    ImageView imageView = (ImageView) view;
                    String imageUri = (String) data;
                    if (imageUri.length() <= 0)
                        return true;
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().build();
                    imageLoader.displayImage(imageUri, imageView, options);
                    return true;
                }
                return false;
            }
        });
        merchantListView.setAdapter(adapter);
        merchantListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int lastItem = 0;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (lastItem == data.size() && scrollState == SCROLL_STATE_IDLE) {
                    if (have_more) {
                        pageId++;
                        startTask();
                    } else
                        Toast.makeText(NearbyMerchantFragment.this.getActivity(), "已到末尾", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount;
            }
        });
        merchantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(NearbyMerchantFragment.this.getActivity(),
                        MerchantDetailActivity.class);
                intent.putExtra("shop_id", String.valueOf(merchants.get(i).getMerchantId()));
//                intent.putExtra("shop_picture", picture_head + merchants.get(i).getPicture());
                startActivity(intent);
            }
        });

        positionView = (TextView) view.findViewById(R.id.position);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationClient != null && locationClient.isStarted()) {
            locationClient.stop();
            locationClient = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void startTask() {
        if (mTask != null)
            return;
        mTask = new SearchMerchantTask();
//        mTask.execute(String.valueOf(121.439813), String.valueOf(31.194009), String.valueOf(pageId), String.valueOf(pageSize), String.valueOf(Distances.distances[distanceSpinner.getSelectedItemPosition()]/1000.0));
        mTask.execute(String.valueOf(121.43564), String.valueOf(31.19076), String.valueOf(pageId), String.valueOf(pageSize), String.valueOf(Distances.distances[distanceSpinner.getSelectedItemPosition()] / 1000.0));
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void refresh() {
        LoadLocation();
        startTask();
    }

    private void LoadLocation() {

        CampusApplication app = (CampusApplication) NearbyMerchantFragment.this.getActivity().getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(NearbyMerchantFragment.this.getActivity());
            app.mBMapManager.init(CampusApplication.strKey, new CampusApplication.MyGeneralListener());
        }

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setAddrType("all");//返回的定位结果包含地址信息
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.disableCache(true);//禁止启用缓存定位
        option.setPoiNumber(5);    //最多返回POI个数
        option.setPoiDistance(1000); //poi查询距离
        option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息
        option.setPriority(LocationClientOption.NetWorkFirst);

        locationClient = new LocationClient(NearbyMerchantFragment.this.getActivity().getApplicationContext());
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(new BDLocationListener() {

            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location == null) {
                    return;
                }
                showLocation(location);
            }

            @Override
            public void onReceivePoi(BDLocation location) {
                System.out.println("baidu");
                if (location == null) {
                    return;
                }
                showLocation(location);
            }

        });
        locationClient.start();
        System.out.println("-=-=-=-=-=-");
        System.out.println(locationClient.requestLocation());
        System.out.println(locationClient.requestPoi());
    }

    private void showLocation(BDLocation location) {
        StringBuilder sb = new StringBuilder();
//        sb.append("Lat:").append(location.getLatitude()).append(" Lng:").append(location.getLongitude()).append('\n');
        if (location.getAddrStr() != null)
            sb.append(location.getAddrStr());
        positionView.setText(sb.toString());
    }

    class SearchMerchantTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... args) {
            String url = null;
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            url = NearbyMerchantFragment.this.getString(R.string.search_by_distance_head);
            params.add(new BasicNameValuePair("longitude", args[0]));
            params.add(new BasicNameValuePair("latitude", args[1]));
            params.add(new BasicNameValuePair("pageId", args[2]));
            params.add(new BasicNameValuePair("pageSize", args[3]));
            params.add(new BasicNameValuePair("distance", args[4]));

            SearchResultHelper tmp = new JsonGetter<SearchResultHelper>().getFromUrl(url, SearchResultHelper.class, params);

            if (tmp == null || tmp.getState().equals("fail"))
                return false;
            for (SearchResultInfo info : tmp.getResults()) {
                Map<String, Object> line = new HashMap<String, Object>();
                if (info.getAddress() != null)
                    line.put("address", "地址：" + info.getAddress());
                else
                    line.put("address", "地址： ");
                line.put("price", "人均：" + ((info.getAverageConsume() < 0) ? "无" : info.getAverageConsume() + "元"));
                line.put("rate", Float.valueOf(info.getAverageValue()));
                if (info.getMerchantName() != null)
                    line.put("name", info.getMerchantName());
                else
                    line.put("name", "");
                if (info.getTelNumber() != null)
                    line.put("phone", "电话：" + info.getTelNumber());
                else
                    line.put("phone", "电话：");
                if (info.getPicture() != null && info.getPicture().length() > 0)
                    line.put("picture", picture_head + info.getPicture());
                else
                    line.put("picture", picture_head + "000000.jpg");
                DecimalFormat df = new DecimalFormat("###.0");
                if (info.getDistance() < 1) {
                    long distance = Math.round(info.getDistance() * 1000);
                    line.put("distance", String.valueOf(distance) + "米");
                } else {
                    Double distance = new Double(0);
                    distance = Double.valueOf(df.format(info.getDistance()));
                    line.put("distance", String.valueOf(distance) + "千米");
                }

                data.add(line);
                merchants.add(info);
            }
            have_more = (tmp.getResults().size() > 0);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean cond) {
            mTask = null;
            progressBar.setVisibility(View.INVISIBLE);

            if (!cond) {
                Toast.makeText(NearbyMerchantFragment.this.getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
                return;
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled() {
            mTask = null;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}