package com.stpi.campus.activity.campusNearby;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.stpi.campus.R;
import com.stpi.campus.entity.item.ItemInfo;
import com.stpi.campus.entity.item.ItemInfoListHelper;
import com.stpi.campus.util.HttpListGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cyc on 2014/11/8.
 */
public class ItemListActivity extends Activity {

    private ProgressBar progressBar = null;
    private ListView itemListView = null;

    private String item_picture_head = null;

    private SimpleAdapter adapter = null;
    private LoadMerchantTask loadMerchantTask = null;
    private List<ItemInfo> item_infos = new ArrayList<ItemInfo>();
    private List<Map<String, Object>> item_datas = new ArrayList<Map<String, Object>>();
    private boolean have_more = true;
    private int pageId = 0;
    private int pageSize = 5;

    private int objectId = 0;
    private int typeId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_nearby_list);

        item_picture_head = this.getString(R.string.school_picture_head);

        initLoadIntent();
        initIcons();
        initEvents();

        setActionBar();
    }

    public void setActionBar() {

        View view = null;
        switch (typeId) {
            case 3:
                view = LayoutInflater.from(this).inflate(R.layout.actionbar_food_spinner, null);
                break;
            case 4:
                view = LayoutInflater.from(this).inflate(R.layout.actionbar_entertainment_spinner, null);
                break;
            case 5:
                view = LayoutInflater.from(this).inflate(R.layout.actionbar_living_spinner, null);
                break;
        }

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.setSelection(objectId-1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                objectId = position+1;
                have_more = true;
                item_infos.clear();
                item_datas.clear();
                pageId = 0;
                startLoad();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ImageView backHome = (ImageView) view.findViewById(R.id.backHome);
        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemListActivity.this.finish();
            }
        });
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable
                .actionbar_color_campusnearby));
        actionBar.setCustomView(view);
    }

    private void initLoadIntent() {
        typeId = this.getIntent().getIntExtra("typeId", 0);
        objectId = this.getIntent().getIntExtra("objectId", 0);
    }

    private void initIcons() {
        itemListView = (ListView) findViewById(R.id.item_list);
        progressBar = (ProgressBar) findViewById(R.id.progress);
    }

    private void initEvents() {

        adapter = new SimpleAdapter(ItemListActivity.this, item_datas,
                R.layout.layout_item_item, new String[]{"itemName", "itemLocation",
                "itemPic", "itemDescription"}, new int[]{
                R.id.item_name, R.id.item_location, R.id.item_image, R.id.item_description}
        );

        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if ((view instanceof ImageView) && (data instanceof String)) {
                    ImageView imageView = (ImageView) view;
                    String imageUri = (String) data;
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().build();
                    imageLoader.displayImage(imageUri, imageView, options);
                    return true;
                }
                return false;
            }
        });

        itemListView.setAdapter(adapter);

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                Intent intent = new Intent(ItemListActivity.this,
                        ItemDetailActivity.class);
                ItemInfo item = item_infos.get(arg2);
                intent.putExtra("itemDescription", item.getItemDescription() !=null ?
                        item.getItemDescription() : "");
                intent.putExtra("itemId", item.getItemId());
                intent.putExtra("itemLocation", item.getItemLocation() != null ?
                        item.getItemLocation() : "");
                intent.putExtra("itemName", item.getItemName() != null ?
                        item.getItemName() : "");
                if (item.getItemPic() != null)
                    intent.putExtra("itemPic", item_picture_head + item.getItemPic());
                else
                    intent.putExtra("itemPic", item_picture_head + "000000.jpg");
                intent.putExtra("itemPlus", item.getItemPlus() != null ? item.getItemPlus() : "");
                intent.putExtra("itemTel", item.getItemTel() != null ? item.getItemTel() : "");
                if(item.getLatitude() != null) {
                    intent.putExtra("latitude", String.valueOf(item.getLatitude()));
                }
                else
                    intent.putExtra("latitude", "-1");
                if(item.getLongitude() != null) {
                    intent.putExtra("longitude", String.valueOf(item.getLongitude()));
                }
                else
                    intent.putExtra("longitude", "-1");
                intent.putExtra("objectId", item.getObjectId());
                intent.putExtra("typeId", item.getTypeId());
                startActivity(intent);
            }
        });

        itemListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int lastItem = 0;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (lastItem == item_infos.size() && scrollState == SCROLL_STATE_IDLE) {
                    if (have_more) {
                        startLoad();
                    } else
                        Toast.makeText(ItemListActivity.this, "已到末尾", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount;
            }
        });

    }

    private void startLoad() {
        if(loadMerchantTask != null)
            return;

        loadMerchantTask = new LoadMerchantTask();
        progressBar.setVisibility(View.VISIBLE);
        loadMerchantTask.execute(String.valueOf(typeId), String.valueOf(objectId),
                String.valueOf(pageId), String.valueOf(pageSize));
    }

    @Override
    public void onResume() {
        super.onResume();
//        have_more = true;
//        item_infos.clear();
//        item_datas.clear();
//        pageId = 0;
//        startLoad();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class LoadMerchantTask extends
            AsyncTask<String, Integer, List<Map<String, Object>>> {
        @Override
        protected List<Map<String, Object>> doInBackground(String... args) {
            String url = ItemListActivity.this
                    .getString(R.string.fetch_item_list_head);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("typeId", args[0]));
            params.add(new BasicNameValuePair("objectId", args[1]));
            params.add(new BasicNameValuePair("pageId", args[2]));
            params.add(new BasicNameValuePair("pageSize", args[3]));

            ItemInfoListHelper tmp = new HttpListGetter<ItemInfoListHelper>()
                    .getFromUrl(url, ItemInfoListHelper.class, params);

            if (tmp == null || !tmp.getState().equals("success") || tmp.getResults() == null ||
                    tmp.getResults().size() == 0) {
                have_more = false;
                return item_datas;
            }

            List<ItemInfo> newLoadInfos = tmp.getResults();

            pageId += 1;

            for (ItemInfo item : newLoadInfos) {
                Map<String, Object> line = new HashMap<String, Object>();
                if (item.getItemName() != null)
                    line.put("itemName", item.getItemName());
                else
                    line.put("itemName", "");
                if (item.getItemDescription() != null)
                    line.put("itemDescription", item.getItemDescription().length() > 20 ?
                            item.getItemDescription().substring(0, 20) + "..." : "");
                else
                    line.put("itemDescription", "");
                if (item.getItemLocation() != null)
                    line.put("itemLocation", item.getItemLocation());
                else
                    line.put("itemLocation", "");
                if (item.getItemPic() != null && !item.getItemPic().equals(""))
                    line.put("itemPic", item_picture_head + item.getItemPic());
                else
                    line.put("itemPic", item_picture_head + "000000.jpg");

                item_datas.add(line);
                item_infos.add(item);
            }

            return item_datas;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {

            adapter.notifyDataSetChanged();
            loadMerchantTask = null;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}