package com.stpi.campus.activity.personalService;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.stpi.campus.R;
import com.stpi.campus.activity.merchant.MerchantDetailActivity;
import com.stpi.campus.activity.otherService.SearchActivity;
import com.stpi.campus.items.search.SearchResultHelper;
import com.stpi.campus.items.search.SearchResultInfo;
import com.stpi.campus.util.JsonGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cyc on 14-6-24.
 */
public class SearchSJTUMerchantActivity extends Activity {

    private ListView merchant_list = null;
    private List<SearchResultInfo> merchants = new ArrayList<SearchResultInfo>();
    private String searchContext = "";

    private ProgressBar progressBar = null;
    private SearchMerchantTask mTask = null;

    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    private SimpleAdapter adapter = null;
    private int pageId = 0;
    private int pageSize = 5;
    private boolean have_more = true;
    private SearchView searchView = null;

    private String picture_head = null;
    private String typeId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_merchant);

        picture_head = this.getString(R.string.shop_picture_head);
        typeId = this.getIntent().getStringExtra("typeId");

        merchant_list = (ListView) this.findViewById(R.id.listView);
        adapter = new SimpleAdapter(
                SearchSJTUMerchantActivity.this, data,
                R.layout.search_merchant_item,
                new String[]{"address", "price", "rate", "name", "phone", "picture"},
                new int[]{R.id.merchant_address, R.id.merchant_price, R.id.merchant_ratting, R.id.merchant_name, R.id.merchant_phone, R.id.merchant_picture});
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
        merchant_list.setAdapter(adapter);
        merchant_list.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int lastItem = 0;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (lastItem == data.size() && scrollState == SCROLL_STATE_IDLE) {
                    if (have_more) {
                        pageId++;
                        startTask();
                    } else
                        Toast.makeText(SearchSJTUMerchantActivity.this, "已到末尾", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount;
            }
        });
        merchant_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchSJTUMerchantActivity.this,
                        MerchantDetailActivity.class);
                intent.putExtra("shop_id", String.valueOf(merchants.get(i).getMerchantId()));
                intent.putExtra("shop_picture", picture_head + merchants.get(i).getPicture());
                startActivity(intent);
            }
        });

        progressBar = (ProgressBar) this.findViewById(R.id.progress);

        searchView = (SearchView) this.findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchContext = s;
                pageId = 0;
                have_more = true;
                data.clear();
                merchants.clear();
                startTask();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.equals("*") || s.trim().length() <= 0) {
                    searchContext = "*";
                    pageId = 0;
                    have_more = true;
                    data.clear();
                    merchants.clear();
                    startTask();
                } else {
                    searchContext = s.trim();
                    pageId = 0;
                    have_more = true;
                    data.clear();
                    merchants.clear();
                    startTask();
                }
                return false;
            }
        });

        searchContext = getIntent().getStringExtra("search");
        startTask();
    }

    private void showActionBar() {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.custom_actionbar, null);
        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(v);
    }

    private void startTask() {
        if (mTask != null)
            return;
        mTask = new SearchMerchantTask();
        mTask.execute(searchContext, String.valueOf(pageId), String.valueOf(pageSize));
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem submit = menu.add(0, 1, 0, "探索");
        submit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            startSearchActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void startSearchActivity() {
        Intent intent = new Intent(SearchSJTUMerchantActivity.this,
                SearchActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("loadSearch", true);
        bundle.putString("type", "one");
        bundle.putString("keyword", "美食");
        bundle.putString("recommented", "0");
        bundle.putString("id", "");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    class SearchMerchantTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... args) {
            String url = null;
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            if (args[0].equals("*")) {
//                url = SearchSJTUMerchantActivity.this.getString(R.string.search_all_sjtu_head);
                url = SearchSJTUMerchantActivity.this.getString(R.string.search_all_head);
                params.add(new BasicNameValuePair("circleId", "2"));
                params.add(new BasicNameValuePair("typeId", typeId));
            } else {
                url = SearchSJTUMerchantActivity.this.getString(R.string.search_head);
//                url = SearchSJTUMerchantActivity.this.getString(R.string.search_sjtu_head);
                params.add(new BasicNameValuePair("circleId", "2"));
                params.add(new BasicNameValuePair("content", args[0]));
                params.add(new BasicNameValuePair("typeId", typeId));
            }
            params.add(new BasicNameValuePair("pageId", args[1]));
            params.add(new BasicNameValuePair("pageSize", args[2]));

            SearchResultHelper tmp = new JsonGetter<SearchResultHelper>().getFromUrl(url, SearchResultHelper.class, params);

            if (tmp == null || tmp.getState().equals("fail"))
                return false;
            for (SearchResultInfo info : tmp.getResults()) {
                Map<String, Object> line = new HashMap<String, Object>();
                line.put("address", "地址：" + info.getAddress());
                line.put("price", "人均：" + ((info.getAverageConsume() < 0) ? "无" : info.getAverageConsume() + "元"));
                line.put("rate", Float.valueOf(info.getAverageValue()));
                line.put("name", info.getMerchantName());
                line.put("phone", "电话：" + info.getTelNumber());
                line.put("picture", picture_head + info.getPicture());
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
                Toast.makeText(SearchSJTUMerchantActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
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