package com.stpi.campus.activity.shMarket;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.stpi.campus.entity.sh_lv.ShInfo;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.R;
import com.stpi.campus.activity.RefreshableFragment;
import com.stpi.campus.entity.sh_lv.ShInfoHelper;
import com.stpi.campus.util.HttpListGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cyc on 14-7-27.
 */
public class shInfoFragment extends RefreshableFragment {
    View view = null;
    List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
    private ListView infoList = null;
    private LoadInfoTask mTask = null;
    private int pageId = 0;
    private int pageSize = 5;
    private boolean have_more = true;
    private String common_picture_head = null;
    private ProgressBar progressBar = null;
    private List<ShInfo> data = new ArrayList<ShInfo>();
    private SimpleAdapter adapter = null;

    private Menu menu = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sh_list, container, false);

        setHasOptionsMenu(true);
        common_picture_head = this.getString(R.string.common_picture_head);

        initIcons();
        initEvents();
        startTask();
        return view;
    }

    private void initIcons() {

        infoList = (ListView) view.findViewById(R.id.nearby_news_list);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);

    }

    private void initEvents() {

        infoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShInfo info = data.get(i);
                Intent intent = new Intent(shInfoFragment.this.getActivity(), shCommentActivity.class);
                intent.putExtra("title", info.getShlvinfo().getTitle());
                intent.putExtra("createTime", info.getTimestamp());
                intent.putExtra("picture", info.getShlvinfo().getPicture());
                intent.putExtra("description", info.getShlvinfo().getDescription());
                intent.putExtra("itemId", String.valueOf(info.getShlvinfo().getShId()));
                intent.putExtra("replyNum", String.valueOf(info.getShlvinfo().getCommentNum()));
                intent.putExtra("contact", String.valueOf(info.getShlvinfo().getLinkWay()));
                startActivity(intent);
            }
        });

        infoList.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int lastItem = 0;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (lastItem == data.size() && scrollState == SCROLL_STATE_IDLE) {
                    if (have_more) {
//                        pageId++;
//                        pageId += pageSize;
                        startTask();
                    } else
                        Toast.makeText(shInfoFragment.this.getActivity(), "已到末尾", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount;
            }
        });

        adapter = new SimpleAdapter(shInfoFragment.this.getActivity(), datas, R.layout.sh_info_item,
                new String[]{"picture", "title", "createTime", "replyNum", "description"},
                new int[]{R.id.picture, R.id.title, R.id.createTime, R.id.replyNum, R.id.description});

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
        infoList.setAdapter(adapter);

    }

    private void setActionBarMenu() {
        menu.clear();
        menu.add(0, R.integer.add_sh_item, 0, "发帖").setIcon(R.drawable.actionbar_add_icon).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.integer.add_sh_item:
                this.showAddInfoDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddInfoDialog() {
        Intent intent = new Intent(shInfoFragment.this.getActivity(), shPublishActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        have_more = true;
        datas.clear();
        data.clear();
        pageId = 0;
        new LoadInfoTask().execute(String.valueOf(pageId), String.valueOf(pageSize));
    }

    private void startTask() {
        progressBar.setVisibility(View.VISIBLE);
        if (mTask != null)
            return;
        mTask = new LoadInfoTask();
        mTask.execute(String.valueOf(pageId), String.valueOf(pageSize));
    }

    @Override
    public void refresh() {
        getActivity().getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable
                .actionbar_color_shmarket));
        setActionBarMenu();
        have_more = true;
        datas.clear();
        data.clear();
        pageId = 0;
        new LoadInfoTask().execute(String.valueOf(pageId), String.valueOf(pageSize));
    }

    class LoadInfoTask extends
            AsyncTask<String, Integer, List<Map<String, Object>>> {
        @Override
        protected List<Map<String, Object>> doInBackground(String... args) {

            String url = shInfoFragment.this.getString(R.string.shlv_info_head);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("circleId", String.valueOf(UserInfo.circleId)));
            params.add(new BasicNameValuePair("type", "1"));
            params.add(new BasicNameValuePair("pageId", args[0]));
            params.add(new BasicNameValuePair("pageSize", args[1]));

            ShInfoHelper tmp = new HttpListGetter<ShInfoHelper>()
                    .getFromUrl(url, ShInfoHelper.class, params);

            if (tmp == null || tmp.getState().equals("fail")) {
                have_more = false;
                return datas;
            }
            List<ShInfo> sh_infos = tmp.getResults();

            if (sh_infos == null || sh_infos.size() == 0) {
                have_more = false;
                return datas;
            }

            System.out.println("result get = " + sh_infos.size());
            pageId += sh_infos.size();

            for (ShInfo info : sh_infos) {
                Map<String, Object> line = new HashMap<String, Object>();
                line.put("username", info.getUserName());
                line.put("title", info.getShlvinfo().getTitle());
                line.put("createTime", info.getTimestamp());
                line.put("replyNum", "评论(" + info.getShlvinfo().getCommentNum() + ")");
                if(info.getShlvinfo().getDescription() != null)
                    line.put("description", info.getShlvinfo().getDescription().length() > 20 ?
                            info.getShlvinfo().getDescription().substring(0, 20) + "..." : "");
                else
                    line.put("description", "");
                if (info.getShlvinfo().getPicture() != null && info.getShlvinfo().getPicture().length() > 0)
                    line.put("picture", common_picture_head + info.getShlvinfo().getPicture());
                else
                    line.put("picture", common_picture_head + "000000.jpg");
                datas.add(line);
                data.add(info);
            }

            return datas;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> myData) {
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
            mTask = null;
        }

    }
}