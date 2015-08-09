package com.stpi.campus.activity.Navigate;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.stpi.campus.activity.RefreshableFragment;
import com.stpi.campus.entity.navigate.LocationHelper;
import com.stpi.campus.util.navigation.NavigateMapActivity;
import com.stpi.campus.R;
import com.stpi.campus.entity.navigate.Location;
import com.stpi.campus.util.HttpListGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2014/8/14.
 */
public class NavigateFragment extends RefreshableFragment {
    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    private View view;
    private GridView destList;
    private String dest_Pic_Head;
    private Integer pageSize = 6;
    private Integer pageId = 0;
    private ProgressBar progressBar;
    private SearchView searchView;
    private String select = "";
    private Boolean hasMore = true;
    private List<Location> locData = new ArrayList<Location>();
    private SimpleAdapter adapter;
    private Menu menu;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.acitvity_navigate, container, false);
        setHasOptionsMenu(true);
        destList = (GridView) view.findViewById(R.id.destList);
        dest_Pic_Head = NavigateFragment.this.getString(R.string.dest_picture_head);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        destList.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && absListView.getLastVisiblePosition() == (absListView.getCount() - 1)) {
                    if (hasMore) {
                        pageId++;
                        startAsynLoad();
                    } else
                        Toast.makeText(NavigateFragment.this.getActivity(), "已到末尾", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        adapter = new SimpleAdapter(getActivity(), data, R.layout.navigate_list_item, new String[]{"picture"},
                new int[]{ R.id.destImageView});
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if ((view instanceof ImageView) && (data instanceof String)) {
                    ImageView imageView = (ImageView) view;
                    String imageUri = dest_Pic_Head + (String) data;
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
        destList.setAdapter(adapter);

        destList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(NavigateFragment.this.getActivity(), NavigateMapActivity.class);
                intent.putExtra("longitude", String.valueOf(locData.get(arg2).getLocationLongi()));
                intent.putExtra("latitude", String.valueOf(locData.get(arg2).getLocationLati()));
                startActivity(intent);
            }
        });


        return view;
    }

    @Override
    public void refresh() {
        initActionBar();
        pageId = 0;
        select = "";
        data.clear();
        locData.clear();
        startAsynLoad();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
    }

    public void initActionBar() {
        getActivity().getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable
                .actionbar_color_navigation));
        menu.clear();
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.search_view, menu);
        searchView = (SearchView) menu.findItem(R.id.search_view).getActionView();
        initSearchView(searchView);
    }

    private void initSearchView(SearchView sv) {
        int searchImgId = searchView.getContext().getResources().getIdentifier
                ("android:id/search_button",null,null);
        ImageView iView = (ImageView) sv.findViewById(searchImgId);
        iView.setImageDrawable(getResources().getDrawable(R.drawable.actionbar_search_icon));
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                select = s.trim();
                pageId = 0;
                data.clear();
                startAsynLoad();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                select = s.trim();
                pageId = 0;
                data.clear();
                startAsynLoad();
                return false;
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public void startAsynLoad() {
        progressBar.setVisibility(View.VISIBLE);
        new LoadLocationAsynTask().execute(select);
    }

    public class LoadLocationAsynTask extends AsyncTask<String, Integer, List<Location>> {
        Integer dataNum = 0;

        @Override
        protected List<Location> doInBackground(String... params) {
            List<Location> res = new ArrayList<Location>();
            String url = NavigateFragment.this
                    .getString(R.string.navigate_location_head);
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            try {
                param.add(new BasicNameValuePair("select", new String(params[0].getBytes(), "UTF-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            param.add(new BasicNameValuePair("pageId", String.valueOf(pageId)));
            param.add(new BasicNameValuePair("pageSize", String.valueOf(pageSize)));

            LocationHelper locationHelper = new HttpListGetter<LocationHelper>()
                    .getFromUrl(url, LocationHelper.class, param);

            if (locationHelper == null || locationHelper.getState().equals("fail")) {
                return res;
            }
            locData.addAll(locationHelper.getResults());
            res = locationHelper.getResults();
            return res;
        }

        @Override
        protected void onPostExecute(List<Location> res) {
            progressBar.setVisibility(View.INVISIBLE);
            if (res.size() == 0) {
                hasMore = false;
                return;
            }
            for (Location location : res) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("name", location.getLocationName());
                map.put("picture", location.getLocationPic());
                data.add(map);
            }
            hasMore = true;
            adapter.notifyDataSetChanged();
        }
    }

}